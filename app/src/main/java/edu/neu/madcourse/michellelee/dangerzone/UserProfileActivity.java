package edu.neu.madcourse.michellelee.dangerzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Get user information
        String name = preferences.getString("username", null);
        int level = preferences.getInt("level", -1);
        String currentTitle = preferences.getString("title", null);
        String minutesWalked = preferences.getString("minutes walked", null);
        String distanceWalked = preferences.getString("distance walked", null);
        int titles = preferences.getInt("# titles", -1);
        int achievements = preferences.getInt("# achievements", -1);
        String achievementsString = preferences.getString("achievements", null);


        // Hooking up achievements with adapters
        ArrayList<String> itemList1 = new ArrayList<String>();
        ArrayAdapter<String> titlesAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, itemList1);
        ListView titlesList = (ListView) findViewById(R.id.titles_list);
        titlesList.setAdapter(titlesAdapter);
//        titlesAdapter.add("title1");
        ArrayList<String> itemList2 = new ArrayList<String>();
        ArrayAdapter<String> achievementsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, itemList2);
        ListView achievementsList = (ListView) findViewById(R.id.achievements_list);
        achievementsList.setAdapter(achievementsAdapter);
//        achievementsAdapter.add("title1");

        // TEST FOR TITLES AND ACHIEVEMENTS PROCESSING
        // Special Processing to Add: To add a new title to the StringBuilder "list", create a StringBuilder based on the current
        // titles in shared preferences. Append an "," to the current titles "list" in StringBuilder before the next new title.
        // Add the new title to the list. Commit to shared preferences.
        String test1 = "test 1";
        StringBuilder titlebuild = new StringBuilder(currentTitle); // Create new StringBuilder
        titlebuild.append(","); // Add a delimiter to the end of it the existing String list
        titlebuild.append(test1);   // Add the new title to the StringBuilder
        editor.putString("titles", titlebuild.toString());  // Replace old String of titles with new
        editor.apply();

//        String test2 = "test 2";
//        StringBuilder achievebuild = new StringBuilder(achievementsString);   // Create new StringBuilder
//        achievebuild.append(test1);
//        achievebuild.append(",");   // Add a delimiter to the end of it the existing String list
//        achievebuild.append(test2); // Add the new title to the StringBuilder
//        editor.putString("achievements", achievebuild.toString());  // Replace old String of titles with new
//        editor.apply();

        // Special Processing to Retrieve: Get the String which contains all the titles. Split into an array based on the ","
        // delimiter. Iterate through the array of titles and add each to the ListView.
        String titleList = preferences.getString("titles", null);   // Get String list from SharedPreferences
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
        minutesWalkedView.setText(minutesWalked);
        TextView distancewalkedView = (TextView) findViewById(R.id.distance_walked);
        distancewalkedView.setText(distanceWalked);
        TextView titlesEarned = (TextView) findViewById(R.id.titles_earned);
        titlesEarned.setText(Integer.toString(titles));
        TextView numberOfAchievementsView = (TextView) findViewById(R.id.achievements_earned);
        numberOfAchievementsView.setText(Integer.toString(achievements));

        // Clickable title selection
        // Making the title ListView clickable so that titles can be changed
        titlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = ((TextView) view).getText().toString();
                userTitleView.setText("Title: " + selected);
                editor.putString("title", selected);
                editor.apply();
                Toast.makeText(UserProfileActivity.this,selected,Toast.LENGTH_LONG).show();
            }
        });


    }

}
