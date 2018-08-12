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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.FriendsList;

/**
 * Shows the user information including a summary of app usage and provides a link to the friends list of the user.
 */
public class UserProfileActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Dialogs for titles and achievements
    private AlertDialog titleDialog;
    private AlertDialog firstTimerTitleDialog;
    private AlertDialog firstTimerAchievementDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Shared Preferences to get and display user information like name, level, statistics, etc.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Check if this is the first time this screen has been accessed because we want to
        // congratulate the user for playing by giving them a new title and a new achievement
        if (preferences.getBoolean("profile access", true)) {
            isInitialStartup();
        }

        // Get user information from shared preferences that will be used to update the views
        final String name = preferences.getString("username", null);
        int level = preferences.getInt("level", -1);
        String currentTitle = preferences.getString("title", null);
        int secondsWalked = preferences.getInt("seconds walked", -1);
        int stepsWalked = preferences.getInt("steps walked", -1);
        int titles = preferences.getInt("# titles", -1);
        int achievements = preferences.getInt("# achievements", -1);
        String achievementsString = preferences.getString("achievements", null);

        // Calculate cumulative minutes and distance walked to display
        int minutesWalked = secondsWalked / 60;
        int distanceWalked = (int) Math.rint(stepsWalked * 0.000762);

        // Hooking up titles and achievements with adapters
        ArrayList<String> itemList1 = new ArrayList<String>();  // TITLES
        ArrayAdapter<String> titlesAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, itemList1);
        ListView titlesList = (ListView) findViewById(R.id.titles_list);
        titlesList.setAdapter(titlesAdapter);
        ArrayList<String> itemList2 = new ArrayList<String>(); // ACHIEVEMENTS
        ArrayAdapter<String> achievementsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, itemList2);
        ListView achievementsList = (ListView) findViewById(R.id.achievements_list);
        achievementsList.setAdapter(achievementsAdapter);

        // TEST FOR TITLES AND ACHIEVEMENTS PROCESSING
        // Special Processing to Retrieve: Get the String which contains all the titles. Split into an array based on the ","
        // delimiter. Iterate through the array of titles and add each to the ListView.
        String titleList = preferences.getString("title list", null);   // Get String list from SharedPreferences
        String[] titleArray = titleList.split(","); // Get array of individual titles by splitting the string based on the "," delimiter
        for (String eachTitle : titleArray) {   // Iterate through each of the titles in the list
            titlesAdapter.add(eachTitle);   // Add each title to the list
        }
        String achievementList = preferences.getString("achievements", null); // Get String list from SharedPreferences
        String[] achievementArray = achievementList.split(","); // Get array of individual achievements by splitting the string based on the "," delimiter
        for (String eachAchievement : achievementArray) { // Iterate through each of the achievements in the list
            achievementsAdapter.add(eachAchievement);   // Add each achievement to the list
        }

        // Hooking up text views and settings text from loaded user information
        TextView usernameView = (TextView) findViewById(R.id.user_name);
        usernameView.setText(name);
        TextView userLevelView = (TextView) findViewById(R.id.user_level);
        userLevelView.setText("Level " + Integer.toString(level));
        final TextView userTitleView = (TextView) findViewById(R.id.user_title);
        userTitleView.setText("Title: " + currentTitle);
        TextView minutesWalkedView = (TextView) findViewById(R.id.minutes_walked);
        minutesWalkedView.setText(Integer.toString(minutesWalked));
        TextView stepsWalkedView = (TextView) findViewById(R.id.steps_walked);
        stepsWalkedView.setText(Integer.toString(stepsWalked));
        TextView distancewalkedView = (TextView) findViewById(R.id.distance_walked);
        distancewalkedView.setText(Integer.toString(distanceWalked));
        TextView titlesEarned = (TextView) findViewById(R.id.titles_earned);
        titlesEarned.setText(Integer.toString(titles));
        TextView numberOfAchievementsView = (TextView) findViewById(R.id.achievements_earned);
        numberOfAchievementsView.setText(Integer.toString(achievements));

        // Get dialog for the title information
        final AlertDialog.Builder titleBuilder = new AlertDialog.Builder(this);
        LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = startInflater.inflate(R.layout.title_instructions, null);     // Get dialog view
        titleBuilder.setCancelable(false);
        titleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                titleDialog.dismiss();
            } });
        titleBuilder.setView(dialogView);    // Set view to title dialog

        // Adding information button on how to change titles
        Button titleInfo = (Button) findViewById(R.id.info_button);
        titleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleDialog = titleBuilder.show();
            }
        });

        // Clickable title selection- Making the title ListView clickable so that titles can be changed
        titlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String selected = ((TextView) view).getText().toString();
            userTitleView.setText("Title: " + selected);
            editor.putString("title", selected);
            editor.apply();
            String titleChanged = getResources().getString(R.string.title_change);
            Toast.makeText(UserProfileActivity.this,titleChanged,Toast.LENGTH_SHORT).show();
            dataAddAppInstance(selected);
            }
        });

        // Hook up button for friends list activity
        Button friendsScreen = (Button) findViewById(R.id.friends_screen);
        friendsScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent friendsIntent = new Intent(getApplicationContext(), FriendsList.class);
                startActivity(friendsIntent);
            }
        });

    }

    /**
     * Method to change the title name to be displayed to the friend's list
     * @param title title that is being changed to
     */
    public void dataAddAppInstance(String title) {
         // Get ID reference for node in question
        String uniqueID =  preferences.getString("uid", null);

        // Update firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("title").setValue(title);
    }

    /**
     * Initial startup routine that displays the new players title and achievement for starting
     * the app for the first time
     */
    private void isInitialStartup() {
        // Set the access flag so that it does not show the dialog again
        editor.putBoolean("profile access", false);
        editor.apply();

        final LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Title dialog with new title information
        final View titleView = startInflater.inflate(R.layout.first_title, null);

        // Make this dialog pop up and set the conditions for dismissal
        final AlertDialog.Builder firstTitleBuilder = new AlertDialog.Builder(this);
        firstTitleBuilder.setView(titleView);
        firstTitleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               firstTimerTitleDialog.dismiss();
                // Achievement dialog with new achievement information
                final View achievementView = startInflater.inflate(R.layout.first_achievement, null);

                // Make this dialog pop up and set the conditions for dismissal
                final AlertDialog.Builder firstAchievementBuilder = new AlertDialog.Builder(UserProfileActivity.this);
                firstAchievementBuilder.setView(achievementView);
                firstAchievementBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firstTimerAchievementDialog.dismiss();
                    }
                });
                firstTimerAchievementDialog = firstAchievementBuilder.create();
                firstTimerAchievementDialog.show();
            }
        });
        firstTimerTitleDialog = firstTitleBuilder.create();
        firstTimerTitleDialog.show();

    }

}
