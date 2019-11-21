package com.yajananrao.mediaplayer;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

public class MediaPlayerPackage implements ReactPackage {
    private static final String TAG = "MediaPlayerPackage";

    private SeekBarViewManager seekBarView;

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        if(seekBarView == null){
            Log.i(TAG,"Creating seekbar instance in view manager");
            seekBarView = new SeekBarViewManager();
        }
        Log.i(TAG, "sending seekbar instance to react native");
        return Collections.<ViewManager>singletonList(
            seekBarView
        );
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        if(seekBarView == null){
            Log.i(TAG, "Creating seekbar instance in native module");
            seekBarView = new SeekBarViewManager();
        }

        modules.add(new MediaPlayerModule(reactContext, seekBarView));
        return modules;
    }

}
