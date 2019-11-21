package com.yajananrao.mediaplayer;

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
            try {
                mmr.setDataSource(resource, new HashMap<String, String>());
            } catch (Exception e) {
                //TODO: handle exception
                Log.e(TAG, "extractMetaData: Try 1"+ resource);
                mmr.setDataSource(resource);
            }
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if(title == null){
                title = "Track";
            }
            metaData.put("title",title);
            
            String albumArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);         
            if(albumArtist == null){
                albumArtist = "Unknown Artist";
            }
            metaData.put("albumArtist", albumArtist);

            try{
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                Log.i(TAG, "duration: "+duration);
                metaData.put("duration", duration);
            } catch (Exception e){
                Log.i(TAG, "extractMetaData: "+ e.toString());
            }

            try {
                byte[] imageData = mmr.getEmbeddedPicture();
                if (imageData != null) {
                    Log.i(TAG, "extractMetaData: got Image");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    metaData.put("artcover", bitmap);
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.i(TAG, "extractMetaData: "+e.toString());
            }
           

            return metaData;
        }
        catch (Exception exp){
            Log.e(TAG, "extractMetaData: Failed "+ exp.toString());
            return metaData;
        }
    }

}
