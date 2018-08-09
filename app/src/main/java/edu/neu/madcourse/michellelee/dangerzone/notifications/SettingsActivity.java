package edu.neu.madcourse.michellelee.dangerzone.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.michellelee.dangerzone.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private Button submitInterval;
    private TextView currentInterval;
    private int savedInterval;

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
        savedInterval = preferences.getInt("intervalMinutes", 30);

        // Set the view for the notifications switch depending to user preferences
        final Switch notificationsSwitch = (Switch) findViewById(R.id.notifications_switch);
        // Get the current state of notification preferences
        String switchState = preferences.getString("Notifications", "Off");
        if (switchState.equals("On")) { // If notifications are on, display the switch in its "On" state
            notificationsSwitch.setChecked(true);
        } else if (switchState.equals("Off")) {    // If they are turned off, display the switch in its "Off" state
            notificationsSwitch.setChecked(false);
        }

        // Display what interval the notifications are sent to go off at
        currentInterval = (TextView) findViewById(R.id.current_setting);    // TextView to update
        final String currentSettings = getResources().getString(R.string.current_settings);
        currentInterval.setText(currentSettings + savedInterval);     // Display default setting on startup

        // Hooking up buttons
        submitInterval = (Button) findViewById(R.id.submit_interval);
        final RadioGroup intervalSelection = (RadioGroup) findViewById(R.id.interval_group);
        intervalSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                submitInterval.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Execute if notifications are enabled
                        if(notificationsSwitch.isChecked()) {
                            // Get user selection for notification interval
                            int radioButtonID = intervalSelection.getCheckedRadioButtonId();
                            View radioButton = intervalSelection.findViewById(radioButtonID);
                            int index = intervalSelection.indexOfChild(radioButton);

                            // Identify which time was selected
                            int minutesSelected;
                            if (index == 0) {
                                minutesSelected = 15;
                            } else if (index == 1) {
                                minutesSelected = 30;
                            } else if (index == 2) {
                                minutesSelected = 45;
                            } else {
                                minutesSelected = 60;
                            }

                            editor.putInt("intervalMinutes", minutesSelected);    // Save minutes interval for notification reminder
                            editor.apply(); // This will be used to determine when to send for the receiver

                            savedInterval = preferences.getInt("intervalMinutes", 30);
                            currentInterval.setText(currentSettings + savedInterval);    // Display current interval as selected by the
                                                                                        // user in real time so they have confirmation that changes were made

                            // Cancel the old alarm at hte old interval
                            NotificationHelper.cancelAlarmElapsed();
                            NotificationHelper.disableBootReceiver(mContext);

                            // Set the new alarm based on new preferences based on interval selected
                            switch (savedInterval) {
                                case 15: // 15 minutes
                                    NotificationHelper.scheduleRepeatingElapsedNotification15(mContext);
                                    NotificationHelper.enableBootReceiver(mContext);
                                    break;
                                case 30: // 30 minutes
                                    NotificationHelper.scheduleRepeatingElapsedNotification30(mContext);
                                    NotificationHelper.enableBootReceiver(mContext);
                                    break;
                                case 45: // 45 minutes
                                    NotificationHelper.scheduleRepeatingElapsedNotification45(mContext);
                                    NotificationHelper.enableBootReceiver(mContext);
                                    break;
                                case 60: // 60 minutes
                                    NotificationHelper.scheduleRepeatingElapsedNotification60(mContext);
                                    NotificationHelper.enableBootReceiver(mContext);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Toast.makeText(SettingsActivity.this, "Enable notifications to submit", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Setting up switch function to turn notifications on/off
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Enable buttons for selecting notification settings
                if (notificationsSwitch.isChecked()) {
                    editor.putString("Notifications","On"); // Save the notification preference as on
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Notifications on", Toast.LENGTH_SHORT).show();    // Let the user know that notifications are turned on;

                    // Get the current interval level
                    savedInterval = preferences.getInt("intervalMinutes", 30);

                    // Schedule a repeating notification to walk
                    NotificationHelper.scheduleRepeatingElapsedNotification30(mContext);
                    NotificationHelper.enableBootReceiver(mContext);

                // Disable buttons if notifications are turned off
                } else if (!notificationsSwitch.isChecked()) {
                    editor.putString("Notifications","Off"); // Save the notification preference as off
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "Notifications off", Toast.LENGTH_SHORT).show();   // Let the user know that notifications are turned off

                    // Cancel alarm intent as notifications were switched off
                    NotificationHelper.cancelAlarmElapsed();
                    NotificationHelper.disableBootReceiver(mContext);
                }
            }
        });

    }

}
