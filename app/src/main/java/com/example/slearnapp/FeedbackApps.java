package com.example.slearnapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackApps extends AppCompatActivity {

    EditText feedApp, feedTeacher;
    Button submitButton;

    FirebaseFirestore fstore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_apps);

        fstore = FirebaseFirestore.getInstance();

        feedApp = findViewById(R.id.feedApp);
        feedTeacher = findViewById(R.id.feedTeacher);
        submitButton = findViewById(R.id.btnSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String app = feedApp.getText().toString();
                String teacher = feedTeacher.getText().toString();

                if (app.isEmpty() || teacher.isEmpty()) {
                    Toast.makeText(FeedbackApps.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a unique document ID for each feedback
                DocumentReference documentReference = fstore.collection("feedback").document();

                Map<String, Object> feedback = new HashMap<>();
                feedback.put("FeedApp", app);
                feedback.put("FeedTeacher", teacher);

                documentReference.set(feedback)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(FeedbackApps.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Feedback submitted successfully: " + documentReference.getId());
                            Intent intent = new Intent(FeedbackApps.this, MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(FeedbackApps.this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error submitting feedback: ", e);
                        });
            }
        });
    }
}