package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import edu.neu.madcourse.michellelee.dangerzone.R;
import edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models.User;

public class ScoreboardActivity extends AppCompatActivity {

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
//    private String date;
//    private TreeMap<Integer, User> sortedScoreTree = new TreeMap(Collections.reverseOrder());    // treemaps are ordered highest to lowest
//    private TreeMap<Integer, User> sortedWordTree = new TreeMap(Collections.reverseOrder());     // treemaps are ordered highest to lowest
    private TreeMap<Double, User> sortedScoreTree = new TreeMap(Collections.reverseOrder());    // treemaps are ordered highest to lowest
    private TreeMap<Double, User> sortedWordTree = new TreeMap(Collections.reverseOrder());     // treemaps are ordered highest to lowest

    // FIREBASE STUFF
    private static final String TAG = ScoreboardActivity.class.getSimpleName();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = mFirebaseDatabase.getReference();
    private DatabaseReference mRef;

    // INSTANCE STUFF
    private String token = FirebaseInstanceId.getInstance().getToken();

    // HIGH SCORE TRACKER
    public static int highestScore = 0;
    public int compareScore = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board);

        // Accessing database contents for this instance only
        rootRef = FirebaseDatabase.getInstance().getReference();
        mRef = rootRef.child(token);

//        final ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // loop over each User in the DataSnapshot
//                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                        User uUser = (User) userSnapshot.getValue(User.class);
//                        if (Integer.parseInt(uUser.score) > compareScore) {
//                            compareScore = Integer.parseInt(uUser.score);
//                        }
//                        if (sortedScoreTree.containsKey(Double.parseDouble(uUser.score))) {
//                            double newKey = Integer.parseInt(uUser.score) + 0.0000000001;
//                            sortedScoreTree.put(newKey, uUser);     // saving contents in score order
//                        } else {
//                            sortedScoreTree.put(Double.parseDouble(uUser.score), uUser);
//                        }
//                        if (sortedWordTree.containsKey(Double.parseDouble(uUser.wordScore))) {
//                            double newKey = Integer.parseInt(uUser.wordScore) + 0.0000000001;
//                            sortedWordTree.put(newKey, uUser);     // saving contents in score order
//                        } else {
//                            sortedWordTree.put(Double.parseDouble(uUser.wordScore), uUser);
//                        }
////                        sortedScoreTree.put(Integer.parseInt(uUser.score), uUser);     // saving contents in score order
////                        sortedWordTree.put(Integer.parseInt(uUser.wordScore), uUser);  // saving contents in word score order
//                }
//
//                int counter = 1;
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
//
//                    // keep track of the highest score
//                    if (Integer.parseInt(value.score) > highestScore) {
//                        highestScore = Integer.parseInt(value.score);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        mRef.addListenerForSingleValueEvent(eventListener);
//
//        // INITIALIZING LIST VIEWS
//        leaderboardRank = (ListView) findViewById(R.id.leaderboard_rank);   // user rank
//        itemRank = new ArrayList<>();
//        adapterRank = new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemRank);
//        leaderboardRank.setAdapter(adapterRank);
//        TextView rankTextView = new TextView(this);
//        rankTextView.setText(R.string.rank_title);
//        rankTextView.setTypeface(null, Typeface.BOLD);
//        rankTextView.setGravity(Gravity.CENTER);
//        leaderboardRank.addHeaderView(rankTextView);
//        leaderboardRank.setScrollContainer(false);
//
//        leaderboardName = (ListView) findViewById(R.id.leaderboard_name);   // user name
//        itemName = new ArrayList<>();
//        adapterName = new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemName);
//        leaderboardName.setAdapter(adapterName);
//        TextView nameTextView = new TextView(this);
//        nameTextView.setText(R.string.name_title);
//        nameTextView.setTypeface(null, Typeface.BOLD);
//        nameTextView.setGravity(Gravity.CENTER);
//        leaderboardName.addHeaderView(nameTextView);
//        leaderboardName.setScrollContainer(false);
//
//        leaderboardScore = (ListView) findViewById(R.id.leaderboard_score); // end score
//        itemScore = new ArrayList<>();
//        adapterScore = new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemScore);
//        leaderboardScore.setAdapter(adapterScore);
//        TextView scoreTextView = new TextView(this);
//        scoreTextView.setText(R.string.endscore_title);
//        scoreTextView.setTypeface(null, Typeface.BOLD);
//        scoreTextView.setGravity(Gravity.CENTER);
//        leaderboardScore.addHeaderView(scoreTextView);
//        leaderboardScore.setScrollContainer(false);
//
//        leaderboardDate = (ListView) findViewById(R.id.leaderboard_date);   // date
//        itemDate = new ArrayList<>();
//        adapterDate = new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemDate);
//        leaderboardDate.setAdapter(adapterDate);
//        TextView dateTextView = new TextView(this);
//        dateTextView.setText(R.string.date_title);
//        dateTextView.setTypeface(null, Typeface.BOLD);
//        dateTextView.setGravity(Gravity.CENTER);
//        leaderboardDate.addHeaderView(dateTextView);
//        leaderboardDate.setScrollContainer(false);
//
//        leaderboardWord = (ListView) findViewById(R.id.leaderboard_word);   // longest word
//        itemWord = new ArrayList<>();
//        adapterWord = new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemWord);
//        leaderboardWord.setAdapter(adapterWord);
//        TextView wordTextView = new TextView(this);
//        wordTextView.setText(R.string.longest_word);
//        wordTextView.setTypeface(null, Typeface.BOLD);
//        wordTextView.setGravity(Gravity.CENTER);
//        leaderboardWord.addHeaderView(wordTextView);
//        leaderboardWord.setScrollContainer(false);
//
//        leaderboardWordScore = (ListView) findViewById(R.id.leaderboard_wordscore); // longest word score
//        itemWordScore = new ArrayList<>();
//        adapterWordScore= new ArrayAdapter<String>(ScoreboardActivity.this, R.layout.word_list_row, R.id.listRowTextView, itemWordScore);
//        leaderboardWordScore.setAdapter(adapterWordScore);
//        TextView wordScoreTextView = new TextView(this);
//        wordScoreTextView.setText(R.string.wordscore_title);
//        wordScoreTextView.setTypeface(null, Typeface.BOLD);
//        wordScoreTextView.setGravity(Gravity.CENTER);
//        leaderboardWordScore.addHeaderView(wordScoreTextView);
//        leaderboardWordScore.setScrollContainer(false);
//
//        // HOOKING UP BUTTONS
//        Button updateButton = (Button) findViewById(R.id.update_leaderboard2);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pullInfo(); // gets new info and updates the view
//            }
//        });
//
//        final Button sortButton = (Button) findViewById(R.id.sort_endscore2);
//        sortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sortButton.setSelected(!sortButton.isSelected());
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
//        // accessing database contents for this instance only
//        rootRef = FirebaseDatabase.getInstance().getReference();
//        mRef = rootRef.child(token);
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
////                        sortedScoreTree.put(Integer.parseInt(uUser.score), uUser);     // saving contents in score order
////                        sortedWordTree.put(Integer.parseInt(uUser.wordScore), uUser);  // saving contents in word score order
//
//                }
//
//                // repull information
//                int counter = 1;
//                for(Map.Entry<Double,User> entry : sortedScoreTree.entrySet()) {
////                for(Map.Entry<Integer,User> entry : sortedScoreTree.entrySet()) {
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
//
//                    // keep track of the highest score
//                    if (Integer.parseInt(value.score) > highestScore) {
//                        highestScore = Integer.parseInt(value.score);
//                    }
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
////            for(Map.Entry<Integer,User> entry : sortedWordTree.entrySet()) {
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
//    public static int getHighestScore() {
//
//        FirebaseDatabase hFirebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference hRootRef = hFirebaseDatabase.getReference();
//        DatabaseReference hRef;
//        String hToken = FirebaseInstanceId.getInstance().getToken();
//        // accessing database contents for this instance only
//        hRootRef = FirebaseDatabase.getInstance().getReference();
//        hRef = hRootRef.child(hToken);
//
//        final ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // loop over each User in the DataSnapshot
//                int highestScore = 0;
//                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User uUser = (User) userSnapshot.getValue(User.class);
//                    int currentScore = Integer.parseInt(uUser.score);
//                    if (currentScore > highestScore) {
//                        highestScore = currentScore;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        hRef.addListenerForSingleValueEvent(eventListener);
//
//        return highestScore;
    }

}