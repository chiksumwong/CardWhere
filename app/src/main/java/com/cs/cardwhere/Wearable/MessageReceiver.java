package com.cs.cardwhere.Wearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cs.cardwhere.MainActivity;

public class MessageReceiver extends BroadcastReceiver {

    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MessageReceiver", "on receive");
        String timeElapsed = intent.getStringExtra(MessageService.REPORT_KEY);
        Log.d("MessageReceiver", "time elapsed: " + timeElapsed);

        Intent intentNew = new Intent(context, MainActivity.class);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNew.putExtra(MessageService.REPORT_KEY, timeElapsed);
        context.startActivity(intentNew);
    }
}
