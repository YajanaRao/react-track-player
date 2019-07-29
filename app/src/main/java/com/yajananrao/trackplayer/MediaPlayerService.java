package com.yajananrao.trackplayer;

import android.app.NotificationChannel;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class MediaPlayerService {
    public void createNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "react-native-music-control")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Hi")
                .setContentText("Hello guys what's up")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
