package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.WalkIntro;

public class AlarmReceiver extends BroadcastReceiver {

    /**
     * Displays a notification once received info from the Alarm Manager
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "sup fucker", Toast.LENGTH_LONG).show();

        // Get the pending intent for the notification. The notification is intended to tell the user when to walk so it
        // will start the Walk activity.
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, WalkIntro.class), PendingIntent.FLAG_UPDATE_CURRENT);   // PendingIntent set to the Walk Introduction screen

        // Build the notification to tell the user it is time to walk again
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)     // Set icon for notification
                .setContentTitle("Walk Notification")   // The app only sends out "Walk Notifications" letting the user know its time to walk based on their time preferences
                .setContentText("Time to get on the highway away from the danger zone!")    // Letting the user know it's time to walk
                .setContentIntent(contentIntent);   // When the notification is pressed, take the user to the Walk Introduction screen

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build()); // Send the notification

    }

}
