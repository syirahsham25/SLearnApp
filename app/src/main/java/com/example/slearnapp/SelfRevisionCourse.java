package com.example.slearnapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelfRevisionCourse extends AppCompatActivity {
    private TextView courseTitle;
    private GridLayout topicsGrid;
    private Button addTopicButton;
    private ImageButton backButton;
    private String courseName;
    private ArrayList<String> topicsList;
    private View lastDeletedView;
    private String lastDeletedTopicName;
    private int lastDeletedPosition;
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private static final String SHARED_PREFS_NAME = "courses_data";
    private static final String KEY_TOPICS_PREFIX = "topics_for_course_";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_revision_course);

        initializeViews();
        courseName = getIntent().getStringExtra("course_name");
        topicsList = new ArrayList<>();

        if (courseName != null) {
            courseTitle.setText(courseName);
        }

        setupClickListeners();
        loadExistingTopics();
    }

    private void initializeViews() {
        topicsGrid = findViewById(R.id.topics_grid);
        addTopicButton = findViewById(R.id.add_topic);
        backButton = findViewById(R.id.backButton);
        courseTitle = findViewById(R.id.textView8);
    }

    private void setupClickListeners() {
        courseTitle.setOnClickListener(v -> showEditCourseNameDialog());
        addTopicButton.setOnClickListener(v -> showAddTopicDialog());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadExistingTopics() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        Set<String> savedTopics = prefs.getStringSet(KEY_TOPICS_PREFIX + courseName, new HashSet<>());
        topicsList = new ArrayList<>(savedTopics != null ? savedTopics : new HashSet<>());
        Collections.sort(topicsList);

        // Clear existing views
        topicsGrid.removeAllViews();

        // Add topics to grid
        for (String topicName : topicsList) {
            addTopicToGrid(topicName);
        }
    }

    private void showAddTopicDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_topic, null);

        EditText etTopicInput = dialogView.findViewById(R.id.etTopicInput);
        Button btnSaveTopic = dialogView.findViewById(R.id.btnSaveTopic);
        Button btnCancelTopic = dialogView.findViewById(R.id.btnCancelTopic);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        btnSaveTopic.setOnClickListener(v -> {
            String topicName = etTopicInput.getText().toString().trim();
            if (!topicName.isEmpty()) {
                if (!topicsList.contains(topicName)) {
                    addTopicToGrid(topicName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Topic already exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Topic name is required", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelTopic.setOnClickListener(v -> dialog.dismiss());

        etTopicInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etTopicInput, InputMethodManager.SHOW_IMPLICIT);

        dialog.show();
    }

    private void addTopicToGrid(String topicName) {
        LinearLayout topicLayout = new LinearLayout(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        topicLayout.setLayoutParams(params);
        topicLayout.setOrientation(LinearLayout.VERTICAL);
        topicLayout.setGravity(Gravity.CENTER);

        MaterialButton topicButton = new MaterialButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        topicButton.setLayoutParams(buttonParams);
        topicButton.setText(topicName);
        topicButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.topic_button_color)));
        topicButton.setCornerRadius(48);

        // Handle swipe and click
        topicButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    return false;

                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x1 - x2;

                    if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0) {
                        // Handle swipe delete
                        lastDeletedView = topicLayout;
                        lastDeletedTopicName = topicName;
                        lastDeletedPosition = topicsGrid.indexOfChild(topicLayout);

                        topicLayout.animate()
                                .translationX(-topicLayout.getWidth())
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction(() -> {
                                    topicsGrid.removeView(topicLayout);
                                    topicsList.remove(topicName);
                                    saveTopics();
                                    showUndoSnackbar();
                                });
                        return true;
                    }
                    return false;
            }
            return false;
        });

        // Navigate to topic on click
        topicButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelfRevisionCourse.this, SelfRevisionTopic.class);
            intent.putExtra("course_name", courseName);
            intent.putExtra("topic_name", topicName);
            startActivity(intent);
        });

        topicLayout.addView(topicButton);
        topicsGrid.addView(topicLayout);

        if (!topicsList.contains(topicName)) {
            topicsList.add(topicName);
            saveTopics();
        }
    }

    private void showEditCourseNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Course Name");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(courseName);
        input.setSelection(input.getText().length());
        input.setPadding(32, 32, 32, 32);

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                String oldName = courseName;
                courseName = newName;
                courseTitle.setText(courseName);
                updateCourseName(oldName, newName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showUndoSnackbar() {
        View snackbarView = findViewById(android.R.id.content);
        if (snackbarView != null) {
            Snackbar.make(snackbarView, "Topic deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        if (lastDeletedView != null && lastDeletedTopicName != null) {
                            lastDeletedView.setTranslationX(0f);
                            lastDeletedView.setAlpha(1f);
                            topicsGrid.addView(lastDeletedView, lastDeletedPosition);
                            topicsList.add(lastDeletedPosition, lastDeletedTopicName);
                            saveTopics();
                        }
                    })
                    .show();
        }
    }

    private void saveTopics() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_TOPICS_PREFIX + courseName, new HashSet<>(topicsList));
        editor.apply();
    }

    private void updateCourseName(String oldName, String newName) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save new course name
        editor.putString("course_name_" + newName, newName);

        // Move topics to new key
        Set<String> topics = prefs.getStringSet(KEY_TOPICS_PREFIX + oldName, new HashSet<>());
        editor.putStringSet(KEY_TOPICS_PREFIX + newName, topics);
        editor.remove(KEY_TOPICS_PREFIX + oldName);

        // Update all associated topic data
        for (String topic : topicsList) {
            String oldTopicKey = oldName + "_" + topic;
            String newTopicKey = newName + "_" + topic;

            // Move topic content
            String content = prefs.getString("topic_content_" + oldTopicKey, null);
            if (content != null) {
                editor.putString("topic_content_" + newTopicKey, content);
                editor.remove("topic_content_" + oldTopicKey);
            }

            // Move notes
            int noteCounter = prefs.getInt("title_counter_" + oldTopicKey, 0);
            editor.putInt("title_counter_" + newTopicKey, noteCounter);
            editor.remove("title_counter_" + oldTopicKey);

            for (int i = 1; i <= noteCounter; i++) {
                String noteId = prefs.getString("note_id_" + oldTopicKey + "_" + i, null);
                if (noteId != null) {
                    // Update note ID reference
                    editor.putString("note_id_" + newTopicKey + "_" + i, noteId);
                    editor.remove("note_id_" + oldTopicKey + "_" + i);

                    // Move note content and title
                    String noteContent = prefs.getString("note_content_" + oldName + "_" + noteId, null);
                    String titleText = prefs.getString("title_text_" + oldName + "_" + noteId, null);

                    if (noteContent != null) {
                        editor.putString("note_content_" + newName + "_" + noteId, noteContent);
                        editor.remove("note_content_" + oldName + "_" + noteId);
                    }
                    if (titleText != null) {
                        editor.putString("title_text_" + newName + "_" + noteId, titleText);
                        editor.remove("title_text_" + oldName + "_" + noteId);
                    }
                }
            }
        }

        editor.remove("course_name_" + oldName);
        editor.apply();
    }

    public void deleteCourse() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Remove course name
        editor.remove("course_name_" + courseName);

        // Remove all topics for this course
        editor.remove(KEY_TOPICS_PREFIX + courseName);

        // Remove all topic and note data
        for (String topic : topicsList) {
            String topicKey = courseName + "_" + topic;
            editor.remove("topic_content_" + topicKey);

            int noteCounter = prefs.getInt("title_counter_" + topicKey, 0);
            for (int i = 1; i <= noteCounter; i++) {
                String noteId = prefs.getString("note_id_" + topicKey + "_" + i, null);
                if (noteId != null) {
                    editor.remove("note_content_" + courseName + "_" + noteId);
                    editor.remove("title_text_" + courseName + "_" + noteId);
                    editor.remove("note_id_" + topicKey + "_" + i);
                }
            }
            editor.remove("title_counter_" + topicKey);
        }

        editor.apply();
    }
}