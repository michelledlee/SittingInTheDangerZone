package edu.neu.madcourse.michellelee.dangerzone;

import android.content.SharedPreferences;
import android.os.Bundle;
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

    private String name;
    private int level;
    private String currentTitle;
    private int minutesWalked;
    private int distanceWalked;
    private int achievements;

    private TextView usernameView;
    private TextView userLevelView;
    private TextView userTitleView;
    private TextView minutesWalkedView;
    private TextView distancewalkedView;
    private TextView numberOfAchievementsView;

    private ListView titlesList;
    private ListView achievementsList;

    private ArrayAdapter<String> titlesAdapter;
    private ArrayAdapter<String> achievementsAdapter;
    private ArrayList<String> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get user information
        name = preferences.getString("username", null);
        level = preferences.getInt("level", -1);
        currentTitle = preferences.getString("title", null);
        minutesWalked = preferences.getInt("minutes walked", -1);
        distanceWalked = preferences.getInt("distance walked", -1);
        achievements = preferences.getInt("# achievements", -1);

        // Hooking up text views and settings text from loaded user information
        usernameView = (TextView) findViewById(R.id.user_name);
        usernameView.setText(name);
        userLevelView = (TextView) findViewById(R.id.user_level);
        userLevelView.setText(level);
        userTitleView = (TextView) findViewById(R.id.user_title);
        userTitleView.setText(currentTitle);
        minutesWalkedView = (TextView) findViewById(R.id.minutes_walked);
        minutesWalkedView.setText(minutesWalked);
        distancewalkedView = (TextView) findViewById(R.id.distance_walked);
        distancewalkedView.setText(distanceWalked);
        numberOfAchievementsView = (TextView) findViewById(R.id.achievements_earned);
        numberOfAchievementsView.setText(achievements);

        // Hooking up achievements with adapters
        itemList = new ArrayList<String>();
        titlesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        titlesList = (ListView) findViewById(R.id.titles_list);
        titlesList.setAdapter(titlesAdapter);
        titlesAdapter.add("title1");
        achievementsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        achievementsList = (ListView) findViewById(R.id.achievements_list);
        achievementsList.setAdapter(achievementsAdapter);
        achievementsAdapter.add("title1");
    }

}
