package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import edu.neu.madcourse.michellelee.dangerzone.notifications.SettingsActivity;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

/**
 * This activity is the main menu of the app. On initial startup it asks for a user name and initializes
 * app-wide data that will be used. It also adds this user instance to Firebase. The main menu has buttons
 * to the other activities in the app.
 */
public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AlertDialog startDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Shared Preferences which will be heavily used on first start to initialize the user's profile
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Ask if this is the first startup of the app as we need to get the user name
        if (preferences.getBoolean("initial startup", true)) {
            isInitialStartup(); // Initial startup routine gets the user name, adds them to Firebase, and intializes their user profile
        }

        // Settings button to start the Settings activity
        View settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        // Walk introduction screen that allows the user to select the times they want to walk and start that activity
        View walkIntroButton = findViewById(R.id.walk_button);
        walkIntroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent walkIntent = new Intent(getApplicationContext(), WalkIntro.class);
                startActivity(walkIntent);
            }
        });

        // User profile button accesses all their user specific data from app usage
        View userProfile = findViewById(R.id.profile_button);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(userProfileIntent);
            }
        });

        // Information and instructions button on how to play and about the developers
        View instructionsButton = findViewById(R.id.instructions_button);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent userProfileIntent = new Intent(getApplicationContext(), InformationInstructionsFragment.class);
                startActivity(userProfileIntent);
            }
        });

        // Create notification channel for the app
       createNotificationChannel();
    }

    /**
     * Create notification channel for the app. The app sends notifications based on an interval
     * timer to remind the user to play.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Initial startup routine that gathers user information for the app experience and Firebase.
     */
    private void isInitialStartup() {
            // Set up views for the dialog to ask for the user name
            LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // Get layout inflater
            final View dialogView = startInflater.inflate(R.layout.initial_start_dialog, null);     // Get dialog view
            final EditText usernameEntry = (EditText) dialogView.findViewById(R.id.username_input); // Get edit text view
            final Button enterUserName = (Button) dialogView.findViewById(R.id.enter_username); // Button to submit username

            // Dialog to ask for user name
            final AlertDialog.Builder startBuilder = new AlertDialog.Builder(this);
            startBuilder.setView(dialogView);    // Set view to initial start dialog
            // Set up positive button to save user name information that the user enters as well as initialize
            // shared preferences for this instance's app
            startBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Get username information from the edit text and convert it to a string
                    StringBuilder userName = new StringBuilder();
                    userName.append(usernameEntry.getText());
                    String userNameString = userName.toString();

                    // Update shared preferences with default values for the user profile
                    editor.putString("username", userNameString);
                    editor.apply();
                    editor.putInt("level", 1);
                    editor.apply();
                    editor.putInt("seconds walked", 0);
                    editor.apply();
                    editor.putInt("steps walked", 0);
                    editor.apply();
                    editor.putInt("# titles", 1);
                    editor.apply();
                    editor.putString("title", "Fresh Meat");
                    editor.apply();
                    editor.putString("title list", "Fresh Meat");
                    editor.apply();
                    editor.putInt("# achievements", 1);
                    editor.apply();
                    editor.putString("achievements", "Danger Seeker");
                    editor.apply();
                    editor.putInt("xp", 0);
                    editor.apply();
                    editor.putInt("personal best", 0);
                    editor.apply();
                    doDataAddToDb(userNameString, "Fresh Meat");    // Add this user's information to Firebase
                    String newTitleEarned = getResources().getString(R.string.new_title_earned);    // Get the initial title from string resources
                    Toast.makeText(MainActivity.this,newTitleEarned,Toast.LENGTH_SHORT).show();  // Let the user know they have earned a default new player title

                    // Set default notification preferences in case the user does not set them all
                    editor.putInt("intervalMinutes", 30);
                    editor.apply();

                    // Set the initial startup flag so that it does not ask again
                    editor.putBoolean("initial startup", false);
                    editor.apply();
                }
            });
            startDialog = startBuilder.create();


            // Add text changed listener to ensure that the user has entered some username information before they can continue
            // as the username cannot be changed once submitted
            usernameEntry.addTextChangedListener(new TextWatcher() {
                private void handleText() {
                    // Grab the button
                    final Button okButton = startDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    if(usernameEntry.getText().length() == 0) {
                        okButton.setEnabled(false);
                    } else {
                        okButton.setEnabled(true);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    handleText();
                }
            });

            // Show the button to submit the user name
            enterUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get username from the edit text
                    StringBuilder userName = new StringBuilder();
                    userName.append(usernameEntry.getText());
                    String userNameString = userName.toString();

                    // Text view that shows what the user has submitted their name as
                    TextView welcomeUser = (TextView) dialogView.findViewById(R.id.welcome_message);
                    welcomeUser.setText("Welcome " + userNameString + ".");
                    welcomeUser.setVisibility(View.VISIBLE);
                    enterUserName.setVisibility(View.INVISIBLE);
                }
            });


        startDialog.show();
        startDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }


    /**
     * Adds a brand new user to Firebase
     * @param userNameString the user name entered by the user
     * @param title the current title of the user
     */
    private void doDataAddToDb(String userNameString, String title) {
        // Get token for this app instance
        String uniqueID = idGenerator();
        editor.putString("uid", uniqueID);  // Add this ID to shared preferences
        editor.apply();

        // Get date last active (now)
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Create a map to hold the friends list
        Map<String, String> friendsList = new HashMap<String, String>();

        // Creating a new user for the database
        User newUser = new User(userNameString, title, date, "n/a", "n/a", uniqueID, friendsList);

        // Add new node in database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).setValue(newUser);
    }

    /**
     * Generates a (hopefully) random 10 character unique ID string
     * @return the user ID
     */
    private String idGenerator() {
        String alphabet= "abcdefghijklmnopqrstuvwxyz";
        String s = "";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = alphabet.charAt(random.nextInt(26));
            s+=c;
        }
        return s;
    }


}