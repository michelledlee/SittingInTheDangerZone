package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AlertDialog startDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Ask if this is the first startup of the app as we need to get the user name
        isInitialStartup();

        // Settings button
        View settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        // User profile
        View userProfile = findViewById(R.id.profile_button);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(userProfileIntent);
            }
        });

    }

    private void isInitialStartup() {
        // If this is the first time we are opening the app
        if (preferences.getBoolean("initial startup", true)) {
            // Dialog to ask for user name
            AlertDialog.Builder startBuilder = new AlertDialog.Builder(this);
            LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = startInflater.inflate(R.layout.initial_start_dialog, null);     // Get dialog view
            startBuilder.setCancelable(false);
            startBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startDialog.dismiss();
                } });
            startBuilder.setView(dialogView);    // Set view to initial start dialog

            // Set up username input
            final EditText usernameEntry = (EditText) dialogView.findViewById(R.id.username_input);
            // Button to submit username
            final Button enterUserName = (Button) dialogView.findViewById(R.id.enter_username);
            enterUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // Username information
                StringBuilder userName = new StringBuilder();
                userName.append(usernameEntry.getText());
                String userNameString = userName.toString();
                // Text view that shows what the user has submitted their name as
                TextView welcomeUser = (TextView) dialogView.findViewById(R.id.welcome_message);
                welcomeUser.setText("Welcome " + userNameString + ".");
                welcomeUser.setVisibility(View.VISIBLE);
                enterUserName.setVisibility(View.INVISIBLE);
                // Update shared preferences with default values for the user profile
                editor.putString("username", userNameString);
                editor.apply();
                editor.putInt("level", 0);
                editor.apply();
                editor.putString("title", "Fresh Meat");
                editor.putInt("minutes walked", 0);
                editor.apply();
                editor.putInt("distance walked", 0);
                editor.apply();
                editor.putInt("# titles", 1);
                editor.apply();
                editor.putInt("# achievements", 0);
                editor.apply();

                }
            });

            startDialog = startBuilder.show();

            // Update that app has been started up
            editor.putBoolean("initial startup", false);
            editor.apply();
        }
    }

}