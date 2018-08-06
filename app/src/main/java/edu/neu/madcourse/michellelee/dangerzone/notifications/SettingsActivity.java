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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Display what interval the notifications are sent to go off at
        currentInterval = (TextView) findViewById(R.id.current_setting);
        String currentSettings = getResources().getString(R.string.current_settings);
        currentInterval.setText(currentSettings + interval);

        // Submitting this changes the interval to which the timer is set
        submitNotification = (Button) findViewById(R.id.submit_notification);
        submitNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scheduleNotification(getNotification(notificationText));
            }
        });
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
