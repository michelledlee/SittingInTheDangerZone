package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Context;
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
    private TextView textView;
    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    // Timer
    private boolean isPaused = false;   // paused status
    private long timeRemaining = 0;     // CountDownTimer remaining time
    private CountDownTimer timer;       // timer object
    private Button btnPause;
    private TextView tView;
    private TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        textView.setTextSize(30);
//        setContentView(R.layout.activity_simple_pedometer);
        setContentView(R.layout.activity_walk);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);

        steps = (TextView) findViewById(R.id.step_counter);

//        final TextView tView = (TextView) findViewById(R.id.timer);
//        final Button btnPause = (Button) findViewById(R.id.btn_pause);
        tView = (TextView) findViewById(R.id.timer);
        btnPause = (Button) findViewById(R.id.btn_pause);

        // before start, pause is disabled
        btnPause.setEnabled(false);

        // once started, pause button is active
        btnPause.setEnabled(true);

        long millisInFuture = 90000;  // 1.5 minutes
        long countDownInterval = 1000; // 1 second

        // Initialize a new CountDownTimer instance
        timer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;

                if (millis >= 10000 && millis <= 11000) {                                       // if time is 10 seconds
                    Toast.makeText(SimplePedometerActivity.this, "FINAL COUNTDOWN", Toast.LENGTH_LONG).show();    // entering final countdown
                }

                // display time in minutes and seconds
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                if (isPaused) {   // cancel current instance if paused
                    cancel();
                } else {
                    tView.setText(text);        // display current time set above
                    timeRemaining = millisUntilFinished;    // store remaining time
                }
            }

            public void onFinish() {
//                if (phase == 1) {
//                    phase = 2;
//                    phaseTwo();
//                } else if (phase == 2) {
//                    gameOver(); // game over dialog
//                }

                //Disable the pause, resume and cancel button
                btnPause.setEnabled(false);     // on finish, pause does not work
            }
        }.start();

        // Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = true;
                btnPause.setEnabled(false); // disable the pause button

//                AlertDialog.Builder builder = new AlertDialog.Builder(SimplePedometerActivity.this);
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View dialogView = inflater.inflate(R.layout.paused_screen, null);     // create view for custom dialog
//                builder.setCancelable(false);
//                builder.setView(dialogView);    // set view to paused screen layout
                Button resume = (Button) findViewById(R.id.btn_resume);
                resume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPause.setEnabled(true);   // enable the pause button

                        // specify the current state is not paused
                        isPaused = false;

                        // initialize a new CountDownTimer instance
                        long millisInFuture = timeRemaining;
                        long countDownInterval = 1000;
                        timer = new CountDownTimer(millisInFuture, countDownInterval) {
                            public void onTick(long millisUntilFinished) {
                                long millis = millisUntilFinished;

                                if (millis >= 10000 && millis <= 11000) {                                       // if time is 10 seconds
                                    Toast.makeText(SimplePedometerActivity.this, "FINAL COUNTDOWN", Toast.LENGTH_LONG).show();    // entering final countdown
                                }

                                // display time in minutes and seconds
                                String text = String.format(Locale.getDefault(), "%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(millis),
                                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                                if (isPaused) { // pause requested
                                    cancel();
                                } else {
                                    tView.setText(text);
                                    timeRemaining = millisUntilFinished;    // remember time remaining
                                }
                            }

                            public void onFinish() {
//                                if (phase == 1) {
//                                    phase = 2;
//                                    phaseTwo();
//                                } else if (phase == 2) {
//                                    gameOver(); // game over dialog
//                                }

                                // disable all buttons
                                btnPause.setEnabled(false);
                            }
                        }.start();
                    }
                });
//                mDialog = builder.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        numSteps = 0;
        textView.setText(TEXT_NUM_STEPS + numSteps);
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
        textView.setText(TEXT_NUM_STEPS + numSteps);
    }

}