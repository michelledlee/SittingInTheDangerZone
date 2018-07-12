package edu.neu.madcourse.michellelee.dangerzone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainControlsFragment extends Fragment {
    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_controls, container, false);

        // Walk button
        View walkButton = rootView.findViewById(R.id.walk_button);  // create button view
        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent walkActivityIntent = new Intent(getActivity(), WalkActivity.class);
                startActivity(walkActivityIntent);
            }
        });

        // Settings button
        View settingsButton = rootView.findViewById(R.id.settings_button);  // create button view
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        // Profile button
        View profileButton = rootView.findViewById(R.id.profile_button);  // create button view
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws RuntimeException {
                Intent settingsIntent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(settingsIntent);
            }
        });


        // Information button
        View infoButton = rootView.findViewById(R.id.instructions_button);  // create button view
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getActivity(), InformationInstructionsFragment.class);
                startActivity(settingsIntent);
            }
        });
        return rootView;
    }
}
