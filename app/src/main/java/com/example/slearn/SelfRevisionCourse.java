package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import java.util.ArrayList;



public class SelfRevisionCourse extends AppCompatActivity {
    private TextView courseTitle;
    private GridLayout topicsGrid;
    private Button addTopicButton;
    private ImageButton backButton;
    private BottomNavigationView bottomNavigationView;
    private String courseName;
    private ArrayList<String> topicsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_revision_course);

        // Initialize views
        initializeViews();

        // Get course name from intent
        courseName = getIntent().getStringExtra("course_name");
        courseTitle.setText(courseName);

        // Set up click listeners
        setupClickListeners();

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        topicsGrid = findViewById(R.id.topics_grid);
        addTopicButton = findViewById(R.id.add_topic);
        backButton = findViewById(R.id.backButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        courseTitle = findViewById(R.id.textView8);
    }

    private void setupClickListeners() {
        // Make course title editable
        courseTitle.setOnClickListener(v -> showEditCourseNameDialog());

        // Add Topic Button
        addTopicButton.setOnClickListener(v -> showAddTopicDialog());

        // Back Button
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void showAddTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Topic");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter topic name");
        input.setPadding(32, 32, 32, 32);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);

        final AlertDialog dialogRef = builder.create();

        // Handle keyboard "Go" button
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String topicName = input.getText().toString().trim();
                if (!topicName.isEmpty()) {
                    addTopic(topicName);

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogRef.dismiss();
                    return true;
                }
            }
            return false;
        });

        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String topicName = input.getText().toString().trim();
            if (!topicName.isEmpty()) {
                addTopic(topicName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        dialogRef.show();

        // Show keyboard automatically
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showEditCourseNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Course Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        input.setText(courseName);
        input.setSelection(input.getText().toString().length());
        input.setPadding(32, 32, 32, 32);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);

        final AlertDialog dialogRef = builder.create();

        // Handle keyboard "Go" button
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    courseName = newName;
                    courseTitle.setText(courseName);
                    saveCourseName();

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogRef.dismiss();
                    return true;
                }
            }
            return false;
        });

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                courseName = newName;
                courseTitle.setText(courseName);
                saveCourseName();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        dialogRef.show();

        // Show keyboard automatically
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showEditTopicNameDialog(MaterialButton button, String currentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Topic Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentName);
        input.setSelection(input.getText().length());
        input.setPadding(32, 32, 32, 32);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);

        final AlertDialog dialogRef = builder.create();

        // Handle keyboard "Go" button
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    button.setText(newName);
                    int index = topicsList.indexOf(currentName);
                    if (index != -1) {
                        topicsList.set(index, newName);
                    }

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogRef.dismiss();
                    return true;
                }
            }
            return false;
        });

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                button.setText(newName);
                int index = topicsList.indexOf(currentName);
                if (index != -1) {
                    topicsList.set(index, newName);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        dialogRef.show();

        // Show keyboard automatically
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    private void addTopic(String topicName) {
        // Create layout for the topic button
        LinearLayout topicLayout = new LinearLayout(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        topicLayout.setLayoutParams(params);
        topicLayout.setOrientation(LinearLayout.VERTICAL);
        topicLayout.setGravity(Gravity.CENTER);

        // Create the topic button
        MaterialButton topicButton = new MaterialButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        topicButton.setLayoutParams(buttonParams);
        topicButton.setText(topicName);
        topicButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.topic_button_color)));
        topicButton.setCornerRadius(48);
        topicButton.setIconResource(R.drawable.baseline_forum_24);
        topicButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        topicButton.setIconPadding(40);

        // Add the button to the layout
        topicLayout.addView(topicButton);
        topicsGrid.addView(topicLayout);

        // Save the topic
        topicsList.add(topicName);

        // Long press to edit topic name
        topicButton.setOnLongClickListener(v -> {
            showEditTopicNameDialog(topicButton, topicName);
            return true;
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_discussion) {
                startActivity(new Intent(this, DiscussionActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void saveCourseName() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("course_name", courseName);
        editor.apply();
    }
}