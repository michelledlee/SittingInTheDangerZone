package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

public class FriendsList extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String uid;
    private ArrayList<String> friendArrayList;
    private ArrayAdapter<String> friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

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
//                addFriend(friendID);
                checkIfAdded(friendID);
            }
        });

        // Set up ListView for friend's list
        friendArrayList = new ArrayList<>();
        friendAdapter = new ArrayAdapter<String>(this, R.layout.list_item_profile, friendArrayList);
        ListView friendsListView = (ListView) findViewById(R.id.list_of_friends);
        friendsListView.setAdapter(friendAdapter);
        initializeListAdapter();    // Update the adapter with the friends this user currently has

        // Make the friend's list clickable to remove a friend
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Remove friend from list
                int pos = adapterView.getPositionForView(view); // Get position of the item that was clicked
                friendArrayList.remove(pos);    // Remove friend at this position that was clicked
                friendAdapter.notifyDataSetChanged();   // Update friend list for friend removed
                // Remove from friend's list on Firebase
                String listEntry = (((TextView) view).getText().toString());
                String uniqueID = listEntry.substring(0, Math.min(listEntry.length(), 8));
                Log.e("uniqueID", uniqueID);
                deleteFriend(uniqueID);
            }
        });
    }

    /**
     * Add a new friend to this user if the friend ID is valid
     * @param friendsID the unique ID code of the friend
     */
    private void addFriend(final String friendsID) {
        // Get ID reference for node in question
        String uniqueID = preferences.getString("uid", null);

        // Accessing database contents
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = rootRef.child("users");

        // Check if ID is valid
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean foundID = false;
                // Loop over each User in the DataSnapshot
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nodeID = userSnapshot.child("uniqueID").getValue(String.class);
                    if (nodeID != null) {
                        foundID = true;
                        break;  // Have found the friend node, can add friend to friend's list
                    }
                }
                // If the foundID is still false, the friend ID is not valid
                if (!foundID) {
                    Toast.makeText(FriendsList.this, "Invalid ID",Toast.LENGTH_LONG).show();    // Indicate to the user the ID was not valid
                    return; // Return as no friend information will be added
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);

        // Add this ID to the user's friend list
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(uniqueID).child("friends").push().setValue(friendsID);

        // Update friends list
        updateFriendsList(friendsID);
    }

    /**
     * Check if this friendID has already been added to friends. If the ID has not been added,
     *  the add friend ID is called.
     * @param friendsID friend ID to check if added
     */
    private void checkIfAdded(final String friendsID) {
        // Accessing database contents
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = rootRef.child("users");

        // Getting initial read of data from database
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop over each User in the DataSnapshot
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nodeID = userSnapshot.child("uniqueID").getValue(String.class);    // Get the unique ID for this node
                    // If this friendsID is already in the list, do not add it again
                    if (nodeID != null && nodeID.equals(uid)) {
                        if (userSnapshot.child("friends").getValue() instanceof Map) {  // If there is an existing friend's list with 1+ friends, it is stored as a map
                            // Iterate through existing friends by obtaining the "friends" map
                            Map<String, String> map = (Map<String, String>) userSnapshot.child("friends").getValue();
                            if (map == null || !map.containsValue(friendsID)) { // If the map isn't empty and does not already contain this friend
                                addFriend(friendsID);   // Add this friend
                            } else { // Already friends with this person, do not add
                                Toast.makeText(FriendsList.this, "Friend already added", Toast.LENGTH_LONG).show(); // Let the user know that the friend was already added
                            }
                        } else {    // Add the user's very first friend
                            addFriend(friendsID);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

    /**
     * Get information for the friend ID passed in and add to friend's list
     * @param friendsID
     */
    private void updateFriendsList(final String friendsID) {
        // Accessing database contents
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = rootRef.child("users");

        // Getting initial read of data from database
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop over each User in the DataSnapshot
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nodeID = userSnapshot.child("uniqueID").getValue(String.class);    // Get the unique ID for this node
                    // If the user node unique ID is equal to the one we are looking for, add it to this user's friend list
                    if (nodeID != null && nodeID.equals(friendsID)) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        String title = userSnapshot.child("title").getValue(String.class);
                        String lastPlayed = userSnapshot.child("lastPlayed").getValue(String.class);
                        String lastEncounter = userSnapshot.child("lastEncounter").getValue(String.class);
                        String friendInfo = nodeID+username+title+lastPlayed+lastEncounter;
                        friendAdapter.add(friendInfo);
                        break;  // Have found the friend node, can populate friend information
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

    /**
     * Remove friend from friend's list
     * @param friendsID the unique ID of the friend to delete
     */
    private void deleteFriend(final String friendsID) {
//        // Accessing database contents
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//        mRef.removeValue();

        // Accessing database contents
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = rootRef.child("users");    // Users level reference

        // Getting initial read of data from database
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop over each User in the DataSnapshot
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nodeID = userSnapshot.child("uniqueID").getValue(String.class);    // Get the unique ID for this node
                    if (nodeID != null && nodeID.equals(uid)) { // If this is the node we need the friend's list from
                        Map<String, String> map = (Map<String, String>) userSnapshot.child("friends").getValue();   // Get the friends list for this node
                        if (map != null || map.containsValue(friendsID)) {  // If the friends list is not null and contains the friend's ID
                            for (Map.Entry<String, String> entry : map.entrySet()) { // Iterate through this map to find the friend to delete
                                Log.e("entry value", entry.getValue());
                                if (entry.getValue().equals(friendsID)) {    // If this entry is the friend that is to be deleted
                                    Log.e("found id", entry.getValue());
                                    String theKey = entry.getKey(); // Get the key for this friend
                                    Log.e("the key", entry.getKey());
                                    DatabaseReference friendRef = rootRef.child("users").child(uid).child("friends").child(theKey); // Get a reference to this key
                                    friendRef.removeValue(); // Remove the value at this level
                                    friendRef.setValue(null);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);

    }

    /**
     * Initialize the adapter with the friends that are in Firebase for this user
     */
    private void initializeListAdapter() {
        // Accessing database contents
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef = rootRef.child("users");

        // Getting initial read of data from database
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop over each User in the DataSnapshot
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nodeID = userSnapshot.child("uniqueID").getValue(String.class);    // Get the unique ID for this node
                    if (nodeID != null && nodeID.equals(uid)) {
//                        if (userSnapshot.child("uniqueID").child("friends").getValue() instanceof String) {   // Case if no friends have been added yet
//                            Toast.makeText(FriendsList.this, "Add new friends!",Toast.LENGTH_LONG).show();
//                            break;
//                        } else if (userSnapshot.child("uniqueID").child("friends").getValue() instanceof Map) {    // If this friendsID is already in the list, do not add it again
                        if (userSnapshot.child("uniqueID").child("friends").getValue() instanceof Map) {
                        // Iterate through existing friends
                            Map<String, String> map = (Map<String, String>) userSnapshot.child("friends").getValue();
                            // For each friend, add to the list
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                friendAdapter.add(entry.getValue());
                            }
                            break;
                        } else {
                            break;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

}
