package com.yajananrao.mediaplayer;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import static com.yajananrao.mediaplayer.MediaPlayerService.CHANNEL_ID;

public class MediaStyleHelper {

    private static String TAG = "MediaStyleHelper";
    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of {@link MediaMetadataCompat#getDescription()} to extract the appropriate information.
     * @param context Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    public static NotificationCompat.Builder from(
            Context context, MediaSessionCompat mediaSession) {

        try {

            Log.i(TAG, "from: Building notification");
            MediaControllerCompat controller = mediaSession.getController();
            MediaMetadataCompat mediaMetadata = controller.getMetadata();
            if(mediaMetadata !=null) {
                MediaDescriptionCompat description = mediaMetadata.getDescription();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                builder
                        .setContentTitle(description.getTitle())
                        .setContentText(description.getSubtitle())
                        .setLargeIcon(description.getIconBitmap())
                        .setContentIntent(controller.getSessionActivity())
                        .setSmallIcon(R.drawable.ic_audiotrack)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDeleteIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSession.getSessionToken())
                                .setShowActionsInCompactView(1)
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                        PlaybackStateCompat.ACTION_STOP))
                        );
                return builder;
            }
            return null;

        }catch (Exception exp){
            Log.e(TAG, "from: "+exp.toString() );
            return null;
        }
    }
}