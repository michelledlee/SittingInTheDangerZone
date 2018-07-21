package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

public class LeaderboardActivity extends AppCompatActivity {

    // LIST VIEW SET UP
    private ListView leaderboardRank;            // listview for rank
    private ArrayList<String> itemRank;         // word list
    private ArrayAdapter<String> adapterRank;   // string adapter
    private ListView leaderboardName;            // listview for name
    private ArrayList<String> itemName;         // word list
    private ArrayAdapter<String> adapterName;   // string adapter
    private ListView leaderboardDate;            // listview for date
    private ArrayList<String> itemDate;         // word list
    private ArrayAdapter<String> adapterDate;   // string adapter
    private ListView leaderboardScore;            // listview for score
    private ArrayList<String> itemScore;         // word list
    private ArrayAdapter<String> adapterScore;   // string adapter
    private ListView leaderboardWord;            // listview for words
    private ArrayList<String> itemWord;         // word list
    private ArrayAdapter<String> adapterWord;   // string adapter
    private ListView leaderboardWordScore;            // listview for word score
    private ArrayList<String> itemWordScore;         // word list
    private ArrayAdapter<String> adapterWordScore;   // string adapter

    // DATA PULL FROM SNAPSHOT
    private TreeMap<Double, User> sortedScoreTree = new TreeMap(Collections.reverseOrder());    // treemaps are ordered highest to lowest
    private TreeMap<Double, User> sortedWordTree = new TreeMap(Collections.reverseOrder());     // treemaps are ordered highest to lowest

    // FIREBASE STUFF
    private static final String TAG = LeaderboardActivity.class.getSimpleName();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = mFirebaseDatabase.getReference();
    DatabaseReference mRef;
//    DatabaseReference highRef;
    private static final String SERVER_KEY = "key=AAAAtacikow:APA91bF9wWueLW8jH2k9ob-Tl19NN1L8yH9B-37BB5ps8rx9BK2k4J4LN3YsYsEabiMvMLFllcUrrQNG8Dlhkg-CL0Z3gkvD50uDyS0OmovlwFAH2VMmyPo5axZFlnJbzqaF5c5LeUEcMBmxUAU2MJVXNpBTxGQPfA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_board);

//        // accessing database contents
//        rootRef = FirebaseDatabase.getInstance().getReference();
//        mRef = rootRef.child("users");
//
//        // getting initial read of data from database
//        final ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // loop over each User in the DataSnapshot
//                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User uUser = (User) userSnapshot.getValue(User.class);
//                    if (sortedScoreTree.containsKey(Double.parseDouble(uUser.score))) { // if the TreeMap already contains the key
//                        double newKey = Integer.parseInt(uUser.score) + 0.0000000001;   // add really small amount to make it unique
//                        sortedScoreTree.put(newKey, uUser);     // add to TreeMap
//                    } else {    // else add already unique key
//                        sortedScoreTree.put(Double.parseDouble(uUser.score), uUser);
//                    }
//                    if (sortedWordTree.containsKey(Double.parseDouble(uUser.wordScore))) {  // if the TreeMap already contains the key
//                        double newKey = Integer.parseInt(uUser.wordScore) + 0.0000000001;   // add really small amount to make it unique
//                        sortedWordTree.put(newKey, uUser);      // add to TreeMap
//                    } else {    // else add already unique key
//                        sortedWordTree.put(Double.parseDouble(uUser.wordScore), uUser);
//                    }
////                    sortedScoreTree.put(Integer.parseInt(uUser.score), uUser);     // saving contents in score order
////                    sortedWordTree.put(Integer.parseInt(uUser.wordScore), uUser);  // saving contents in word score order
//                }
//
//                int counter = 1;    // counter to only get top 10 entries
////                for(Map.Entry<Integer,User> entry : sortedScoreTree.entrySet()) {
//                for(Map.Entry<Double,User> entry : sortedScoreTree.entrySet()) {
//
//                    if (counter == 10) {    // only adding 10 items
//                        break;
//                    }
//                    User value = entry.getValue();  // only need the value
//
//                    // add items to adapters to show in list view
//                    adapterRank.add(Integer.toString(counter));
//                    adapterName.add(value.username);
//                    adapterDate.add(value.datePlayed);
//                    adapterScore.add(value.score);
//                    adapterWord.add(value.word);
//                    adapterWordScore.add(value.wordScore);
//                    counter++;
//                }
////                Double key = sortedScoreTree.firstKey();
////                currentLeader = sortedScoreTree.get(key);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        mRef.addListenerForSingleValueEvent(eventListener);
//
//        // initialize list view - user rank
//        leaderboardRank = (ListView) findViewById(R.id.leaderboard_rank);
//        itemRank = new ArrayList<>();
//        adapterRank = new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemRank);
//        leaderboardRank.setAdapter(adapterRank);
//        TextView rankTextView = new TextView(this);
//        rankTextView.setText(R.string.rank_title);
//        rankTextView.setTypeface(null, Typeface.BOLD);
//        rankTextView.setGravity(Gravity.CENTER);
//        leaderboardRank.addHeaderView(rankTextView);
//        leaderboardRank.setScrollContainer(false);
//
//        // initialize list view - user name
//        leaderboardName = (ListView) findViewById(R.id.leaderboard_name);
//        itemName = new ArrayList<>();
//        adapterName = new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemName);
//        leaderboardName.setAdapter(adapterName);
//        TextView nameTextView = new TextView(this);
//        nameTextView.setText(R.string.name_title);
//        nameTextView.setTypeface(null, Typeface.BOLD);
//        nameTextView.setGravity(Gravity.CENTER);
//        leaderboardName.addHeaderView(nameTextView);
//        leaderboardName.setScrollContainer(false);
//
//        // initialize list view - end score
//        leaderboardScore = (ListView) findViewById(R.id.leaderboard_score);
//        itemScore = new ArrayList<>();
//        adapterScore = new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemScore);
//        leaderboardScore.setAdapter(adapterScore);
//        TextView scoreTextView = new TextView(this);
//        scoreTextView.setText(R.string.endscore_title);
//        scoreTextView.setTypeface(null, Typeface.BOLD);
//        scoreTextView.setGravity(Gravity.CENTER);
//        leaderboardScore.addHeaderView(scoreTextView);
//        leaderboardScore.setScrollContainer(false);
//
//        // initialize list view - date
//        leaderboardDate = (ListView) findViewById(R.id.leaderboard_date);
//        itemDate = new ArrayList<>();
//        adapterDate = new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemDate);
//        leaderboardDate.setAdapter(adapterDate);
//        TextView dateTextView = new TextView(this);
//        dateTextView.setText(R.string.date_title);
//        dateTextView.setTypeface(null, Typeface.BOLD);
//        dateTextView.setGravity(Gravity.CENTER);
//        leaderboardDate.addHeaderView(dateTextView);
//        leaderboardDate.setScrollContainer(false);
//
//        // initialize list view - longest word
//        leaderboardWord = (ListView) findViewById(R.id.leaderboard_word);
//        itemWord = new ArrayList<>();
//        adapterWord = new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemWord);
//        leaderboardWord.setAdapter(adapterWord);
//        TextView wordTextView = new TextView(this);
//        wordTextView.setText(R.string.longest_word);
//        wordTextView.setTypeface(null, Typeface.BOLD);
//        wordTextView.setGravity(Gravity.CENTER);
//        leaderboardWord.addHeaderView(wordTextView);
//        leaderboardWord.setScrollContainer(false);
//
//        // initialize list view - longest word score
//        leaderboardWordScore = (ListView) findViewById(R.id.leaderboard_wordscore);
//        itemWordScore = new ArrayList<>();
//        adapterWordScore= new ArrayAdapter<String>(LeaderboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemWordScore);
//        leaderboardWordScore.setAdapter(adapterWordScore);
//        TextView wordScoreTextView = new TextView(this);
//        wordScoreTextView.setText(R.string.wordscore_title);
//        wordScoreTextView.setTypeface(null, Typeface.BOLD);
//        wordScoreTextView.setGravity(Gravity.CENTER);
//        leaderboardWordScore.addHeaderView(wordScoreTextView);
//        leaderboardWordScore.setScrollContainer(false);
//
//        // hooking up update button to pull new info from the database
//        Button updateButton = (Button) findViewById(R.id.update_leaderboard);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pullInfo(); // gets new info and updates the view
//            }
//        });
//
//        // hooking up button to sort results
//        final Button sortButton = (Button) findViewById(R.id.sort_endscore);
//        sortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            sortButton.setSelected(!sortButton.isSelected());
//                if (sortButton.isSelected()) {
//                    sortInfoWordScore();    // sort by end score
//                    sortButton.setText(R.string.sort_score);
//                } else {
//                    sortInfoScore();    // sort by word score
//                    sortButton.setText(R.string.sort_wordscore);
//                }
//            }
//        });
//
////        // add event listener to database for new nodes
////        rootRef.child("users").addChildEventListener(new ChildEventListener() {
////            @Override
////            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                    User uUser = (User) dataSnapshot.getValue(User.class);
////                    int newScore = Integer.parseInt(uUser.score);
////                    int oldScore = Integer.parseInt(currentLeader.score);
////                    if (newScore > oldScore) {
//////
////                    }
////
//////                double highestScore = sortedScoreTree.firstKey();
//////                if (uUser > currentLeader) {
//////                    new Thread(new Runnable() {
//////                        @Override
//////                        public void run() {
//////                            sendMessageToNews();
//////                        }
//////                    }).start();
//////                }
////            }
////
////            @Override
////            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////
////            }
////
////            @Override
////            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
////
////            }
////
////            @Override
////            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//    }
//
//    /**
//     * Get current info of database
//     */
//    public void pullInfo() {
//        // clear old list view values
//        adapterRank.clear();
//        adapterName.clear();
//        adapterDate.clear();
//        adapterScore.clear();
//        adapterWord.clear();
//        adapterWordScore.clear();
//
//        // clear maps before populating them again
//        sortedScoreTree.clear();
//        sortedWordTree.clear();
//
//        // get new information
//        final ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // loop over each User in the DataSnapshot
//                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User uUser = (User) userSnapshot.getValue(User.class);
//                    if (sortedScoreTree.containsKey(Double.parseDouble(uUser.score))) {
//                        double newKey = Integer.parseInt(uUser.score) + 0.0000000001;
//                        sortedScoreTree.put(newKey, uUser);     // saving contents in score order
//                    } else {
//                        sortedScoreTree.put(Double.parseDouble(uUser.score), uUser);
//                    }
//                    if (sortedWordTree.containsKey(Double.parseDouble(uUser.wordScore))) {
//                        double newKey = Integer.parseInt(uUser.wordScore) + 0.0000000001;
//                        sortedWordTree.put(newKey, uUser);     // saving contents in score order
//                    } else {
//                        sortedWordTree.put(Double.parseDouble(uUser.wordScore), uUser);
//                    }
////                    sortedScoreTree.put(Integer.parseInt(uUser.score), uUser);     // saving contents in score order
////                    sortedWordTree.put(Integer.parseInt(uUser.wordScore), uUser);  // saving contents in word score order
//                }
//
//                int counter = 1;
////                for(Map.Entry<Integer,User> entry : sortedScoreTree.entrySet()) {
//                for(Map.Entry<Double,User> entry : sortedScoreTree.entrySet()) {
//
//                    if (counter == 10) {
//                        break;
//                    }
//                    User value = entry.getValue();  // only need the value
//
//                    // add items to adapters to show in list view
//                    adapterRank.add(Integer.toString(counter));
//                    adapterName.add(value.username);
//                    adapterDate.add(value.datePlayed);
//                    adapterScore.add(value.score);
//                    adapterWord.add(value.word);
//                    adapterWordScore.add(value.wordScore);
//                    counter++;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        mRef.addListenerForSingleValueEvent(eventListener);
//    }
//
//    /**
//     * Sort by end score
//     */
//    public void sortInfoScore() {
//        // clear old list view values
//        adapterRank.clear();
//        adapterName.clear();
//        adapterDate.clear();
//        adapterScore.clear();
//        adapterWord.clear();
//        adapterWordScore.clear();
//
//        int counter = 1;
//        // repopulate list sorted by end score
//        for(Map.Entry<Double,User> entry : sortedScoreTree.entrySet()) {
////        for(Map.Entry<Integer,User> entry : sortedScoreTree.entrySet()) {
//            if (counter == 10) {    // only get top 10 entries
//                break;
//            }
//            User value = entry.getValue();  // only need the value
//
//            // add items to adapters to show in list view
//
//            adapterRank.add(Integer.toString(counter));
//            adapterName.add(value.username);
//            adapterDate.add(value.datePlayed);
//            adapterScore.add(value.score);
//            adapterWord.add(value.word);
//            adapterWordScore.add(value.wordScore);
//            counter++;
//        }
//    }
//
//    /**
//     * Sort by word score
//     */
//    public void sortInfoWordScore() {
//        // clear old list view values
//        adapterRank.clear();
//        adapterName.clear();
//        adapterDate.clear();
//        adapterScore.clear();
//        adapterWord.clear();
//        adapterWordScore.clear();
//
//        int counter = 1;
//        // repopulate list sorted by word score
//        for(Map.Entry<Double,User> entry : sortedWordTree.entrySet()) {
//
////        for(Map.Entry<Integer,User> entry : sortedWordTree.entrySet()) {
//            if (counter == 10) {    // only get top 10 entries
//                break;
//            }
//            User value = entry.getValue();
//
//            adapterRank.add(Integer.toString(counter));
//            adapterName.add(value.username);
//            adapterDate.add(value.datePlayed);
//            adapterScore.add(value.score);
//            adapterWord.add(value.word);
//            adapterWordScore.add(value.wordScore);
//            counter++;
//        }
//    }
//
////    /**
////     * Button Handler; creates a new thread that sends off a message
////     * @param type
////     */
////    public void sendMessageToNews(View type) {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                sendMessageToNews();
////            }
////        }).start();
////    }
//
//    /**
//     * Sends a message to all other devices
//     */
//    private void sendMessageToNews(){
//        JSONObject jPayload = new JSONObject();
//        JSONObject jNotification = new JSONObject();
//        try {
//            jNotification.put("message", "Leaderboard Activity");
//            jNotification.put("body", "New global high score!");
//            jNotification.put("sound", "default");
//            jNotification.put("badge", "1");
//            jNotification.put("click_action", "OPEN_ACTIVITY_1");
//
//            // Populate the Payload object.
//            // Note that "to" is a topic, not a token representing an app instance
//            jPayload.put("to", "/topics/high_score");
//            jPayload.put("priority", "high");
//            jPayload.put("notification", jNotification);
//
//            // Open the HTTP connection and send the payload
//            URL url = new URL("https://fcm.googleapis.com/fcm/send");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Authorization", SERVER_KEY);
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            // Send FCM message content.
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(jPayload.toString().getBytes());
//            outputStream.close();
//
//            // Read FCM response.
//            InputStream inputStream = conn.getInputStream();
//            final String resp = convertStreamToString(inputStream);
//
//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e(TAG, "run: " + resp);
//                    Toast.makeText(LeaderboardActivity.this,resp, Toast.LENGTH_LONG).show();
//                }
//            });
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Helper function
//     * @param is
//     * @return
//     */
//    private String convertStreamToString(InputStream is) {
//        Scanner s = new Scanner(is).useDelimiter("\\A");
//        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
//

}