package com.trackplayer;

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
import com.facebook.react.bridge.ReadableMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import android.os.SystemClock;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;



public class TrackPlayerModule extends ReactContextBaseJavaModule {

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Activity mActivity;
    private ReactContext mContext;
    private TrackPlayerService mService;
    private PlaybackStateCompat mLastPlaybackState;
    private MediaMetadataCompat metadata;

    public Handler handler;

    private boolean connecting = false;
    private boolean playing = false;
    private static final String TAG = "TrackPlayerModule";
    private static int duration = 0;
    private static int position = 0;
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    
    @Override
    public String getName() {
        return "TrackPlayer";
    }

    public TrackPlayerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void initialize() {
        mContext = getReactApplicationContext();
        mActivity = getCurrentActivity();
        Intent intent = mActivity.getIntent();
        mMediaBrowserCompat = new MediaBrowserCompat(mActivity, new ComponentName(mActivity, TrackPlayerService.class),
                mMediaBrowserCompatConnectionCallback, intent.getExtras());
        mMediaBrowserCompat.connect();
        mService = new TrackPlayerService();
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
            case PlaybackStateCompat.STATE_BUFFERING: {
                sendEvent(mContext, "media", "loading");
                playing = false;
                stopSeekbarUpdate();
                break;
            }
            case PlaybackStateCompat.STATE_PLAYING: {
                sendEvent(mContext, "media", "playing");
                playing = true;
                scheduleSeekbarUpdate();
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                sendEvent(mContext, "media", "paused");
                playing = false;
                stopSeekbarUpdate();
                break;
            }
            case PlaybackStateCompat.STATE_STOPPED: {
                sendEvent(mContext, "media", "stopped");
                playing = false;
                stopSeekbarUpdate();
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT: {
                sendEvent(mContext, "media", "skip_to_next");
                playing = false;
                stopSeekbarUpdate();
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS: {
                sendEvent(mContext, "media", "skip_to_previous");
                playing = false;
                stopSeekbarUpdate();
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
                Log.i(TAG, "updateDuration: metadata is null");
                return;
            }
            duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            Log.d(TAG, "updateDuration: "+duration);
        }catch(Exception e){
            Log.e(TAG,"updateDuration: "+e.toString());
        }
       
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown() && playing) {
            metadata = mMediaControllerCompat.getMetadata();
            updateDuration(metadata);
            Log.d(TAG,"seekbar update");
            handler = new Handler();
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            handler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        Log.d(TAG, "stop the update");
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        try{
            if (mLastPlaybackState == null) {
                Log.d(TAG, "nothing to update returing");
                stopSeekbarUpdate();
                return;
            }

            mMediaControllerCompat.getTransportControls().sendCustomAction("ACTION_PROGRESS_UPDATE", null);
            long currentPosition = mLastPlaybackState.getPosition();

            Log.d(TAG, "Current position: "+currentPosition);
            if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING || mLastPlaybackState.getState() == PlaybackStateCompat.STATE_NONE) {
                // Calculate the elapsed time between the last position update and now and unless
                // paused, we can assume (delta * speed) + current position is approximately the
                // latest position. This ensure that we do not repeatedly call the getPlaybackState()
                // on MediaControllerCompat.
                long timeDelta = SystemClock.elapsedRealtime() -
                        mLastPlaybackState.getLastPositionUpdateTime();
                float playbackSpeed = mLastPlaybackState.getPlaybackSpeed();
                Log.d(TAG, "teme delta "+ timeDelta + " playback speed "+ playbackSpeed);
                if(playbackSpeed == 0){
                    Log.d(TAG, "playback speed is null");
                    currentPosition += (int) timeDelta;
                }else {
                    currentPosition += (int) timeDelta * playbackSpeed;
                }
            } 
            position = (int) currentPosition;
        }catch(Exception e){
            Log.e(TAG, "updateProgress: " + e.toString());
        }
    }

    private void sendEvent(ReactContext reactContext, String eventName, String params) {
        try {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "sendEvent: "+e.toString());
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
    public void load(ReadableMap track,final Promise callback) {
        if(track.hasKey("title")){
            Log.d(TAG, "load track "+ track.getString("title"));
        }
        TrackPlayerService.track = track;
        final String path = track.getString("path");
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                if (!path.isEmpty()) {
                    Uri uri = Uri.parse(path);
                    stopSeekbarUpdate();
                    mMediaControllerCompat.getTransportControls().playFromUri(uri, null);
                    metadata = mMediaControllerCompat.getMetadata();
                    updateDuration(metadata);
                    callback.resolve(null);
                }

            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void setup(){
        Runnable r = new Runnable(){
            @Override
            public void run() {
                    metadata = mMediaControllerCompat.getMetadata();
                    scheduleSeekbarUpdate();
            }
        };
        waitForConnection(r);
    }


    @ReactMethod
    public void terminate(){
        Runnable r = new Runnable(){
            @Override
            public void run(){
                stopSeekbarUpdate();
                Log.d(TAG, "terminating seek bar schedular");
            }
        };
        waitForConnection(r);
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
                    stopSeekbarUpdate();
                    mExecutorService.shutdown();
                }
            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void getDuration(Promise promise) {
        try{
            promise.resolve(duration);
        }  catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "getDuration: " + e.toString());
            promise.reject("getDuration" ,e);
        }
    }

    @ReactMethod
    public void getPosition(Promise promise) {
        try{
            promise.resolve(position);
        }  catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "getPosition: " + e.toString());
            promise.reject("getPosition", e);
        }
    }
}
