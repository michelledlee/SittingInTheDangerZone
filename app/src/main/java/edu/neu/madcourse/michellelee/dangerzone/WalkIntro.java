package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WalkIntro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_intro);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
            Log.e("radioValue: ", Integer.toString(index));
            // Identify what the timer is
                int timer = 0;
                if (index == 0) {
                    timer = 1;
                } else if (index == 1) {
                    timer = 3;
                } else {
                    timer = 5;
                }

            // Start walk activity
            Intent walkIntent = new Intent(getApplicationContext(), SimplePedometerActivity.class);
            walkIntent.putExtra("timer", timer);
            startActivity(walkIntent);
            }
        });
    }
}
