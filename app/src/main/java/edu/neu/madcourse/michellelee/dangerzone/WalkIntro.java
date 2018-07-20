package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WalkIntro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hooking up buttons
        final RadioGroup minSelection = (RadioGroup) findViewById(R.id.min_group);
        Button walkButton = (Button) findViewById(R.id.walk_button);
        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Get user selection for walk time
            String radioValue = ((RadioButton)findViewById(minSelection.getCheckedRadioButtonId())).getText().toString();

            // Start walk activity
            Intent walkIntent = new Intent(getApplicationContext(), WalkActivity.class);
            startActivity(walkIntent);
            }
        });
    }
}
