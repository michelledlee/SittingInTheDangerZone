package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InformationInstructionsFragment extends AppCompatActivity {
    private AlertDialog ackDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_information_instructions);

        Button acknowledgmentsButton = (Button) findViewById(R.id.ack_button);
        acknowledgmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder startBuilder = new AlertDialog.Builder(InformationInstructionsFragment.this);
                LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = startInflater.inflate(R.layout.ack_dialog, null);     // Get dialog view
                startBuilder.setCancelable(false);
                startBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ackDialog.dismiss();
                    } });
                startBuilder.setView(dialogView);
                ackDialog = startBuilder.show();

            }
        });
    }
}
