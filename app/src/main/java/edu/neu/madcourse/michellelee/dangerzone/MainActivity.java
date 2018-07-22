package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

import static android.content.ContentValues.TAG;

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

        // Walk introduction screen
        View walkIntroButton = findViewById(R.id.walk_button);
        walkIntroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent walkIntent = new Intent(getApplicationContext(), WalkIntro.class);
                startActivity(walkIntent);
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

        // Information and instructions button
        View instructionsButton = findViewById(R.id.instructions_button);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent userProfileIntent = new Intent(getApplicationContext(), InformationInstructionsFragment.class);
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
                editor.putInt("level", 1);
                editor.putString("minutes walked", "none");
                editor.putString("distance walked", "none");
                editor.putInt("# titles", 1);
                editor.putString("title", "Fresh Meat");
                editor.putInt("# achievements", 1);
                editor.putString("achievements", "Danger Seeker");
                editor.putInt("xp", 0);
                editor.putInt("personal best", 0);
                editor.apply();
                doDataAddToDb(userNameString, "Fresh Meat");
                }
            });

            startDialog = startBuilder.show();

            // Update that app has been started up
            editor.putBoolean("initial startup", false);
            editor.apply();
        }
    }

    /**
     * Adds a brand new user to Firebase
     * @param userNameString the user name entered by the user
     * @param title the current title of the user
     */
    private void doDataAddToDb(String userNameString, String title) {
        // Get token for this app instance
        String token = FirebaseInstanceId.getInstance().getToken();
        String uniqueID = token.substring(token.length()-8);
        editor.putString("uid", uniqueID);  // Add this ID to shared preferences
        editor.apply();

        // Get date last active (now)
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Create a String[] for friends
        String friendsList = "";

        // Creating a new user for the database
        User newUser = new User(userNameString, title, date, "n/a", "n/a", uniqueID, friendsList);

        // Add new node in database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).setValue(newUser);
    }


}