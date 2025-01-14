package com.example.slearnapp;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

@Keep
public class FeedbackTeacher {

    @Exclude
    private String id;

    private String feedbackMessage;
    private int rating;
    private String teacherName;
    private String username;
    private Timestamp timestamp;

    public FeedbackTeacher() {
    }

    public FeedbackTeacher(String feedbackMessage, int rating, String teacherName, String username, Timestamp timestamp) {
        this.feedbackMessage = feedbackMessage;
        this.rating = rating;
        this.teacherName = teacherName;
        this.username = username;
        this.timestamp = timestamp;
    }
    // Getters and setters

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    public int getRating() { return rating; }
    public void setScore(int rating) { this.rating = rating; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }



}
