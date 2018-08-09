package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String username;
    private String title;
    private String lastPlayed;
    private String lastEncounter;
    private String lastOutcome;
    private String uniqueID;
    private int level;
    private Map<String, String> friends;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

        public User(String username, String title, String lastPlayed, String lastEncounter, String lastOutcome, String uniqueID, int level, Map<String, String> friends){
        this.username = username;
        this.title = title;
        this.lastPlayed = lastPlayed;
        this.lastEncounter = lastEncounter;
        this.lastOutcome = lastOutcome;
        this.uniqueID = uniqueID;
        this.level = level;
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getLastPlayed() {
        return lastPlayed;
    }

    public String getLastEncounter() {
        return lastEncounter;
    }

    public String getLastOutcome() {
        return lastOutcome;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int getLevel() {
        return level;
    }

    public Map<String, String> getFriends() {
        return friends;
    }
}