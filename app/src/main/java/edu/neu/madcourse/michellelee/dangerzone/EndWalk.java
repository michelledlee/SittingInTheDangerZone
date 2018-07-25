package edu.neu.madcourse.michellelee.dangerzone;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class EndWalk extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_walk);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Putting together all bonuses for list
        ArrayList<String> pointsArray = new ArrayList<>();

        // Get variables passed form ActivityWalk
        Bundle extras = getIntent().getExtras();
        int numSteps = extras.getInt("steps");
        String walkFinished = extras.getString("walk finished");
        if (!walkFinished.equals("")) pointsArray.add(walkFinished);
        String stepBonus = extras.getString("step bonus");
        if (!stepBonus.equals("")) pointsArray.add(stepBonus);
        String timeBonus = extras.getString("time bonus");
        if (!timeBonus.equals("")) pointsArray.add(timeBonus);
        String personalBest = extras.getString("personal best");
        if (!personalBest.equals("")) pointsArray.add(personalBest);

        // XP and level calculator
        int experience = preferences.getInt("xp", -1);
        int experienceNeeded = 0;
        if (experience < 99) {
            editor.putInt("level", 1);
            editor.apply();
            experienceNeeded = 100;
        } else if (experience >= 100 && experience < 199) {
            editor.putInt("level", 2);
            editor.apply();
            experienceNeeded = 200;
        } else if (experience >= 200 && experience < 399) {
            editor.putInt("level", 3);
            editor.apply();
            experienceNeeded = 399;
        } else if (experience >= 400 && experience < 799) {
            editor.putInt("level", 4);
            editor.apply();
            experienceNeeded = 799;
        } else if (experience >= 800 && experience < 1599) {
            editor.putInt("level", 5);
            editor.apply();
            experienceNeeded = 1599;
        }
        int level = preferences.getInt("level", -1);
        int nextLevel = level + 1;
        double percentageXP = experience / experienceNeeded * 100;

        // Convert to double to get percentage to next level
        double obtainedScore = (double) experience;
        double totalScore = (double) experienceNeeded;
        float percentage = (float) ((obtainedScore*100)/totalScore);

        // Animate the progress bar
        int progress = (int) percentage;
        TextView xpProgress = (TextView) findViewById(R.id.progress_text);
        xpProgress.setText(Integer.toString(progress) + "%");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progress); // see this max value coming back here, we animate towards that value
        animation.setDuration(5000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // TextView to display progress information
        TextView progressNextLevel = (TextView) findViewById(R.id.progress_level);
        String progressText = getResources().getString(R.string.to_next_level);
        progressNextLevel.setText(experience + progressText + nextLevel);

        // Main menu activity
        Button mainMenu = (Button) findViewById(R.id.walk);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        // Total points calculator and listing
        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, pointsArray);
        ListView walkExperienceList = (ListView) findViewById(R.id.walk_experience_list);
        walkExperienceList.setAdapter(pointsAdapter);

        // Statistics calculation and listing (time and steps)
        ArrayList<String> statisticsArray = new ArrayList<>();  // ArrayList for statistics display
        int secondsSession = extras.getInt("seconds summary");  // Get seconds walked this session
        int sessionMinutes = secondsSession / 60;   // Convert to minutes
        String minutesSummary;  // Declare string for minutes summary
        // Check to determine what we are displaying for the time statistic
        if (sessionMinutes < 1) {
            minutesSummary = "Minutes Walked (min) ... < 1 min";    // Do not display fractional time if less than a minute
            Log.e("walked: ", "< 1 min");
        } else {
            minutesSummary = "Minutes Walked (min) ... " + Integer.toString(sessionMinutes);    // Concatenate string with values
            Log.e("walked: ", "more than a feeling");
        }
        statisticsArray.add(minutesSummary);    // Add to the array of statistics strings
        int stepsSession = extras.getInt("steps summary"); // Get distance walked this session
        String stepsSummary = "Steps Walked (m) ... " + Integer.toString(stepsSession); // Concatenate string with values
        statisticsArray.add(stepsSummary);   // Add to the array of statistics strings
        ArrayAdapter<String> statisticsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, statisticsArray);
        ListView statisticsSummary = (ListView) findViewById(R.id.statistics_list);
        statisticsSummary.setAdapter(statisticsAdapter);
    }
}
