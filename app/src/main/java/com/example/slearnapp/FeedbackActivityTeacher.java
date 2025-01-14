package com.example.slearnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivityTeacher extends AppCompatActivity {

    private Button btnAssignmentFeedbacks, btnTeacherFeedbacks;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_teacher);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Go back to the previous activity
            onBackPressed(); // Or alternatively, finish() could be used
        });
    }
}
