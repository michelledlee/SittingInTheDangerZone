package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import edu.neu.madcourse.michellelee.dangerzone.MainActivity;
import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.WalkIntro;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "sup fucker", Toast.LENGTH_LONG).show();

        // Notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, WalkIntro.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Walk Notification")
                .setContentText("Time to get on the highway away from the danger zone!")
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

//
//        // Intent to invoke app when click on notification.
//        // Start/launch this sample app when user clicks on notification
//        Intent intentToRepeat = new Intent(context, MainActivity.class);
//        // Set flag to restart/relaunch the app
//        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Pending intent to handle launch of Activity in intent above
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Build notification
//        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();
//
//        // Send local notification
//        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Morning Notification")
                        .setAutoCancel(true);

        return builder;
    }

}
