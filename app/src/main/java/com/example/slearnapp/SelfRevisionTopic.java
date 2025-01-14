package com.example.slearnapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SelfRevisionTopic extends AppCompatActivity {
    private TextView topicTitle;
    private ImageButton backButton;
    private Button addNoteButton;
    private LinearLayout notesContainer;
    private String topicName;
    private String courseName;
    private int titleCounter = 1;
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private static final String SHARED_PREFS_NAME = "courses_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_revision_topic);

        initializeViews();

        // Get both course and topic names from intent
        courseName = getIntent().getStringExtra("course_name");
        topicName = getIntent().getStringExtra("topic_name");

        if (topicName != null) {
            topicTitle.setText(topicName);
        }

        setupClickListeners();
        loadSavedState();
    }

    private void initializeViews() {
        topicTitle = findViewById(R.id.topic_title);
        backButton = findViewById(R.id.backButton);
        addNoteButton = findViewById(R.id.add_note_button);
        notesContainer = findViewById(R.id.notes_container);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        addNoteButton.setOnClickListener(v -> showAddTitleDialog());
        topicTitle.setOnClickListener(v -> showEditTopicNameDialog());
    }

    private void showAddTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_title_input, null);

        EditText titleInput = dialogView.findViewById(R.id.input_field);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button addButton = dialogView.findViewById(R.id.add_button);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            String titleText = titleInput.getText().toString().trim();
            if (!titleText.isEmpty()) {
                addNewTitleSection(titleText);
                dialog.dismiss();
            } else {
                titleInput.setError("Please enter a title");
            }
        });

        dialog.show();

        titleInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(titleInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addNewTitleSection(String titleText) {
        String noteId = courseName + "_" + topicName + "_" + System.currentTimeMillis();

        // Create title view
        TextView titleView = new TextView(this);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleView.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        titleView.setTextSize(20);
        titleView.setTextColor(getResources().getColor(R.color.blue));
        titleView.setBackgroundColor(getResources().getColor(R.color.light_blue));
        titleView.setText(titleText);
        titleView.setTag(noteId);

        // Create content view
        EditText noteContent = new EditText(this);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        noteParams.setMargins(0, 0, 0, dpToPx(16));
        noteContent.setLayoutParams(noteParams);
        noteContent.setMinHeight(dpToPx(120));
        noteContent.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        noteContent.setBackgroundColor(getResources().getColor(android.R.color.white));
        noteContent.setTextSize(16);
        noteContent.setTextColor(getResources().getColor(R.color.gray));
        noteContent.setHint("enter your text here ...");
        noteContent.setGravity(android.view.Gravity.TOP);
        noteContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        // Load existing content if any
        String savedContent = getNoteContent(noteId);
        if (!savedContent.isEmpty()) {
            noteContent.setText(savedContent);
        }

        // Set up swipe detection for the title
        titleView.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = endX - startX;
                        if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0) {
                            // Left swipe detected
                            showDeleteConfirmationDialog(titleView, noteContent, noteId);
                        } else if (Math.abs(deltaX) < MIN_DISTANCE) {
                            // It's a click, show edit dialog
                            showEditTitleDialog(titleView, noteId);
                        }
                        return true;
                }
                return false;
            }
        });

        // Auto-save content changes
        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveNoteContent(noteId, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        notesContainer.addView(titleView);
        notesContainer.addView(noteContent);

        saveTitleText(noteId, titleText);
        saveNoteId(titleCounter, noteId);
        titleCounter++;
        saveCounter();
    }

    private void showEditTopicNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Topic Name");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(topicName);
        input.setSelection(input.getText().length());
        input.setPadding(dpToPx(32), dpToPx(32), dpToPx(32), dpToPx(32));

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                String oldName = topicName;
                topicName = newName;
                topicTitle.setText(topicName);
                updateTopicName(oldName, newName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showEditTitleDialog(TextView titleView, String noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Title");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(titleView.getText());
        input.setSelection(input.getText().length());
        input.setPadding(32, 32, 32, 32);

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                titleView.setText(newTitle);
                saveTitleText(noteId, newTitle);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void showDeleteConfirmationDialog(View titleView, View noteContent, String noteId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Section")
                .setMessage("Are you sure you want to delete this section?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notesContainer.removeView(titleView);
                    notesContainer.removeView(noteContent);
                    deleteNoteContent(noteId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTopicName(String oldName, String newName) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String oldKey = courseName + "_" + oldName;
        String newKey = courseName + "_" + newName;

        // Move all note data
        int noteCount = prefs.getInt("title_counter_" + oldKey, 0);
        editor.putInt("title_counter_" + newKey, noteCount);
        editor.remove("title_counter_" + oldKey);

        for (int i = 1; i <= noteCount; i++) {
            String noteId = prefs.getString("note_id_" + oldKey + "_" + i, null);
            if (noteId != null) {
                // Update note ID reference
                editor.putString("note_id_" + newKey + "_" + i, noteId);
                editor.remove("note_id_" + oldKey + "_" + i);
            }
        }

        editor.apply();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // SharedPreferences methods
    private void loadSavedState() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String topicKey = courseName + "_" + topicName;
        titleCounter = prefs.getInt("title_counter_" + topicKey, 1);

        for (int i = 1; i < titleCounter; i++) {
            String noteId = getNoteId(i);
            if (noteId != null) {
                String savedTitle = getTitleText(noteId);
                if (savedTitle != null && !savedTitle.isEmpty()) {
                    addNewTitleSection(savedTitle);
                }
            }
        }
    }

    private void saveCounter() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String topicKey = courseName + "_" + topicName;
        prefs.edit().putInt("title_counter_" + topicKey, titleCounter).apply();
    }

    private void saveNoteContent(String noteId, String content) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString("note_content_" + courseName + "_" + noteId, content).apply();
    }

    private String getNoteContent(String noteId) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("note_content_" + courseName + "_" + noteId, "");
    }

    private void saveTitleText(String noteId, String titleText) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString("title_text_" + courseName + "_" + noteId, titleText).apply();
    }

    private String getTitleText(String noteId) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("title_text_" + courseName + "_" + noteId, "");
    }

    private void saveNoteId(int counter, String noteId) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String topicKey = courseName + "_" + topicName;
        prefs.edit().putString("note_id_" + topicKey + "_" + counter, noteId).apply();
    }

    private String getNoteId(int counter) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String topicKey = courseName + "_" + topicName;
        return prefs.getString("note_id_" + topicKey + "_" + counter, null);
    }

    private void deleteNoteContent(String noteId) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("note_content_" + courseName + "_" + noteId);
        editor.remove("title_text_" + courseName + "_" + noteId);
        editor.apply();
    }
}