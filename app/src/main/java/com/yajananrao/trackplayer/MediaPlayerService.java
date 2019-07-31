package com.yajananrao.trackplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.os.Binder;

import java.util.HashMap;

public class MediaPlayerService extends Service {

    private static final String TAG = "MyApp";
    private static final int NOTIFICATION_ID = 001;
    private static final String CHANNEL_ID = "com_yajananrao_trackplayer";
    private static final String CHANNEL_NAME = "Track Player";
    private NotificationManager mNotificationManager;
    private NotificationManagerCompat mNotificationManagerCompat;

    private MediaPlayerHolder mPlayer;
    private String song;
    private MediaSessionCompat mediaSession;
    private final IBinder musicBind = new MusicBinder();
    private HashMap metaData;

    private NotificationCompat.Builder mBuilder;

    public static final String MEDIA_TOGGLE = "com.yajananrao.trackplayer.MEDIA_TOGGLE";
    public static final String MEDIA_PREVIOUS = "com.yajananrao.trackplayer.MEDIA_PREVIOUS";
    public static final String MEDIA_NEXT = "com.yajananrao.trackplayer.MEDIA_NEXT";


    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.release();
        return false;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        initMusicPlayer();
        Log.i(TAG, "onCreate: Initialised music player");
    }

    public void setSong(String songUrl){
        song=songUrl;
        Log.i(TAG, "setSong: "+song);
        //play a song
        mPlayer.loadMedia(song);
        metaData = mPlayer.extractMetaData(song);
        String title = (String) metaData.get("title");
        String albumArtist = (String) metaData.get("albumArtist");
        Bitmap artcover = (Bitmap) metaData.get("artcover");
        createNotification(title,albumArtist,artcover);
    }

    public void playSong(){
        mPlayer.play();
    }

    public void pauseSong(){
        mPlayer.pause();
    }

    public void toggle(){
        Log.i(TAG, "toggle: "+mPlayer.isPlaying());
        if(mPlayer.isPlaying()){
            mPlayer.pause();
        }else{
            mPlayer.play();
        }
    }

    public class MusicBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public void createNotificationAction(NotificationCompat.Builder builder){
        Intent toggleIntent = new Intent(this, MainActivity.class);
        toggleIntent.putExtra(MEDIA_TOGGLE,"toggle");

        Intent previousIntent = new Intent(this,MainActivity.class);
        previousIntent.putExtra(MEDIA_PREVIOUS, "previous");

        Intent nextIntent = new Intent(this,MainActivity.class);
        nextIntent.putExtra(MEDIA_NEXT, "next");

        toggleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingToggleIntent = PendingIntent.getActivity(this,001, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the Activity to start in a new, empty task
        previousIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPreviousIntent = PendingIntent.getActivity(this,003, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNextIntent = PendingIntent.getActivity(this,002, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_skip_previous, "Previous", pendingPreviousIntent));

        if(mPlayer.isPlaying()){
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "pause", pendingToggleIntent));
        }else {
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", pendingToggleIntent));
        }

        mBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_skip_next, "Next", pendingNextIntent));
    }

    public void createNotification(String title, String description, Bitmap artcover){
      try{

          Intent appIntent = new Intent(this, MainActivity.class);


          PendingIntent appPendingIntent = PendingIntent.getActivity(this,000,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
          Log.i(TAG, "createNotification: creating Nofification");
          mBuilder = new NotificationCompat.Builder(this, "react-native-music")
                  .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                  .setSmallIcon(R.drawable.ic_play_circle)
                  .setContentTitle(title)
                  .setContentText(description)
                  .setContentIntent(appPendingIntent)
                  .setLargeIcon(artcover)
                  .setChannelId(CHANNEL_ID)
                  .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                  .setPriority(NotificationManager.IMPORTANCE_DEFAULT);
          createNotificationAction(mBuilder);
          mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              Log.i(TAG, "createNotification: In Oreo");
              NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                      CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
              notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
              notificationChannel.setShowBadge(true);
              notificationChannel.setSound(null,null);
              notificationChannel.enableVibration(false);
              mNotificationManager.createNotificationChannel(notificationChannel);
          }
          mNotificationManagerCompat = NotificationManagerCompat.from(this);
          mNotificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
          Log.i(TAG, "createNotification: Notification created");
      }catch (Exception exp){
          Log.i(TAG,exp.toString());
      }
    }

    public void updateNotification(){
        try{
            mBuilder.mActions.clear();
            createNotificationAction(mBuilder);
            mNotificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
        }catch (Exception exp){
            Log.e(TAG, "updateNotification: "+ exp.toString() );
        }
    }

    private void initMusicPlayer(){
        Log.i(TAG, "initMusicPlayer: Initialising music player"+ song);
        mPlayer = new MediaPlayerHolder(this);
        mPlayer.initializeMediaPlayer();
        mediaSession = new MediaSessionCompat(this, TAG, null,null);
    }


    public void cancelNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
