package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

public class FriendsList extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        Button deleteFriend = (Button) findViewById(R.id.delete_friends);
        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFriend();
            }
        });

        // Display user ID
        uid = preferences.getString("uid", null);   // Get user ID for this app instance
        TextView myID = (TextView) findViewById(R.id.uuid);
        String useridIntro = getResources().getString(R.string.userid);
        myID.setText(useridIntro + uid);    // Display for user

        // Set up EditText for friend code input
        final EditText enterFriendID = (EditText) findViewById(R.id.edit_text_input);
        Button enterSubmit = (Button) findViewById(R.id.edit_submit);
        enterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendID = enterFriendID.getText().toString();
                addFriend(friendID);
            }
        });
    }

    /**
     *
     * @param friendsID
     */
    private void addFriend(final String friendsID) {

        // Get ID reference for node in question
        String uniqueID = preferences.getString("uid", null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("friends").push().setValue(friendsID);
//        // Accessing database contents
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference mRef = rootRef.child("users");
//
//        // Getting this user's ID
//        final String myUserID = preferences.getString("uid", null);
//        // Getting initial read of data from database
//        final ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Loop over each User in the DataSnapshot
//                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User uUser = (User) userSnapshot.getValue(User.class);
//                    String nodeID = uUser.getUniqueID();    // Get the unique ID for this node
//                    // Get my user node
//                    if (nodeID.equals(myUserID)) {
//                        User myUserNode = uUser;    // Get my node
//                    }
//                    // If the user node unique ID is equal to the one we are looking for, add it to this user's friend list
//                    if (nodeID.equals(friendsID)) {
//                        User friendUser = uUser;    // Get friend node
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        mRef.addListenerForSingleValueEvent(eventListener);
    }

    private void deleteFriend() {

    }

    /**
     * Add friend to this user's friend list in Firebase
     * @param friendID ID of friend to add
     */
    public void dataAddAppInstance(String friendID) {
//        // Get token for this app instance
//        String token = FirebaseInstanceId.getInstance().getToken();

        // Get ID reference for node in question
        String uniqueID = preferences.getString("uid", null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("friends").setValue(friendID, true);
    }
}
