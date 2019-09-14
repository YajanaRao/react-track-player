package com.yajananrao.trackplayer;

import android.widget.SeekBar;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import android.util.Log;
import android.view.ViewGroup;

public class SeekBarViewManager extends SimpleViewManager<SeekBar> {

    public static final String REACT_CLASS = "SeekBar";
    private static final String TAG = "SeekBarViewManager";
    private SeekBar seekBar;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected SeekBar createViewInstance(ThemedReactContext reactContext) {
        Log.i(TAG, "createViewInstance: ");
        if(seekBar == null){
            seekBar = new SeekBar(reactContext);
        }
        try {
            final ViewGroup parentView = (ViewGroup) seekBar.getParent();
            if (parentView != null) {
                parentView.removeView(seekBar);
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "createViewInstance: "+e.toString());
        }
    	return seekBar;
    }

    public SeekBar getSeekBarInstance(){
    	return seekBar;
    }
}
