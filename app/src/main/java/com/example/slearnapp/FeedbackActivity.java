package com.example.slearnapp;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private Button btnFeedbackApp, btnFeedbackAssignment;
    private ImageButton backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

//        // Initialize the buttons after the layout is set
        btnFeedbackAssignment = findViewById(R.id.btnAssignmentFeedbacks);
        btnFeedbackApp = findViewById(R.id.btnAppFeedbacks);
        backButton = findViewById(R.id.backButton);
//
//        // Set OnClickListener for the btnTeacherFeedbacks button
//        btnFeedbackTeacher.setOnClickListener(v -> {
//            Log.d("FeedbackActivity", "Teacher Feedback button clicked");
//            // Replace this with the correct activity you want to navigate to
//            Intent intent = new Intent(FeedbackActivity.this, FeedbackApps.class);
//            startActivity(intent);
//        });
//
        // Set OnClickListener for the btnAssignmentFeedbacks button
        btnFeedbackAssignment.setOnClickListener(v -> {
//            Log.d("FeedbackActivity", "Assignment Feedback button clicked");
            // Replace this with the correct activity you want to navigate to
            Intent intent = new Intent(FeedbackActivity.this, FeedbackActivityAssignment.class);
            startActivity(intent);
        });
        // Set OnClickListener for the btnAssignmentFeedbacks button
        btnFeedbackApp.setOnClickListener(v -> {
            // Replace this with the correct activity you want to navigate to
            Intent intent = new Intent(FeedbackActivity.this, FeedbackApps.class);
            startActivity(intent);
        });
//
        backButton.setOnClickListener(v -> {
            // Go back to the previous activity
            onBackPressed(); // Or alternatively, finish() could be used
        });

    }
}
