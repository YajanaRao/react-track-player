package com.yajananrao.trackplayer;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.net.Uri;
import android.content.Intent;
import android.app.Activity;
import android.content.ComponentName;


import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.Map;

import java.util.HashMap;

import android.os.RemoteException;
import android.util.Log;

public class RNAudioModule extends ReactContextBaseJavaModule {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;
    private static final String TAG = "MainActivity";

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Activity mActivity;

    @Override
    public String getName() {
        return "Audio";
    }

    public RNAudioModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void load(String url) {
        try {
            if(!url.isEmpty()){
                Uri uri = Uri.parse(url);
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(uri, null);
            }
          
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "Load: "+ e.toString());
        }
    }

    @ReactMethod
    public void play() {
        try {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().play();
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "Play: "+ e.toString());
        }
    }

    @ReactMethod
    public void pause() {
        try {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "Pause: " + e.toString());
        }
    }

    @ReactMethod
    public void destroy(){
        try {
            mMediaBrowserCompat.disconnect();
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "destroy"+ e.toString());
        }
    }

    @Override
    public void initialize() {
        try {
            Log.i(TAG, "initialize: destroy");
            ReactContext context = getReactApplicationContext();
            mActivity = getCurrentActivity();
            Intent intent = mActivity.getIntent();
            mMediaBrowserCompat = new MediaBrowserCompat(mActivity,
                    new ComponentName(mActivity, MediaPlayerService.class), mMediaBrowserCompatConnectionCallback,
                    intent.getExtras());
            mMediaBrowserCompat.connect();
        } catch (Exception e) {
            //TODO: handle exception
            Log.e(TAG, "initialize: "+ e.toString());
        }
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
            Log.i(TAG, "onConnected: connected");
            try {
                mMediaControllerCompat = new MediaControllerCompat(mActivity,
                        mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(mActivity, mMediaControllerCompat);
            } catch (RemoteException e) {
                Log.e(TAG, "onConnected: "+ e.toString());
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
            ReactContext context = getReactApplicationContext();
            switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING: {
                mCurrentState = STATE_PLAYING;
                sendEvent(context, "media", "playing");
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                mCurrentState = STATE_PAUSED;
                sendEvent(context, "media", "paused");
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT: {
                sendEvent(context, "media", "skip_to_next");
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS: {
                sendEvent(context, "media", "skip_to_previous");
                break;
            }
            default: {
                break;
            }
            }
        }
    };

    private void sendEvent(ReactContext reactContext, String eventName, String params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}
