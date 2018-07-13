package edu.neu.madcourse.michellelee.dangerzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Settings button
        View settingsButton = findViewById(R.id.settings_button);  // create button view
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

    }

}