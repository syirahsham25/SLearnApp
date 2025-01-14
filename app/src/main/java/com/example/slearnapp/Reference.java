package com.example.slearnapp;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

@Keep
public class Reference {

    @Exclude
    private String id;

    private String resourceName;
    private String resourceURL;
    private String subject;
    private String username;
    private Timestamp timestamp;

    public Reference() {
    }

    public Reference(String resourceName, String resourceURL, String subject, String username, Timestamp timestamp) {
        this.resourceName = resourceName;
        this.resourceURL = resourceURL;
        this.subject = subject;
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
