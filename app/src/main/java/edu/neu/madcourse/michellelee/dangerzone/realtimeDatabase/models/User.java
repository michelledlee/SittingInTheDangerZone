package edu.neu.madcourse.michellelee.dangerzone.realtimeDatabase.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String title;
    public String lastPlayed;
    public String lastEncounter;
    public String lastOutcome;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String title, String lastPlayed, String lastEncounter, String lastOutcome){
        this.username = username;
        this.title = title;
        this.lastPlayed = lastPlayed;
        this.lastEncounter = lastEncounter;
        this.lastOutcome = lastOutcome;
    }
}