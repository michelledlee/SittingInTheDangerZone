package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class AlarmBootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // Only enabling one type of notifications for demo purposes
            NotificationHelper.scheduleRepeatingElapsedNotification(context);
        }
    }
}
