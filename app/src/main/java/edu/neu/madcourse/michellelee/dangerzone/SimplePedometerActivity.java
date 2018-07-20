package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SimplePedometerActivity extends AppCompatActivity implements SensorEventListener, StepListener {
//    private TextView textView;
    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    // Timer
    private boolean isPaused = false;   // Paused status
    private long timeRemaining = 0;     // CountDownTimer remaining time
    private CountDownTimer timer;       // Timer object
    private Button btnPause;
    private Button btnResume;
    private TextView tView;
    private TextView steps;
    private int timerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        textView = new TextView(this);
//        textView.setTextSize(30);
//        setContentView(R.layout.activity_simple_pedometer);
        setContentView(R.layout.activity_walk);

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

        // Default time for CountdownTimer
        long millisInFuture;
        long countDownInterval = 1000; // 1 second

        // Set countdown timer based on selection the user made
        timerTime = getIntent().getIntExtra("timer", -1);
        if (timerTime == 1) {
            millisInFuture = 60000;
        } else if (timerTime == 3) {
            millisInFuture = 180000;
        } else {
            millisInFuture = 300000;
        }

        // Initialize a new CountDownTimer instance
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;

                // Display time in minutes and seconds
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

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
    protected void onDestroy() {
        super.onDestroy();
        // Start walk activity
        Intent endScreenIntent = new Intent(getApplicationContext(), EndWalk.class);
        endScreenIntent.putExtra("steps", numSteps);
        endScreenIntent.putExtra("timer", timerTime);
        startActivity(endScreenIntent);
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



}