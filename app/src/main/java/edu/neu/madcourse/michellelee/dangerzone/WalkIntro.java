package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_intro);

        // Hooking up buttons
        final RadioGroup minSelection = (RadioGroup) findViewById(R.id.min_group);
        Button walkButton = (Button) findViewById(R.id.walk);
        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Get user selection for walk time
            int radioButtonID = minSelection.getCheckedRadioButtonId();
            View radioButton = minSelection.findViewById(radioButtonID);
            int index = minSelection.indexOfChild(radioButton);

            // Identify what times the user can select
            int timer = 0;
            if (index == 0) {
                timer = 1;
            } else if (index == 1) {
                timer = 3;
            } else {
                timer = 5;
            }

            // Start walk activity
            Intent walkIntent = new Intent(getApplicationContext(), WalkActivity.class);
            walkIntent.putExtra("timer", timer);
            startActivity(walkIntent);
            }
        });

        // Play dinosaur sounds when entering this screen
        initSound();
    }

    /**
     * Initialize music and sounds for game
     */
    public void initSound() {
        // Load the sounds of the dino approaching and roaring
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mDinoStomp = mSoundPool.load(this, R.raw.trex_roar, 1);
        mTrexRoar = mSoundPool.load(this, R.raw.trex_roar, 1);

        // Play the sounds
        mSoundPool.play(mDinoStomp, mVolume, mVolume, 1, 0, 1f);
        mSoundPool.play(mTrexRoar, mVolume, mVolume, 1, 0, 1f);
    }
}
