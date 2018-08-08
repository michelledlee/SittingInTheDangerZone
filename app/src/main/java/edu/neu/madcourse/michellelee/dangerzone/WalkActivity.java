package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Vibrator;

import java.util.Random;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * The game activity of the app. It runs a timer for a user specified amount of time and records the steps
 * that the user takes.
 */
public class WalkActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    //  Step counter variables
    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Steps: ";
    private int numSteps;
    private int minSteps;
    private int maxSteps;

    // Timer
    private boolean isPaused = false;   // Paused status
    private long timeRemaining = 0;     // CountDownTimer remaining time
    private CountDownTimer timer;       // Timer object
    private Button btnPause;
    private Button btnResume;
    private TextView tView;
    private TextView steps;
    private int timerTime;

    // Sound
    private MediaPlayer mMediaPlayer;

    // Bonus
    private boolean extraTimeMarker = false;
    private int totalPoints = 0;
    private int bonusTime = 0;

    // Shared preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        // start playing background music on startup
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.danger_zone);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        // Keeps the screen on during the walk activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize Shared Preferences to record game information like steps walked
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Get an instance of the SensorManager that will be used to track the steps during the walk
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);

        // Initializing views for the XML file
        steps = (TextView) findViewById(R.id.step_counter);
        tView = (TextView) findViewById(R.id.timer);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPause.setEnabled(true);  // Pause is enabled on start up
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnResume.setClickable(false);  // Resume is disabled while not paused
        btnResume.setEnabled(false);    // Resume is disabled while not paused

        // Build vibrator service & soundpool that will alert the user at different time intervals
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Default time for CountdownTimer
        long millisInFuture;
        long countDownInterval = 1000; // 1 second

        // Set countdown timer based on selection the user made
        timerTime = getIntent().getIntExtra("timer", -1);
        if (timerTime == 1) {
            millisInFuture = 60000;
            // Target number of steps that is walkable in a minute is between 40 - 50 for the average adult
            minSteps = 40;
            maxSteps = 50;
        } else if (timerTime == 3) {
            millisInFuture = 180000;
            // Target number of steps that is walkable in 3 min is between 100 - 125 for the average adult
            minSteps = 100;
            maxSteps = 125;
        } else {
            millisInFuture = 300000;
            // Target number of steps that is walkable in 5 min is between 210 - 250 for the average adult
            minSteps = 210;
            maxSteps = 250;
        }

        // Initialize a new CountDownTimer instance to display how much time is left during the walk
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;

                // Display time in minutes and seconds
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                // Change background resource to indicate the dinosaur is getting closer
                if (text.equals("00:30")) {
                    RelativeLayout layout =(RelativeLayout)findViewById(R.id.walk_activity);
                    layout.setBackgroundResource(R.drawable.scenario_end);
                }

                // Flash text & vibrate at set times so the user knows that time is about to run out and they can adjust their walk speed
                // At half time
                if (timerTime == 1) {
                    if (text.equals("00:30"))  v.vibrate(500);
                } else if (timerTime == 3) {
                    if (text.equals("01:30")) v.vibrate(500);
                } else {
                    if (text.equals("02:30")) v.vibrate(500);
                }
                // 10 second count down
                if (text.equals("00:10") || text.equals("00:09") || text.equals("00:08") || text.equals("00:07") || text.equals("00:06") ||
                        text.equals("00:05") || text.equals("00:04") || text.equals("00:03") || text.equals("00:02") || text.equals("00:01")) {
                    tView.setTextColor(getResources().getColor(R.color.red_color));
                    v.vibrate(500);
                }
                else tView.setTextColor(getResources().getColor(R.color.white));

                if (isPaused) {             // Cancel current instance if paused
                btnResume.setClickable(true);  // Resume is enabled while paused
                btnResume.setEnabled(true);    // Resume is enabled while paused
                onPause();
                cancel();
                } else {
                    tView.setText(text);    // Display current time set above
                    timeRemaining = millisUntilFinished;    // Store remaining time
                }
            }

            public void onFinish() {
                // Disable the pause and resume button
                btnPause.setEnabled(false);
                btnPause.setClickable(false);
                btnResume.setEnabled(false);
                btnResume.setClickable(false);

                // Vibrate alert
                v.vibrate(500);

                // Perform calculations and actions to start EndWalk screen
                finishTransition();
            }
        }.start();

        // Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            isPaused = true;
            btnPause.setEnabled(false);             // Disable the pause button after being clicked
            btnPause.setClickable(false);           // Disable the pause button after being clicked
            btnResume.setEnabled(true);     // Enable the resume buttons
            btnResume.setClickable(true);   // Enable the resume buttons
            btnResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   // Set the action available for the resume button
                btnPause.setEnabled(true);          // Reenable the pause button
                btnPause.setClickable(true);        // Reenable the pause button
                btnResume.setEnabled(false);        // Disable resume button while the game is actively paused
                btnResume.setClickable(false);      // Disable resume button while the game is actively paused

                isPaused = false;   // Specify the current state is not paused

                // Initialize a new CountDownTimer instance
                long millisInFuture = timeRemaining;
                long countDownInterval = 1000;
                timer = new CountDownTimer(millisInFuture, countDownInterval) {
                    public void onTick(long millisUntilFinished) {
                        long millis = millisUntilFinished;

                        // Display time in minutes and seconds
                        String text = String.format(Locale.getDefault(), "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millis),
                                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                        if (isPaused) { // If paused, cancel the current countdowntimer instance
                            cancel();
                        } else {
                            tView.setText(text);    // As long as the timer is running, update the view with the time
                            timeRemaining = millisUntilFinished;    // Remember time remaining
                        }
                    }

                    public void onFinish() {
                        // Set buttons as disabled once the timer has run out
                        btnPause.setEnabled(false);
                        btnPause.setClickable(false);
                        btnResume.setEnabled(false);
                        btnResume.setClickable(false);

                        // Perform calculations and actions to start EndWalk screen
                        finishTransition();
                    }
                }.start();
                 onResume();
                }
            });
            onPause();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        steps.setText(TEXT_NUM_STEPS + numSteps);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if(mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        else
            return;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        steps.setText(TEXT_NUM_STEPS + numSteps);
    }

    /**
     * After the walk activity has been completed, this method passes information farmed from this
     * activity to the end walk activity for processing and display.
     */
    private void finishTransition() {
        // Will transition to the EndWalk activity, declare intent
        Intent endScreenIntent = new Intent(getApplicationContext(), EndWalk.class);
        endScreenIntent.putExtra("steps", numSteps);
        endScreenIntent.putExtra("timer", timerTime);

        // Finish and extra step bonus calculation; pass point score to EndWalk activity
        // Add the String that we will use to display the points earned
        // Increase totalPoints variable which will be used to calculate the level progression
        Random rand = new Random();
        int stepsNeeded = rand.nextInt((maxSteps - minSteps) + 1) + minSteps;
        if (numSteps == stepsNeeded) {  // Level was completed successfully but no bonus was obtained
            endScreenIntent.putExtra("walk finished", "Walk Finished +25");
            endScreenIntent.putExtra("step bonus", "");
            totalPoints += 25;
        } else if (numSteps > stepsNeeded) { // Level was completed successfully and bonus was obtained
            endScreenIntent.putExtra("walk finished", "Walk Finished +25");
            endScreenIntent.putExtra("step bonus", "Step Bonus +25");
            totalPoints += 50;
        } else {    // Level was failed
            endScreenIntent.putExtra("walk finished", "");
            endScreenIntent.putExtra("step bonus", "");
        }

        // Extra time bonus calculation
        // Add the String that we will use to display the points earned
        // Increase totalPoints variable which will be used to calculate the level progression
        if (extraTimeMarker == true) {
            endScreenIntent.putExtra("time bonus", "Extra Time Bonus +50");
            totalPoints += 50;
        } else {
            endScreenIntent.putExtra("time bonus", "");
        }

        // Calcuating bonus for personal best
        int bestSoFar = preferences.getInt("personal best", -1);
        // Extra time bonus calculation
        // Add the String that we will use to display the points earned
        // Increase totalPoints variable which will be used to calculate the level progression
        if (numSteps > bestSoFar) { // A new personal best was achieved
            endScreenIntent.putExtra("personal best", "Personal Best +75");
            totalPoints += 75;
            editor.putInt("personal best", numSteps);   // Add new personal best to Shared Preferences
            editor.apply();
        } else {    // No personal best achieved this instance
            endScreenIntent.putExtra("personal best", "");
        }

        // Calculate cumulative seconds walked across all sessions
        int seconds = preferences.getInt("seconds walked", -1);
        seconds = seconds + (timerTime * 60) + bonusTime;  // Add current session's time on to total
        editor.putInt("seconds walked", seconds);
        editor.apply();

        endScreenIntent.putExtra("seconds summary", (timerTime * 60) + bonusTime); // Pass this session's time to the end screen

        // Calculate cumulative steps walked
        endScreenIntent.putExtra("steps summary", numSteps);
        int existingSteps = preferences.getInt("steps walked", -1);
        editor.putInt("steps walked", numSteps + existingSteps);
        editor.apply();

        // Start the end screen activity
        endScreenIntent.putExtra("total points", totalPoints);
        editor.putInt("xp", preferences.getInt("xp", -1) + totalPoints);
        editor.apply();
        startActivity(endScreenIntent);
    }


    /**
     * Do not want users going back to the start screen, so disable back button.
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        else
            return;
    }
}