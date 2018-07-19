package edu.neu.madcourse.michellelee.dangerzone;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        int percentageXP = experience / experienceNeeded;

        // Animate the progress bar
        int progress = percentageXP * 100;
        TextView xpProgress = (TextView) findViewById(R.id.progress_text);
        xpProgress.setText(Integer.toString(progress) + "%");
//        int progress = 50;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progress); // see this max value coming back here, we animate towards that value
        animation.setDuration(5000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Main menu activity
        Button mainMenu = (Button) findViewById(R.id.walk);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
