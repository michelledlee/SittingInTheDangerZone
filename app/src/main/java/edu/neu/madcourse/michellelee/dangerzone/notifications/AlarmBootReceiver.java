package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import edu.neu.madcourse.michellelee.dangerzone.notifications.SettingsActivity;

public class AlarmBootReceiver extends BroadcastReceiver {
    SharedPreferences preferences;

    /**
     * Schedules the repeating notification once the app is notified that the device has finished booting
     *
     * @param context the activity from which the notifications are being sent (SettingsActivity)
     * @param intent the action that was received
     */
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // Initialize Shared Preferences
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int interval = preferences.getInt("intervalMinutes", 30);

            switch (interval) {
                case 1: // 1 minute
                    NotificationHelper.scheduleRepeatingElapsedNotification1(context);
                    break;
                case 15: // 15 minutes
                    NotificationHelper.scheduleRepeatingElapsedNotification15(context);
                    break;
                case 30: // 30 minutes
                    NotificationHelper.scheduleRepeatingElapsedNotification30(context);
                    break;
                case 45: // 45 minutes
                    NotificationHelper.scheduleRepeatingElapsedNotification45(context);
                    break;
                case 60: // 60 minutes
                    NotificationHelper.scheduleRepeatingElapsedNotification60(context);
                    break;
                default:
                    break;
            }

            // Enable notification
//            NotificationHelper.scheduleRepeatingElapsedNotification1(context);
        }
    }

}
