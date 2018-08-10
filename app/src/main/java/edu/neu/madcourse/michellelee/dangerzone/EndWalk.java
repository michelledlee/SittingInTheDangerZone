package edu.neu.madcourse.michellelee.dangerzone;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * This Activity calculates and displays all results from the walk including whether the user
 * has won or lost, the steps and distance they walked, etc.
 */
public class EndWalk extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AlertDialog failDialog;
    private AlertDialog successDialog;
    private AlertDialog dinoTitleDialog;

    private int mDinoEating, mBirdsChirping;
    private SoundPool mSoundPool;
    private float mVolume = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_walk);

        // Initialize Shared Preferences to record user profile information such as level, distance, etc.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Loading sound objects for victory and failure
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mDinoEating = mSoundPool.load(getApplicationContext(), R.raw.dino_eating, 1);
        mBirdsChirping = mSoundPool.load(getApplicationContext(), R.raw.birds_chirping, 1);

        // Putting together all bonuses for list
        ArrayList<String> pointsArray = new ArrayList<>();

        // Get variables passed form ActivityWalk
        Bundle extras = getIntent().getExtras();
        int numSteps = extras.getInt("steps");
        String walkFinished = extras.getString("walk finished");    // Get results of walk finished
        if (!walkFinished.equals("")) { // If walk was finished successfully
            // ADD RESULTS TO FIREBASE FOR A WIN
            dataAddAppInstance("T-Rex Tango", "escaped!", preferences.getInt("level", -1));

            // SUCCESS DIALOG
            AlertDialog.Builder successBuilder = new AlertDialog.Builder(this);
            final LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = startInflater.inflate(R.layout.success_dialog, null);     // Get dialog view
            successBuilder.setCancelable(false);
            successBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                successDialog.dismiss();

                    // IF THIS IS THE FIRST TIME THIS SCENARIO HAS BEEN WON, A NEW TITLE IS EARNED
                    int successfulDinoAttempts = preferences.getInt("dinosaurs", 0);
                    if (successfulDinoAttempts == 0) {
                        // Title dialog with new title information
                        final View titleView = startInflater.inflate(R.layout.first_dino, null);
                        // Make this dialog pop up and set the conditions for dismissal
                        final AlertDialog.Builder dinoTitleBuilder = new AlertDialog.Builder(EndWalk.this);
                        dinoTitleBuilder.setView(titleView);
                        dinoTitleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dinoTitleDialog.dismiss();
                                // ADD NEW TITLE
                                // Special Processing to Add: To add a new title to the StringBuilder "list", create a StringBuilder based on the current
                                // titles in shared preferences. Append an "," to the current titles "list" in StringBuilder before the next new title.
                                // Add the new title to the list. Commit to shared preferences.
                                String dinomite = getResources().getString(R.string.dinomite);
                                String existingTitles = preferences.getString("title list", null);
                                StringBuilder titlebuild = new StringBuilder(existingTitles); // Create new StringBuilder
                                titlebuild.append(","); // Add a delimiter to the end of it the existing String list
                                titlebuild.append(dinomite);   // Add the new title to the StringBuilder
                                editor.putString("title list", titlebuild.toString());  // Replace old String of titles with new
                                editor.apply();
                            }
                        });
                        dinoTitleDialog = dinoTitleBuilder.create();
                        dinoTitleDialog.show();

                        // Update # of times this scenario has been completed successfully
                        editor.putInt("dinosaurs", successfulDinoAttempts + 1);
                        editor.apply();
                    }

                    // CHECKING FOR ACHIEVEMENTS
                    if (successfulDinoAttempts > 5) {
                        editor.putInt("# achievements", 2);
                        editor.apply();

                        // ADD NEW ACHIEVEMENT
                        // Special Processing to Add: To add a new achievement to the StringBuilder "list", create a StringBuilder based on the current
                        // achievements in shared preferences. Append an "," to the current achievements "list" in StringBuilder before the next new achievement.
                        // Add the new achievement to the list. Commit to shared preferences.
                        String dinowrangler = getResources().getString(R.string.dino_wrangler);
                        String existingAchievements = preferences.getString("achievement list", null);
                        StringBuilder achievementBuild = new StringBuilder(existingAchievements); // Create new StringBuilder
                        achievementBuild.append(","); // Add a delimiter to the end of it the existing String list
                        achievementBuild.append(dinowrangler);   // Add the new title to the StringBuilder
                        editor.putString("achievement list", achievementBuild.toString());  // Replace old String of titles with new
                        editor.apply();

                        // Get ID reference for node in question
                        String uniqueID =  preferences.getString("uid", null);

                        // Update firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users");
                        myRef.child(uniqueID).child("achievements").setValue(2);
                    }
                } });
            successBuilder.setView(dialogView);    // Set view to success dialog
            successDialog = successBuilder.show();  // Set to show

            // Set the access flag so that it does not show the dialog again
            editor.putBoolean("profile access", false);
            editor.apply();

            // ADD TO LIST OF POINTS TO DISPLAY
            pointsArray.add(walkFinished);  // Add walk finished points total to display

            mSoundPool.play(mBirdsChirping, mVolume, mVolume, 1, 0, 1f); // Pleasant bird in meadow sounds

        } else {
            // ADD RESULTS TO FIREBASE FOR A LOSS
            dataAddAppInstance("T-Rex Tango", "eaten!", preferences.getInt("level", -1));

            // Create the success alert dialog
            AlertDialog.Builder failureBuilder = new AlertDialog.Builder(this);
            LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = startInflater.inflate(R.layout.failure_dialog, null);     // Get dialog view
            failureBuilder.setCancelable(false);
            failureBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    failDialog.dismiss();
                } });
            failureBuilder.setView(dialogView);    // Set view to failure dialog
            failDialog = failureBuilder.show();

            mSoundPool.play(mDinoEating, mVolume, mVolume, 1, 0, 1f); // Being eaten sounds
        }

        // Get results of the bonuses. If there is actually a value, we add this to the key for storage.
        String stepBonus = extras.getString("step bonus");  // Get results of step bonus
        if (!stepBonus.equals("")) pointsArray.add(stepBonus);
        String timeBonus = extras.getString("time bonus");  // Get results of time bonus
        if (!timeBonus.equals("")) pointsArray.add(timeBonus);
        String personalBest = extras.getString("personal best");    // Get results of personal best
        if (!personalBest.equals("")) pointsArray.add(personalBest);

        // XP and level calculator
        int experience = preferences.getInt("xp", -1);  // Retrieve the current xp balance
        int experienceNeeded = 0;
        if (experience < 99) {  // Level 1 if under 99xp
            editor.putInt("level", 1);
            editor.apply();
            experienceNeeded = 100;
        } else if (experience >= 100 && experience < 199) { // Level 2 between this xp threshold
            editor.putInt("level", 2);
            editor.apply();
            experienceNeeded = 200;
        } else if (experience >= 200 && experience < 399) { // Level 3 between this xp threshold
            editor.putInt("level", 3);
            editor.apply();
            experienceNeeded = 399;
        } else if (experience >= 400 && experience < 799) { // Level 4 between this xp threshold
            editor.putInt("level", 4);
            editor.apply();
            experienceNeeded = 799;
        } else if (experience >= 800 && experience < 1599) {    // Level 5 between this xp threshold
            editor.putInt("level", 5);
            editor.apply();
            experienceNeeded = 1599;
        }
        int level = preferences.getInt("level", -1);    // Update the level based on the calculator
        int nextLevel = level + 1;  // Determine what the next level is so we can calculate the progress bar animation requirements

        // Have to convert to double to get percentage value to next level
        double obtainedScore = (double) experience;
        double totalScore = (double) experienceNeeded;
        float percentage = (float) ((obtainedScore*100)/totalScore);    // This is what the progress bar uses to determine how much of itself to display
        int xpToNextLevel = experienceNeeded - experience;  // Used to display to the user how many xp points are needed

        // Percentage text within the progress bar
        int progress = (int) percentage;    // Convert to an int as it will be displayed as a string and we just want the whole number
        TextView xpProgress = (TextView) findViewById(R.id.progress_text);  // Initialize the textview that displays % in numbers
        xpProgress.setText(Integer.toString(progress) + "%");   // Set the text to the percentage calculated

        // Animate the progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progress); // See this max value coming back here, we animate towards that value
        animation.setDuration(5000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // TextView to display current level
        int currentLevel = preferences.getInt("level", -1);
        TextView currentLevelDisplay = (TextView) findViewById(R.id.current_level);
        currentLevelDisplay.setText("LVL " + currentLevel);

        // TextView to display progress information
        TextView progressNextLevel = (TextView) findViewById(R.id.progress_level);
        String progressText = getResources().getString(R.string.to_next_level);
        progressNextLevel.setText(xpToNextLevel + progressText + nextLevel);

        // Main menu activity
        Button mainMenu = (Button) findViewById(R.id.walk);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        // Views to display the points earned during the walk
        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, pointsArray);
        ListView walkExperienceList = (ListView) findViewById(R.id.walk_experience_list);
        walkExperienceList.setAdapter(pointsAdapter);

        // Views to display the statistics calculation and listing (time and steps)
        ArrayList<String> statisticsArray = new ArrayList<>();  // ArrayList for statistics display
        int secondsSession = extras.getInt("seconds summary");  // Get seconds walked this session
        int sessionMinutes = secondsSession / 60;   // Convert to minutes
        String minutesSummary;  // Declare string for minutes summary
        // Check to determine what we are displaying for the time statistic
        if (sessionMinutes < 1) {
            minutesSummary = "Minutes Walked (min) ... < 1 min";    // Do not display fractional time if less than a minute
//            Log.e("walked: ", "< 1 min");
        } else {
            minutesSummary = "Minutes Walked (min) ... " + Integer.toString(sessionMinutes);    // Concatenate string with values
//            Log.e("walked: ", "more than a feeling");
        }
        statisticsArray.add(minutesSummary);    // Add to the array of statistics strings
        int stepsSession = extras.getInt("steps summary"); // Get distance walked this session
        String stepsSummary = "Steps Walked (m) ... " + Integer.toString(stepsSession); // Concatenate string with values
        statisticsArray.add(stepsSummary);   // Add to the array of statistics strings
        ArrayAdapter<String> statisticsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, statisticsArray);
        ListView statisticsSummary = (ListView) findViewById(R.id.statistics_list);
        statisticsSummary.setAdapter(statisticsAdapter);
    }

    /**
     * Do not want users going back to the walk screen, so disable back button.
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Method to update the user's last encounter, last outcome, and level information
     * @param encounter the encounter that just happened
     * @param outcome the outcome of the walk event
     * @param level current level of the user
     */
    public void dataAddAppInstance(String encounter, String outcome, int level) {
        // Get ID reference for node in question
        String uniqueID =  preferences.getString("uid", null);

        // Update firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("lastEncounter").setValue(encounter);
        myRef.child(uniqueID).child("lastOutcome").setValue(outcome);
        myRef.child(uniqueID).child("level").setValue(level);
    }

    /**
     * Method to update the user's achievements
     * @param achievements
     */
    public void dataAddAchievement(int achievements) {
        // Get ID reference for node in question
        String uniqueID =  preferences.getString("uid", null);

        // Update firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("achievements").setValue(2);
    }

}
