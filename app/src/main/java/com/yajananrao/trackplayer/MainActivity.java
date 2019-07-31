package com.yajananrao.trackplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yajananrao.trackplayer.MediaPlayerService.MusicBinder;


// Extends activity and support to action bar
// An Android activity is one screen of the Android app's user interface.

public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String MEDIA_RES = "https://dl.dropboxusercontent.com/s/l36tpowqqlg9ync/03.%20Justin%20Bieber%20-%20Love%20Yourself%28128%29.mp3?dl=0";


    private MediaPlayerService mService;
    private Intent playIntent;
    private boolean mMusicBound=false;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            mService = binder.getService();
            //pass song
            Log.i(TAG, "onServiceConnected: Service connected");
            mService.setSong(MEDIA_RES);
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //Handle intent here...
        // Get the Intent that started this activity and extract the string

        String toggle = intent.getStringExtra(MediaPlayerService.MEDIA_TOGGLE);
        String next = intent.getStringExtra(MediaPlayerService.MEDIA_NEXT);
        String previous = intent.getStringExtra(MediaPlayerService.MEDIA_PREVIOUS);
        if(toggle != null){
            Log.i(TAG, "onNewIntent: pause song");
            mService.toggle();
        }else if(previous != null){
            Log.i(TAG, "onNewIntent: Prevoius song");
        }else if(next != null){
            Log.i(TAG, "onNewIntent: Next song");
        }
        mService.updateNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
//            Starting the MediaPlayerService class execution
            Log.i(TAG, "onStart: Creating the service");
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.i(TAG, "onStart: Service started");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mService=null;
        super.onDestroy();
        Log.i(TAG, "onDestroy: Cleared service");
    }
}