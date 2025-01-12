package com.example.slearnapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

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

    // Added fields for swipe and undo functionality
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private View lastDeletedView;
    private String lastDeletedCourseName;
    private int lastDeletedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_revision);

        initializeViews();
        setupClickListeners();
        loadExistingCourses();
    }

    private void initializeViews() {
        coursesContainer = findViewById(R.id.coursesContainer);
        addCourseButton = findViewById(R.id.add_course);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        addCourseButton.setOnClickListener(v -> showAddCourseDialog());
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Course");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter course name");
        input.setPadding(32, 32, 32, 32);
        builder.setView(input);

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
        MaterialButton courseButton = createCourseButton(courseName);
        coursesContainer.addView(courseButton);
        coursesList.add(courseName);
        saveCourses();
    }

    private MaterialButton createCourseButton(String courseName) {
        MaterialButton courseButton = new MaterialButton(this);
        courseButton.setText(courseName);
        courseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.course_button_color)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        courseButton.setLayoutParams(params);
        courseButton.setPadding(48, 48, 48, 48);
        courseButton.setTextSize(18);

        final Handler handler = new Handler();
        final int[] clickCount = {0};
        final long[] lastClickTime = {0};
        final float[] startX = {0};
        final boolean[] isSwipe = {false};

        courseButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX[0] = event.getX();
                    isSwipe[0] = false;
                    long clickTime = System.currentTimeMillis();

                    if (clickTime - lastClickTime[0] < DOUBLE_CLICK_DELAY) {
                        // Double click detected
                        clickCount[0] = 0;
                        lastClickTime[0] = 0;
                        showDeleteCourseDialog(courseName, courseButton);
                        return true;
                    }

                    lastClickTime[0] = clickTime;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = startX[0] - event.getX();
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        isSwipe[0] = true;
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float finalDeltaX = startX[0] - endX;

                    if (isSwipe[0] && Math.abs(finalDeltaX) > MIN_DISTANCE && finalDeltaX > 0) {
                        // Swipe left detected
                        lastDeletedView = courseButton;
                        lastDeletedCourseName = courseName;
                        lastDeletedPosition = coursesContainer.indexOfChild(courseButton);

                        courseButton.animate()
                                .translationX(-courseButton.getWidth())
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction(() -> {
                                    coursesContainer.removeView(courseButton);
                                    coursesList.remove(courseName);
                                    saveCourses();
                                    showUndoSnackbar();
                                });
                    } else if (!isSwipe[0]) {
                        // Single click detected
                        Intent intent = new Intent(SelfRevision.this, SelfRevisionCourse.class);
                        intent.putExtra("course_name", courseName);
                        startActivity(intent);
                    }
                    return true;
            }
            return false;
        });

        return courseButton;
    }

    private void showDeleteCourseDialog(String courseName, MaterialButton courseButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Course")
                .setMessage("Are you sure you want to delete \"" + courseName + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    lastDeletedView = courseButton;
                    lastDeletedCourseName = courseName;
                    lastDeletedPosition = coursesContainer.indexOfChild(courseButton);

                    courseButton.animate()
                            .translationX(-courseButton.getWidth())
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(() -> {
                                coursesList.remove(courseName);
                                coursesContainer.removeView(courseButton);
                                saveCourses();
                                showUndoSnackbar();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showUndoSnackbar() {
        View snackbarView = findViewById(android.R.id.content);
        if (snackbarView != null) {
            Snackbar.make(snackbarView, "Course deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        try {
                            if (lastDeletedView != null && lastDeletedCourseName != null) {
                                lastDeletedView.setTranslationX(0f);
                                lastDeletedView.setAlpha(1f);
                                coursesContainer.addView(lastDeletedView, lastDeletedPosition);
                                coursesList.add(lastDeletedPosition, lastDeletedCourseName);
                                saveCourses();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to restore course", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
    }

    private void loadExistingCourses() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Set<String> savedCourses = prefs.getStringSet(PREFS_KEY_COURSES, new HashSet<>());
        coursesList = new ArrayList<>(savedCourses);
        Collections.sort(coursesList);

        for (String courseName : coursesList) {
            MaterialButton courseButton = createCourseButton(courseName);
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