package com.example.slearnapp;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

@Keep
public class FeedbackAssignment {

    @Exclude
    private String id;

    private String assignmentTitle;
    private String feedbackMessage;
    private String score;
    private String teacherName;
    private String username;
    private Timestamp timestamp;

    public FeedbackAssignment() {
    }

    public FeedbackAssignment(String assignmentTitle, String feedbackMessage, String score, String teacherName, String username, Timestamp timestamp) {
        this.assignmentTitle = assignmentTitle;
        this.feedbackMessage = feedbackMessage;
        this.score = score;
        this.teacherName = teacherName;
        this.username = username;
        this.timestamp = timestamp;
    }
    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }



}
