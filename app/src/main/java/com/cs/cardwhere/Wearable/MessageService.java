package com.cs.cardwhere.Wearable;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.cs.cardwhere.Bean.CardBean;
import com.cs.cardwhere.R;

public class MessageService extends IntentService {

    public static final String REPORT_KEY = "REPORT_KEY";
    public static final String INTENT_KEY = "com.cs.cardwhere.wearable.BROADCAST";

    public static CardBean card;

    public MessageService(){
        super("BackgroundCounting");
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d("MessageService", "broadcasted");
        synchronized (this) {
            try {
                wait(1000);

                String CHANNEL_ID = "my_channel_01";
                CharSequence name = "Channel human readable title";// The user-visible name of the channel.

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                        importance);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(card.getCompany())
                                .setContentText(card.getAddress());

                Intent resultIntent = new Intent(this, MessageDisplayActiviey.class);
                resultIntent.putExtra(REPORT_KEY, card.getCompany() + " "+ card.getAddress());

                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                taskStackBuilder.addParentStack(MessageDisplayActiviey.class);
                taskStackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);

                notificationManager.notify(187100915, builder.build());
            } catch (Exception e) {
                Log.d("MessageService", "Broadcast Fail: " + e.getMessage());
            }
        }
    }
}
