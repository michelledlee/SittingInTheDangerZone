package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private Spinner hoursSpinner;
    private Spinner minutesSpinner;
    private Button beforeTime;
    private Button afterTime;

    private TextView beforeTimeSet;
    private TextView afterTimeSet;

    private int hour = 0;
    private int minute = 0;

    private Calendar calendar;
    private String notificationText = "Time to get on the highway away from the danger zone!";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = preferences.edit();

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
                    enableButtons();
                // Disable buttons if notifications are turned off
                } else {
                    editor.putString("Notifications","Off");
                    Toast.makeText(SettingsActivity.this, "Notifications off", Toast.LENGTH_LONG).show();
                    editor.apply();
                    disableButtons();
                    // Cancel alarm intent as notifications were switched off
                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        });

        // Setting up hours spinner
        hoursSpinner = (Spinner) findViewById(R.id.hours_spinner);
        hoursSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("intervalHours", i);  // Save hours interval for notification reminder
                editor.apply();
                scheduleNotification(getNotification(notificationText));    // Update notification scheduler
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Setting up minutes spinner
        minutesSpinner = (Spinner) findViewById(R.id.minutes_spinner);
        minutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("intervalMinutes", i);    // Save minutes interval for notification reminder
                editor.apply();
                scheduleNotification(getNotification(notificationText));    // Update notification scheduler
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Setting up TextViews that display what time was selected
        beforeTimeSet = (TextView) findViewById(R.id.before_time_set);
        afterTimeSet = (TextView) findViewById(R.id.after_time_set);

        // Setting up time pickers
        beforeTime = (Button) findViewById(R.id.select_time1);
        beforeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            // Save do not disturb "before" time
                            editor.putInt("beforeHours", hourOfDay);
                            editor.apply();
                            editor.putInt("beforeMinutes", minute);
                            editor.apply();
                            // Set TextView to show what time was selected
                            String timeSet = preferences.getInt("beforeHours", -1) + " : " + preferences.getInt("beforeMinutes", -1);
                            beforeTimeSet.setText(timeSet);
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Select time:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        // Setting up time pickers
        afterTime = (Button) findViewById(R.id.select_time2);
        afterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            // Save do not disturb "after" time
                            editor.putInt("afterHours", hourOfDay);
                            editor.apply();
                            editor.putInt("afterMinutes", minute);
                            editor.apply();
                            // Set TextView to show what time was selected
                            String timeSet = preferences.getInt("afterHours", -1) + " : " + preferences.getInt("afterMinutes", -1);
                            afterTimeSet.setText(timeSet);
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Select time:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
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
    }

    /**
     * Create a notification if notifications are scheduled
     * @param content details of the notification
     * @return a notification to display through the alarm
     */
    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    /**
     * Enable buttons if notifications are turned on
     */
    private void enableButtons() {
        hoursSpinner.setEnabled(true);
        hoursSpinner.setClickable(true);
        minutesSpinner.setEnabled(true);
        minutesSpinner.setClickable(true);
        beforeTime.setClickable(true);
        afterTime.setClickable(true);
    }

    /**
     * Disable buttons if notifications are turned off
     */
    private void disableButtons() {
        hoursSpinner.setEnabled(false);
        hoursSpinner.setClickable(false);
        minutesSpinner.setEnabled(false);
        minutesSpinner.setClickable(false);
        beforeTime.setClickable(false);
        afterTime.setClickable(false);
    }
}
