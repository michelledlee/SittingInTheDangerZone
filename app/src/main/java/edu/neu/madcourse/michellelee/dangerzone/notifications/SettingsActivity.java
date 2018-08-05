package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import edu.neu.madcourse.michellelee.dangerzone.NotificationPublisher;
import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.WalkIntro;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private Spinner minutesSpinner;
    private TextView currentInterval;
    private Button submitNotification;
    private int interval;

    private Context mContext;

    private Calendar calendar;
    private String notificationText = "Time to get on the highway away from the danger zone!";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();


        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = preferences.edit();

        // Get current interval preferences
        interval = preferences.getInt("intervalMinutes", 30);

        // Setting up switch to turn notifications on/off
        final Switch notificationsSwitch = (Switch) findViewById(R.id.notifications_switch);
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Enable buttons for selecting notification settings
                if (notificationsSwitch.isChecked()) {
                    editor.putString("Notifications","On");
                    Toast.makeText(SettingsActivity.this, "Notifications on", Toast.LENGTH_LONG).show();
                    editor.apply();
                    NotificationHelper.scheduleRepeatingElapsedNotification(mContext);
                    NotificationHelper.enableBootReceiver(mContext);
                    enableButtons();

                // Disable buttons if notifications are turned off
                } else {
                    editor.putString("Notifications","Off");
                    Toast.makeText(SettingsActivity.this, "Notifications off", Toast.LENGTH_LONG).show();
                    editor.apply();
                    disableButtons();
                    // Cancel alarm intent as notifications were switched off
                    NotificationHelper.cancelAlarmElapsed();
                    NotificationHelper.disableBootReceiver(mContext);
                }
            }
        });

        // Setting up minutes spinner
        minutesSpinner = (Spinner) findViewById(R.id.minutes_spinner);
        minutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("intervalMinutes", i);    // Save minutes interval for notification reminder
                editor.apply();
//                scheduleNotification(getNotification(notificationText));    // Update notification scheduler
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Display what interval the notifications are sent to go off at
        currentInterval = (TextView) findViewById(R.id.current_setting);
        String currentSettings = getResources().getString(R.string.current_settings);
        currentInterval.setText(currentSettings + interval);

        submitNotification = (Button) findViewById(R.id.submit_notification);
        submitNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scheduleNotification(getNotification(notificationText));
            }
        });
    }

    /**
     * Schedule a repeating notification based on user specifications
     * @param notification the notification to be sent when the alarm goes off
     */
    private void scheduleNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get time that the alarm notifications are allowed to be sent
        int hourDay = preferences.getInt("beforeHours", -1);
        int minutesDay = preferences.getInt("beforeMinutes", -1);

        // Store in calendar object
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourDay);
        calendar.set(Calendar.MINUTE, minutesDay);

        // Get interval preferences set by user
        int intervalHours = preferences.getInt("intervalMinutes", -1);
        int intervalMinutes = preferences.getInt("intervalMinutes", -1);

        // Calculate delay: hours = 1000 ms * 60 min * 60 sec * hours; minutes = 1000 ms * 60 sec * minutes
        int delay = (1000 * 60 * 60 * intervalHours) + (1000 * 60 * intervalMinutes);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis, delay, pendingIntent);

        // Let the user know that the notification has been set
        Toast.makeText(this,"Notification scheduled!",Toast.LENGTH_SHORT).show();

    }

    /**
     * Create a notification if notifications are scheduled
     * @param content details of the notification
     * @return a notification to display through the alarm
     */
    private Notification getNotification(String content) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, WalkIntro.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(contentIntent);
        return builder.build();
    }

    /**
     * Enable buttons if notifications are turned on
     */
    private void enableButtons() {
        submitNotification.setClickable(true);
    }

    /**
     * Disable buttons if notifications are turned off
     */
    private void disableButtons() {
        submitNotification.setClickable(false);
    }
}
