package edu.neu.madcourse.michellelee.dangerzone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Get user information
        String name = preferences.getString("username", null);
        int level = preferences.getInt("level", -1);
        String currentTitle = preferences.getString("title", null);
        int minutesWalked = preferences.getInt("minutes walked", -1);
        int distanceWalked = preferences.getInt("distance walked", -1);
        int titles = preferences.getInt("# titles", -1);
        int achievements = preferences.getInt("# achievements", -1);

        // Hooking up text views and settings text from loaded user information
        TextView usernameView = (TextView) findViewById(R.id.user_name);
        usernameView.setText(name);
        TextView userLevelView = (TextView) findViewById(R.id.user_level);
        userLevelView.setText(level);
        TextView userTitleView = (TextView) findViewById(R.id.user_title);
        userTitleView.setText(currentTitle);
        TextView minutesWalkedView = (TextView) findViewById(R.id.minutes_walked);
        minutesWalkedView.setText(minutesWalked);
        TextView distancewalkedView = (TextView) findViewById(R.id.distance_walked);
        distancewalkedView.setText(distanceWalked);
        TextView titlesEarned = (TextView) findViewById(R.id.titles_earned);
        titlesEarned.setText(titles);
        TextView numberOfAchievementsView = (TextView) findViewById(R.id.achievements_earned);
        numberOfAchievementsView.setText(achievements);

        // Hooking up achievements with adapters
        ArrayList<String> itemList = new ArrayList<String>();
        ArrayAdapter<String> titlesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        ListView titlesList = (ListView) findViewById(R.id.titles_list);
        titlesList.setAdapter(titlesAdapter);
        titlesAdapter.add("title1");
        ArrayAdapter<String> achievementsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        ListView achievementsList = (ListView) findViewById(R.id.achievements_list);
        achievementsList.setAdapter(achievementsAdapter);
        achievementsAdapter.add("title1");
    }

}
