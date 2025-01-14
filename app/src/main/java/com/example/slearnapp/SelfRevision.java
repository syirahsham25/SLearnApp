package com.example.slearnapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelfRevision extends AppCompatActivity {

    private LinearLayout coursesContainer;
    private Button addCourseButton;
    private ImageButton backButton;
    private ArrayList<String> coursesList = new ArrayList<>();
    private static final String PREFS_KEY_COURSES = "courses";
    private static final long DOUBLE_CLICK_DELAY = 300; // Delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_revision);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Load any existing courses
        loadExistingCourses();
    }

    private void initializeViews() {
        coursesContainer = findViewById(R.id.courses_container);
        addCourseButton = findViewById(R.id.add_course);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Add Course Button Click Listener
        addCourseButton.setOnClickListener(v -> showAddCourseDialog());

        // Back Button Click Listener
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Course");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter course name");
        input.setPadding(32, 32, 32, 32);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String courseName = input.getText().toString().trim();
            if (!courseName.isEmpty()) {
                addCourse(courseName);
            } else {
                Toast.makeText(this, "Course name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void addCourse(String courseName) {
        // Create new button for the course
        Button courseButton = createCourseButton(courseName);

        // Add to container
        coursesContainer.addView(courseButton);

        // Save the course
        coursesList.add(courseName);
        saveCourses();
    }

    private Button createCourseButton(String courseName) {
        Button courseButton = new Button(this);
        courseButton.setText(courseName);
        courseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.course_button_color)));

        // Set button styling
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        courseButton.setLayoutParams(params);
        courseButton.setPadding(48, 48, 48, 48);
        courseButton.setTextSize(18);

        // Implement double-click functionality
        final Handler handler = new Handler();
        final int[] clickCount = {0};

        courseButton.setOnClickListener(v -> {
            clickCount[0]++;

            handler.postDelayed(() -> {
                if (clickCount[0] == 1) {
                    // Single click: Navigate to SelfRevisionCourse
                    Intent intent = new Intent(SelfRevision.this, SelfRevisionCourse.class);
                    intent.putExtra("course_name", courseName); // Pass the course name to the next activity
                    startActivity(intent);
                } else if (clickCount[0] == 2) {
                    // Double click: Show delete course dialog
                    showDeleteCourseDialog(courseName, courseButton);
                }
                clickCount[0] = 0;
            }, DOUBLE_CLICK_DELAY);
        });

        return courseButton;
    }

    private void showDeleteCourseDialog(String courseName, Button courseButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Course")
                .setMessage("Are you sure you want to delete \"" + courseName + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove course from the list and container
                    coursesList.remove(courseName);
                    coursesContainer.removeView(courseButton);

                    // Save updated courses list
                    saveCourses();

                    Toast.makeText(this, "Course \"" + courseName + "\" deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void loadExistingCourses() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Set<String> savedCourses = prefs.getStringSet(PREFS_KEY_COURSES, new HashSet<>());
        coursesList = new ArrayList<>(savedCourses);

        // Sort courses alphabetically
        Collections.sort(coursesList);

        // Add buttons for existing courses
        for (String courseName : coursesList) {
            Button courseButton = createCourseButton(courseName);
            coursesContainer.addView(courseButton);
        }
    }

    private void saveCourses() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PREFS_KEY_COURSES, new HashSet<>(coursesList));
        editor.apply();
    }
}
