package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class Token extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if(refreshedToken != null) {
            Log.d("TOKENTAG", "Refreshed token: " + refreshedToken);
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("pref_id", 0);
            prefs.edit().putString("share_pref_token", refreshedToken).apply();
        }
    }
}
