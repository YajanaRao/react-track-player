package com.yajananrao.trackplayer;

import android.content.ComponentName;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;


// Extends activity and support to action bar
// An Android activity is one screen of the Android app's user interface.

public final class MainActivity extends AppCompatActivity {

    public static final String MEDIA_RES = "https://dl.dropboxusercontent.com/s/l36tpowqqlg9ync/03.%20Justin%20Bieber%20-%20Love%20Yourself%28128%29.mp3?dl=0";

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;
    private static final String TAG = "MainActivity";

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;

    private Button mPlayPauseToggleButton;
    private SeekBar mSeekbarAudio;

    private boolean mUserIsSeeking = false;

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(MainActivity.this,mMediaControllerCompat);
                buildTransportControls();
            } catch( RemoteException e ) {

            }
        }
    };

    public void load(String url){
        Uri uri = Uri.parse(url);
        MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().playFromUri(uri, null);
    }

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    Log.i(TAG, "onPlaybackStateChanged: Playing");
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    Log.i(TAG, "onPlaybackStateChanged: Paused");
                    mCurrentState = STATE_PAUSED;
                    break;
                }
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT: {
                    Log.i(TAG, "onPlaybackStateChanged: Skip to next");
                    break;
                }
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS: {
                    Log.i(TAG, "onPlaybackStateChanged: Skip to previous");
                    break;
                }
                default:{
                    Log.i(TAG, "onPlaybackStateChanged: "+ state.getState());
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, MediaPlayerService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

    }


    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowserCompat.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(mMediaControllerCompatCallback);
        }
        mMediaBrowserCompat.disconnect();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( MediaControllerCompat.getMediaController(this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(this).getTransportControls().pause();
        }

        mMediaBrowserCompat.disconnect();
    }

    void buildTransportControls(){
        mPlayPauseToggleButton = (Button) findViewById(R.id.button);

        mSeekbarAudio = (SeekBar) findViewById(R.id.progress);

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mCurrentState == STATE_PAUSED ) {
                    try {
                        MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
                        mCurrentState = STATE_PLAYING;
                    }catch (Exception exp){
                        Log.e(TAG, "onClick: "+exp.toString() );
                    }
                } else {
                    load(MEDIA_RES);
                    try {
                        if( MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
                        }
                    }catch (Exception exp){
                        Log.e(TAG, "onClick: "+exp.toString() );
                    }


                    mCurrentState = STATE_PAUSED;
                }
            }
        });

        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().seekTo(userSelectedPosition);
                    }
                });
    }
}