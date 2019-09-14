package com.yajananrao.trackplayer;

import android.support.v4.media.session.PlaybackStateCompat;
import android.net.Uri;
import android.content.Intent;
import android.app.Activity;
import android.content.ComponentName;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Promise;

import android.os.SystemClock;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import android.os.RemoteException;
import android.util.Log;

import android.widget.SeekBar;


public class RNAudioModule extends ReactContextBaseJavaModule {

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Activity mActivity;
    private ReactContext mContext;
    private MediaPlayerService mService;
    private PlaybackStateCompat mLastPlaybackState;
    private SeekBar mSeekBar;

    private boolean connecting = false;
    private String path;
    private static final String TAG = "RNAudioModule";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private SeekBarViewManager seekBarManager;
    private MediaMetadataCompat metadata;

    private Handler mHandler;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    public String getName() {
        return "RNAudio";
    }

    public RNAudioModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.i(TAG, "RNAudioModule: seekBar not initialized");
    }


    public RNAudioModule(ReactApplicationContext reactContext, SeekBarViewManager seekBar) {
        super(reactContext);
        seekBarManager = seekBar;
    }

    @Override
    public void initialize() {
        mContext = getReactApplicationContext();
        mActivity = getCurrentActivity();
        Intent intent = mActivity.getIntent();
        mMediaBrowserCompat = new MediaBrowserCompat(mActivity, new ComponentName(mActivity, MediaPlayerService.class),
                mMediaBrowserCompatConnectionCallback, intent.getExtras());
        mMediaBrowserCompat.connect();
        mService = new MediaPlayerService();
    }
    
    @Override
    public void onCatalystInstanceDestroy() {
        try {
            Log.i(TAG, "onCatalystInstanceDestroy: destroy");
            if(mMediaBrowserCompat != null){
                mMediaBrowserCompat.disconnect();
            }
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "onCatalystInstanceDestroy: " + e.toString());
        }
    }

    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(mActivity, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(mActivity, mMediaControllerCompat);
            } catch (RemoteException e) {
                Log.e(TAG, "onConnected: " + e.toString());
            }
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if (state == null) {
                return;
            }
            mLastPlaybackState = state;
            switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING: {
                sendEvent(mContext, "media", "playing");
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                sendEvent(mContext, "media", "paused");
                break;
            }
            case PlaybackStateCompat.STATE_STOPPED: {
                sendEvent(mContext, "media", "completed");
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT: {
                sendEvent(mContext, "media", "skip_to_next");
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS: {
                sendEvent(mContext, "media", "skip_to_previous");
                break;
            }
            default: {
                break;
            }
            }
        }
    };




    private void updateDuration(MediaMetadataCompat metadata) {
        try{
             if (metadata == null) {
                return;
            }

            int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            if(mSeekBar != null){
                Log.d(TAG, "updateDuration: "+duration);
                mSeekBar.setMax(duration);
                updateProgress();
            }
        }catch(Exception e){
            Log.e(TAG,"updateDuration: "+e.toString());
        }
       
    }

    private void updateProgress() {
        try{
            if (mLastPlaybackState == null || mSeekBar == null) {
                return;
            }
            long currentPosition = mLastPlaybackState.getPosition();
            Log.i(TAG, "Current position: "+currentPosition);
            if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                // Calculate the elapsed time between the last position update and now and unless
                // paused, we can assume (delta * speed) + current position is approximately the
                // latest position. This ensure that we do not repeatedly call the getPlaybackState()
                // on MediaControllerCompat.
                long timeDelta = SystemClock.elapsedRealtime() -
                        mLastPlaybackState.getLastPositionUpdateTime();
                float playbackSpeed = mLastPlaybackState.getPlaybackSpeed();
                Log.i(TAG, "teme delta "+ timeDelta + " playback speed "+ playbackSpeed);
                if(playbackSpeed == 0){
                    Log.i(TAG, "playback speed is null");
                    currentPosition += (int) timeDelta;
                }else {
                    currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
                }
            } 
            int position = (int) currentPosition;
            if(mSeekBar != null){
                Log.i(TAG, "updateProgress: "+position);
                mSeekBar.setProgress(position); 
            }

        }catch(Exception e){
            Log.e(TAG, "updateProgress: " + e.toString());
        }
    }

    private void sendEvent(ReactContext reactContext, String eventName, String params) {
        try {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
        } catch (Exception e) {
            //TODO: handle exception
            Log.i(TAG, "sendEvent: "+e.toString());
        }
       
    }

    private void waitForConnection(Runnable r) {
        if (mService != null) {
            mService.post(r);
            return;
        }

        if (connecting){
            return;
        }
       
    }

    @ReactMethod
    public void load(String url,final Promise callback) {
        path = url;
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                if (!path.isEmpty()) {
                    Uri uri = Uri.parse(path);
                    mMediaControllerCompat.getTransportControls().playFromUri(uri, null);
                    callback.resolve(null);
                }

            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void init(){
        Runnable r = new Runnable(){
            @Override
            public void run() {
                mSeekBar = seekBarManager.getSeekBarInstance();
                if(mSeekBar != null){
                    Log.i(TAG, "init: seekBar is not null");
                    metadata = mMediaControllerCompat.getMetadata();
                    updateDuration(metadata);
                    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean fromTouch) {
                            if(fromTouch){
                                Log.i(TAG, "onProgressChanged: "+i);
                                mMediaControllerCompat.getTransportControls().seekTo(i);
                            }
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
//                            int pos = seekBar.getProgress();
//                            Log.i(TAG, "onStopTrackingTouch: "+pos);
//                             mMediaControllerCompat.getTransportControls().seekTo(pos);
                        }
                    });
                }
            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void update(){
        Runnable r = new Runnable(){
            @Override
            public void run(){
                updateProgress();
            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void terminate(){
        Runnable r = new Runnable(){
            @Override
            public void run(){
                if(mSeekBar != null){
                    mSeekBar.setMax(0);
                }
            }
        };
    }

    @ReactMethod
    public void play() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                mMediaControllerCompat.getTransportControls().play();
            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void pause() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                mMediaControllerCompat.getTransportControls().pause();
            }
        };
       waitForConnection(r);
    }

    @ReactMethod
    public void destroy() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                if(mMediaBrowserCompat != null){
                    mMediaBrowserCompat.disconnect();
                }
            }
        };
        waitForConnection(r);
    }

}
