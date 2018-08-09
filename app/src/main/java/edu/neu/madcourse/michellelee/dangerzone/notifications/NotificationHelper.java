package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {

    public static int ALARM_TYPE_ELAPSED = 101;
    private static AlarmManager alarmManagerElapsed;
    private static PendingIntent alarmIntentElapsed;

    /***
     * Schedules a repeating notification based on the relative time since the device was booted up.
     * Used an inexact notification as we do not want to wake up the device. 30 minute interval is the default.
     *
     * @param context activity from which the function was called
     */
    public static void scheduleRepeatingElapsedNotification30(Context context) {

        // Setting intent to class where notification will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        // Daily inexact alarm from phone boot - current set to test for 10 seconds
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000 * 30, alarmIntentElapsed);
    }

    /***
     * Schedules a repeating notification based on the relative time since the device was booted up.
     * Used an inexact notification as we do not want to wake up the device. 15 minute interval.
     *
     * @param context activity from which the function was called
     */
    public static void scheduleRepeatingElapsedNotification15(Context context) {


        // Setting intent to class where notification will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        // Daily inexact alarm from phone boot - current set to test for 10 seconds
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000 * 15, alarmIntentElapsed);
    }

    /***
     * Schedules a repeating notification based on the relative time since the device was booted up.
     * Used an inexact notification as we do not want to wake up the device. 45 minute interval.
     *
     * @param context activity from which the function was called
     */
    public static void scheduleRepeatingElapsedNotification45(Context context) {


        // Setting intent to class where notification will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        // Daily inexact alarm from phone boot - current set to test for 10 seconds
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000 * 45, alarmIntentElapsed);
    }

    /***
     * Schedules a repeating notification based on the relative time since the device was booted up.
     * Used an inexact notification as we do not want to wake up the device. 60 minute interval.
     *
     * @param context activity from which the function was called
     */
    public static void scheduleRepeatingElapsedNotification60(Context context) {


        // Setting intent to class where notification will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        // Daily inexact alarm from phone boot - current set to test for 10 seconds
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000 * 60, alarmIntentElapsed);
    }

    /***
     * Schedules a repeating notification with interval of 1 minute based on the relative time since the device was booted up.
     * Used an inexact notification as we do not want to wake up the device.
     *
     * @param context activity from which the function was called
     */
    public static void scheduleRepeatingElapsedNotification1(Context context) {

        // Setting intent to class where notification will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        // Daily inexact alarm from phone boot - current set to test for 10 seconds
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000 * 1, alarmIntentElapsed);

    }

    /**
     * Cancel the repeating alarm
     */
    public static void cancelAlarmElapsed() {
        if (alarmManagerElapsed!= null) {
            alarmManagerElapsed.cancel(alarmIntentElapsed);
        }
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
