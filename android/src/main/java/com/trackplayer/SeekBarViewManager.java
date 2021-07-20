package com.trackplayer;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.appcompat.widget.AppCompatSeekBar;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

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

    @ReactProp(name = "thumbTintColor", customType = "Color")
    public void setThumbTintColor(AppCompatSeekBar seekBar, Integer color) {
        Log.d(TAG, "setThumbTintColor: ");
        if (seekBar.getThumb() != null) {
            if (color == null) {
                seekBar.getThumb().clearColorFilter();
            } else {
                seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @ReactProp(name = "trackTintColor", customType = "Color")
    public void setMinimumTrackTintColor(AppCompatSeekBar seekBar, Integer color) {
        if (color == null) {
            seekBar.getProgressDrawable().clearColorFilter();
        } else {
            seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}
