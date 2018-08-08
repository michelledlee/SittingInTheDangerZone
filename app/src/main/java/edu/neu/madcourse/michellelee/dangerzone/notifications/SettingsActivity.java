package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
    private boolean initialView = true;
    private boolean initialSwitch = true;
    private TextView currentInterval;
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
                // Check if this is the intialization phase, if it is we do not want to change the settings based on this "selection"
                if (initialSwitch) {
                    initialSwitch = false;    // All selection instances after this are true user select instances, therefore set initialView to false so below can execute
                    return;
                }

                // Enable buttons for selecting notification settings
                if (notificationsSwitch.isChecked()) {
                    editor.putString("Notifications","On"); // Save the notification preference as on
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Notifications on", Toast.LENGTH_LONG).show();    // Let the user know that notifications are turned on

                    // Get the current interval level
                    interval = preferences.getInt("intervalMinutes", 30);

                    // Schedule a repeating notification to walk
                    NotificationHelper.scheduleRepeatingElapsedNotification30(mContext);
                    NotificationHelper.enableBootReceiver(mContext);

                // Disable buttons if notifications are turned off
                } else {
                    editor.putString("Notifications","Off"); // Save the notification preference as off
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Notifications off", Toast.LENGTH_LONG).show();   // Let the user know that notifications are turned off

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
                // Check if this is the intialization phase, if it is we do not want to change the settings based on this "selection"
                if (initialView) {
                    initialView = false;    // All selection instances after this are true user select instances, therefore set initialView to false so below can execdute
                    return;
                }

                int minutesSelected = Integer.parseInt(adapterView.getItemAtPosition(i).toString()); // Get user selection from spinner and convert to integer
                editor.putInt("intervalMinutes", minutesSelected);    // Save minutes interval for notification reminder
                editor.apply();

                interval = preferences.getInt("intervalMinutes", 30);   // Set interval to the new preference
                currentInterval.setText(currentSettings + interval);    // Display current interval as selected by the user

                // Cancel the old alarm
                NotificationHelper.cancelAlarmElapsed();
                NotificationHelper.disableBootReceiver(mContext);

                // Set the new alarm based on new preferences based on interval selected
                switch (interval) {
                    case 1: // 1 minute
                        NotificationHelper.scheduleRepeatingElapsedNotification1(mContext);
                        NotificationHelper.enableBootReceiver(mContext);
                        Toast.makeText(SettingsActivity.this, "1 minute set", Toast.LENGTH_LONG).show();
                        break;
                    case 15: // 15 minutes
                        NotificationHelper.scheduleRepeatingElapsedNotification15(mContext);
                        NotificationHelper.enableBootReceiver(mContext);
                        Toast.makeText(SettingsActivity.this, "15 minute set", Toast.LENGTH_LONG).show();
                        break;
                    case 30: // 30 minutes
                        NotificationHelper.scheduleRepeatingElapsedNotification30(mContext);
                        NotificationHelper.enableBootReceiver(mContext);
                        Toast.makeText(SettingsActivity.this, "30 minute set", Toast.LENGTH_LONG).show();
                        break;
                    case 45: // 45 minutes
                        NotificationHelper.scheduleRepeatingElapsedNotification45(mContext);
                        NotificationHelper.enableBootReceiver(mContext);
                        Toast.makeText(SettingsActivity.this, "45 minute set", Toast.LENGTH_LONG).show();
                        break;
                    case 60: // 60 minutes
                        NotificationHelper.scheduleRepeatingElapsedNotification60(mContext);
                        NotificationHelper.enableBootReceiver(mContext);
                        Toast.makeText(SettingsActivity.this, "60 minute set", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}
