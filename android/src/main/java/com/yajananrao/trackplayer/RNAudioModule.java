package com.reactnativeaudiodemo;

import android.widget.Toast;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.net.Uri;
import android.content.Intent;
import android.app.Activity;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.content.ComponentName;

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
            Toast.makeText(getReactApplicationContext(), url, Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse(url);
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(uri, null);
        } catch (Exception e) {
            //TODO: handle exception
            Toast.makeText(getReactApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    public void initialize() {
        try {
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
                Toast.makeText(getReactApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                mCurrentState = STATE_PAUSED;
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT: {
                break;
            }
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS: {
                break;
            }
            default: {
                break;
            }
            }
        }
    };

}
