package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import edu.neu.madcourse.michellelee.dangerzone.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private Spinner minutesSpinner;
    private TextView currentInterval;
    private Button submitNotification;
    private int interval;

    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = preferences.edit();

        // Get current interval preferences. The efault is set to 30 minutes since it is recommended
        // to walk approximately 8 minutes per hour. We assume the app will go off at least twice
        // and users can choose their time length to walk.
        interval = preferences.getInt("intervalMinutes", 30);

        // Set the view for the notifications switch depending to user preferences
        final Switch notificationsSwitch = (Switch) findViewById(R.id.notifications_switch);
        // Get the current state of notification preferences
        String switchState = preferences.getString("Notifications", "Off");
        if (switchState.equals("On")) { // If notifications are on, display the switch in its "On" state
            notificationsSwitch.setChecked(true);
        } else {    // If they are turned off, display the switch in its "Off" state
            notificationsSwitch.setChecked(false);
        }

        // Setting up switch function to turn notifications on/off
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Enable buttons for selecting notification settings
                if (notificationsSwitch.isChecked()) {
                    editor.putString("Notifications","On"); // Save the notification preference as on
                    Toast.makeText(SettingsActivity.this, "Notifications on", Toast.LENGTH_LONG).show();    // Let the user know that notifications are turned on
                    editor.apply();

                    // Get the current interval level
                    interval = preferences.getInt("intervalMinutes", 30);

                    // Schedule a repeating notification to walk
                    NotificationHelper.scheduleRepeatingElapsedNotification(mContext);
                    NotificationHelper.enableBootReceiver(mContext);

                // Disable buttons if notifications are turned off
                } else {
                    editor.putString("Notifications","Off"); // Save the notification preference as off
                    Toast.makeText(SettingsActivity.this, "Notifications off", Toast.LENGTH_LONG).show();   // Let the user know that notifications are turned off
                    editor.apply();

                    // Cancel alarm intent as notifications were switched off
                    NotificationHelper.cancelAlarmElapsed();
                    NotificationHelper.disableBootReceiver(mContext);
                }
            }
        });

        // Display what interval the notifications are sent to go off at
        currentInterval = (TextView) findViewById(R.id.current_setting);    // TextView to update
        final String currentSettings = getResources().getString(R.string.current_settings);
        currentInterval.setText(currentSettings + interval);     // Display default setting on startup

        // Setting up minutes spinner
        minutesSpinner = (Spinner) findViewById(R.id.minutes_spinner);
        minutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int minutesSelected = Integer.parseInt(adapterView.getItemAtPosition(i).toString()); // Get user selection from spinner and convert to integer
                editor.putInt("intervalMinutes", minutesSelected);    // Save minutes interval for notification reminder
                editor.apply();
                interval = preferences.getInt("intervalMinutes", 30);
                currentInterval.setText(currentSettings + interval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//
//        // Submitting this changes the interval to which the timer is set
//        submitNotification = (Button) findViewById(R.id.submit_notification);
//        submitNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                scheduleNotification(getNotification(notificationText));
//            }
//        });
    }

//    /**
//     * Enable buttons if notifications are turned on
//     */
//    private void enableButtons() {
//        submitNotification.setClickable(true);
//    }
//
//    /**
//     * Disable buttons if notifications are turned off
//     */
//    private void disableButtons() {
//        submitNotification.setClickable(false);
//    }
}
