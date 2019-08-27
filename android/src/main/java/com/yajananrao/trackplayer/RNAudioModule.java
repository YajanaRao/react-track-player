package com.yajananrao.trackplayer;

import android.support.v4.media.session.PlaybackStateCompat;
import android.net.Uri;
import android.content.Intent;
import android.app.Activity;
import android.content.ComponentName;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Promise;

import android.os.RemoteException;
import android.util.Log;

public class RNAudioModule extends ReactContextBaseJavaModule {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Activity mActivity;
    private ReactContext mContext;
    private MediaPlayerService mService;

    private boolean connecting = false;
    private String path;
    private static final String TAG = "RNAudioModule";

    @Override
    public String getName() {
        return "RNAudio";
    }

    public RNAudioModule(ReactApplicationContext reactContext) {
        super(reactContext);
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
            switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING: {
                mCurrentState = STATE_PLAYING;
                sendEvent(mContext, "media", "playing");
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                mCurrentState = STATE_PAUSED;
                sendEvent(mContext, "media", "paused");
                break;
            }
            case PlaybackStateCompat.STATE_STOPPED: {
                sendEvent(mContext, "media", "completed");
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
                    MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(uri, null);
                    callback.resolve(null);
                }

            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void play() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().play();
            }
        };
        waitForConnection(r);
    }

    @ReactMethod
    public void pause() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
            }
        };
       waitForConnection(r);
    }

    @ReactMethod
    public void destroy() {
        Runnable r = new Runnable(){
        
            @Override
            public void run() {
                mMediaBrowserCompat.disconnect();
            }
        };
        waitForConnection(r);
    }

}
