package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

/**
 * Lets the user select a timer for the walk. The user can get to this page from the main menu or
 * from notifications received.
 */
public class WalkIntro extends AppCompatActivity {

    private int mDinoStomp, mTrexRoar;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private Button walkButton;
    private int timerTime;
    private int timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_intro);

        // Load the sounds of the dino approaching and roaring
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mDinoStomp = mSoundPool.load(getApplicationContext(), R.raw.dino_stomp, 1);
        mTrexRoar = mSoundPool.load(getApplicationContext(), R.raw.trex_roar, 1);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
            mSoundPool.play(mDinoStomp, mVolume, mVolume, 1, 0, 1f);
            }
        });

        walkButton = (Button) findViewById(R.id.walk);

        // Used to delay the start time once hte walk activity is initiated so the dinosaur can stop roaring first
        final Handler mHandler = new Handler();

        // Hooking up buttons
        final RadioGroup minSelection = (RadioGroup) findViewById(R.id.min_group);
        minSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
            walkButton.setClickable(true);
            walkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                // Dinosaur roars!
                mSoundPool.play(mTrexRoar, mVolume, mVolume, 1, 0, 1f);

                // Get user selection for walk time
                int radioButtonID = minSelection.getCheckedRadioButtonId();
                View radioButton = minSelection.findViewById(radioButtonID);
                int index = minSelection.indexOfChild(radioButton);

                // Get the time selection so we know how long the walk activity should last
                if (index == 0) {
                    timer = 1;
                } else if (index == 1) {
                    timer = 3;
                } else {
                    timer = 5;
                }

                mHandler.postDelayed(mLaunchTask,3000);
                }
            });
            }
        });
    }

    private Runnable mLaunchTask = new Runnable() {
        public void run() {
            Intent walkIntent = new Intent(getApplicationContext(), WalkActivity.class);
            walkIntent.putExtra("timer", timer);
            startActivity(walkIntent);
        }
    };
}
