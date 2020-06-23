package com.yajananrao.mediaplayer;

import androidx.appcompat.widget.AppCompatSeekBar;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import android.util.Log;
import android.view.ViewGroup;

public class SeekBarViewManager extends SimpleViewManager<AppCompatSeekBar> {

    public static final String REACT_CLASS = "SeekBar";
    private static final String TAG = "SeekBarViewManager";
    private static final int STYLE = android.R.attr.seekBarStyle;

    private AppCompatSeekBar seekBar;


    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AppCompatSeekBar createViewInstance(ThemedReactContext reactContext) {
        if(seekBar == null){
            Log.i(TAG, "createViewInstance: ");
            seekBar = new AppCompatSeekBar(reactContext, null, STYLE);
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

    public AppCompatSeekBar getSeekBarInstance(){
    	return seekBar;
    }
}
