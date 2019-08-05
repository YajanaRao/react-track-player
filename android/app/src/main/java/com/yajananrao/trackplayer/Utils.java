package com.yajananrao.trackplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.util.HashMap;

public class Utils {
    private static String TAG = "Utils";

    public HashMap<String, Object> extractMetaData(String resource){
        HashMap<String,Object> metaData = new HashMap<String,Object>();
        try{
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(resource,new HashMap<String, String>());
            metaData.put("title",mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            metaData.put("albumArtist",mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) );
            byte[] imageData = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            metaData.put("artcover",bitmap);


            return metaData;
        }
        catch (Exception exp){
            Log.e(TAG, "extractMetaData: "+ exp.toString());
            return metaData;
        }
    }

}
