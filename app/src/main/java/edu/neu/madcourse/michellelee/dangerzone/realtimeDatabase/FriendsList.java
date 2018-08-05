package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

public class FriendsList extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String uid;
    private String username;
    private String currentTitle;
    private ArrayList<String> friendArrayList;
    private ArrayAdapter<String> friendAdapter;
    private AlertDialog confirmationDialog;
    private AlertDialog removeDialog;
    private static int sessionDepth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Initialize Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        uid = preferences.getString("uid", null);   // Get user ID for this app instance
        username = preferences.getString("username", null); // get username for this app instance
        currentTitle = preferences.getString("title", null); // get current title for this app instance

        TextView myID = (TextView) findViewById(R.id.uuid);
        TextView myUsername = (TextView) findViewById(R.id.friendslist_username);
        TextView myTitle = (TextView) findViewById(R.id.friendslist_title);

        String displayUID = uid.substring(0,5) + "-" + uid.substring(5); // add hyphen for readibility
        displayUID = displayUID.toUpperCase(); // to uppercase

        String useridIntro = getResources().getString(R.string.userid);
        myID.setText(useridIntro + displayUID);    // Display for user
        myUsername.setText(username); // Display for user
        myTitle.setText(currentTitle);


        // Set up EditText for friend code input
        final EditText enterFriendID = (EditText) findViewById(R.id.edit_text_input);
        Button enterSubmit = (Button) findViewById(R.id.edit_submit);
        enterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempfriendID = enterFriendID.getText().toString();
                String friendID = tempfriendID.replace("-", "");  // remove hyphen if input
                friendID = friendID.toLowerCase(); // lowercase if uppercase input
                checkIfAdded(friendID);
            }
        });

        // Set up remove friends information dialog
        final AlertDialog.Builder removeBuilder = new AlertDialog.Builder(this);
        LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = startInflater.inflate(R.layout.remove_instructions, null);     // Get dialog view
        removeBuilder.setCancelable(false);
        removeBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeDialog.dismiss();
            } });
        removeBuilder.setView(dialogView);    // Set view to initial start dialog

        // Set up info button for removing a friend
        Button removeInformation = (Button) findViewById(R.id.remove_info_button);
        removeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDialog = removeBuilder.show();
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
            final int pos = adapterView.getPositionForView(view);   // Get position of the item that was clicked
            String listEntry = (((TextView) view).getText().toString());    // Get the entry that is to be deleted
            final String uniqueID = listEntry.substring(0, Math.min(listEntry.length(), 10));    // Get the unique friend code to be deleted from Firebase
            Log.e("uniqueID", uniqueID);

            // Confirmation dialog for friend removal
            AlertDialog.Builder startBuilder = new AlertDialog.Builder(FriendsList.this);
            LayoutInflater startInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = startInflater.inflate(R.layout.friend_remove, null);     // Get dialog view
            startBuilder.setCancelable(false);

            // Buttons in alert dialog
            Button yesRemove = (Button) dialogView.findViewById(R.id.remove_friend_ya);
            yesRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationDialog.dismiss();
                    friendArrayList.remove(pos);    // Remove friend at this position that was clicked
                    friendAdapter.notifyDataSetChanged();   // Update friend list for friend removed
                    deleteFriend(uniqueID); // Remove from friend's list on Firebase
                }
            });
            Button noRemove = (Button) dialogView.findViewById(R.id.remove_friend_na);
            noRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationDialog.dismiss();
                }
            });

            startBuilder.setView(dialogView);    // Set view to initial start dialog
            confirmationDialog = startBuilder.show();

            }
        });

        // Set up dummy friend node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid).child("friends");
//        myRef.child("friend").setValue("");

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference().child(uid).child("friends");
//        myRef.child("friend").setValue("");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (sessionDepth == 0) {
            // Do nothing
        } else {
            initializeListAdapter();    // Initialize the list adapter so our friends show each time
        }
        Log.e("on RESTART has","been called");

    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionDepth++;
        if(sessionDepth == 1){  // App came to foreground
            // Do nothing
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {    // App went to background
            // Do nothing
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.e("on START has","been called");
////        initializeListAdapter();    // Initialize the list adapter so our friends show each time
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e("on RESUME has","been called");
////        initializeListAdapter();    // Initialize the list adapter so our friends show each time
//    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
////        initializeListAdapter();    // Initialize the list adapter so our friends show each time
//        Log.e("on POST resume has","been called");
//
//    }

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
                    if (nodeID != null && nodeID.equals(friendsID)) {
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
        DatabaseReference myRef = database.getReference("users").child(uniqueID).child("friends");
        myRef.push().setValue(friendsID);

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
                        String friendInfo = nodeID+ "\nName: "+username+" ("+title+") "+"\nActive: "+lastPlayed+" "+lastEncounter;
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
                    if (nodeID.equals(uid)) {   // If we have found this phone's node
                        User friendUser = userSnapshot.getValue(User.class);         // Get the user for this id
                        Map<String, String> friendList = friendUser.getFriends();   // Get the friends list for this user
                        if (friendList == null) return; // If the user has no friends yet (i.e. first start up or no friends), return as there is nothing to update
                        for (Map.Entry<String, String> entry : friendList.entrySet()) { // If there are items, iterate through
                            if (entry.getValue().equals("") || entry.getValue() == null) {   // If the value is equal to the empty string or empty, skip this entry
                                continue;
                            } else {    // If this value is not empty, add the details of the friend to our list
                                updateFriendsList(entry.getValue());
                            }
                        }
                        friendAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

}
