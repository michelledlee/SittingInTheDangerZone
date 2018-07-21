package edu.neu.madcourse.michellelee.dangerzone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Vibrator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SimplePedometerActivity extends AppCompatActivity implements SensorEventListener, StepListener {
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

    // sound
    public int mSoundAlert, mBackgroundMusic;
    private SoundPool mSoundPool;
    private float mVolume = 1f;

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
//        textView = new TextView(this);
//        textView.setTextSize(30);
//        setContentView(R.layout.activity_simple_pedometer);
        setContentView(R.layout.activity_walk);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Get an instance of the SensorManager
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

        // build vibrator service & soundpool
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundAlert = mSoundPool.load(getApplicationContext(), R.raw.beep_alert, 1);
        mBackgroundMusic = mSoundPool.load(getApplicationContext(), R.raw.deeper, 1); // music but not hooked up yet


        // Default time for CountdownTimer
        long millisInFuture;
        long countDownInterval = 1000; // 1 second

        // Set countdown timer based on selection the user made
        timerTime = getIntent().getIntExtra("timer", -1);
        if (timerTime == 1) {
//            millisInFuture = 60000;
            millisInFuture = 10000;
            minSteps = 100;
            maxSteps = 140;
        } else if (timerTime == 3) {
            millisInFuture = 180000;
            minSteps = 300;
            maxSteps = 340;
        } else {
            millisInFuture = 300000;
            minSteps = 500;
            maxSteps = 540;
        }

        // Initialize a new CountDownTimer instance
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;

                // Display time in minutes and seconds
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                // change background resource
                if (text.equals("00:30")) {
                    RelativeLayout layout =(RelativeLayout)findViewById(R.id.walk_activity);
                    layout.setBackgroundResource(R.drawable.scenario_end);
                    mSoundPool.play(mSoundAlert, mVolume, mVolume, 1, 0, 1f);
                }

                // flash text & vibrate at set times
                if (text.equals("00:30") || text.equals("00:25") || text.equals("00:20") || text.equals("00:15") ||
                        text.equals("00:10") || text.equals("00:09") || text.equals("00:08") || text.equals("00:07") || text.equals("00:06") ||
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

                // vibrate alert
                v.vibrate(500);
                mSoundPool.play(mSoundAlert, mVolume, mVolume, 1, 0, 1f);

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
//        numSteps = 0;
//        textView.setText(TEXT_NUM_STEPS + numSteps);
        steps.setText(TEXT_NUM_STEPS + numSteps);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
//        textView.setText(TEXT_NUM_STEPS + numSteps);
        steps.setText(TEXT_NUM_STEPS + numSteps);

    }

    /**
     *
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

        // Calculate minutes walked
        double minutes = Double.parseDouble(preferences.getString("minutes walked", null)); // Need to get Double from String
        minutes = minutes + timerTime + bonusTime;  // Add current session's time on to total minutes
        editor.putString("minutes walked", Double.toString(minutes));
        editor.apply();
        endScreenIntent.putExtra("minutes summary", timerTime + bonusTime); // Pass this session's time to the end screen

        // Calculate distance walked
        double distance = numSteps * 0.8;
        endScreenIntent.putExtra("distance summary", distance);
        double existingDistance = Math.round(Double.parseDouble(preferences.getString("distance walked", null)) + distance);
        editor.putString("distance walked", Double.toString(existingDistance));
        editor.apply();

        // Start the end screen activity
        endScreenIntent.putExtra("total points", totalPoints);
        editor.putInt("xp", preferences.getInt("xp", -1) + totalPoints);
        editor.apply();
        startActivity(endScreenIntent);
    }

}