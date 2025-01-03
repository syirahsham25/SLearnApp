package com.example.project;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;


public class SelfRevisionTopic extends AppCompatActivity {
    private TextView topicTitle;
    private ImageButton backButton;
    private Button addTitleButton;
    private LinearLayout notesContainer;
    private BottomNavigationView bottomNavigationView;
    private String topicName;
    private int titleCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_revision_topic);

        initializeViews();

        // Get topic name from intent
        topicName = getIntent().getStringExtra("topic_name");
        topicTitle.setText(topicName);

        setupClickListeners();
        setupBottomNavigation();

        // Load existing title counter if any
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        titleCounter = prefs.getInt("title_counter", 1);
    }

    private void initializeViews() {
        topicTitle = findViewById(R.id.topic_title);
        backButton = findViewById(R.id.backButton);
        addTitleButton = findViewById(R.id.add_title_button);
        notesContainer = findViewById(R.id.notes_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        // Make topic title editable
        topicTitle.setOnClickListener(v -> showEditTopicNameDialog());

        // Back Button
        backButton.setOnClickListener(v -> finish());

        // Add Title Button
        addTitleButton.setOnClickListener(v -> addNewTitleSection());
    }

    private void showEditTopicNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Topic Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(topicName);
        input.setSelection(input.getText().toString().length());
        input.setPadding(32, 32, 32, 32);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);

        final AlertDialog dialogRef = builder.create();

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    topicName = newName;
                    topicTitle.setText(topicName);
                    saveTopicName();

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
                topicName = newName;
                topicTitle.setText(topicName);
                saveTopicName();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        dialogRef.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }


    private void saveTopicName() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("topic_name", topicName);
        editor.apply();
    }

    private void saveNoteContent(int titleNumber, String content) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("note_content_" + titleNumber, content);
        editor.apply();
    }

    private String getNoteContent(int titleNumber) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getString("note_content_" + titleNumber, null);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeFragment.class));
                return true;
            } else if (itemId == R.id.nav_discussion) {
                startActivity(new Intent(this, DiscussionFragment.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileFragment.class));
                return true;
            }
            return false;
        });
    }

    private void addNewTitleSection() {
        View titleSection = getLayoutInflater().inflate(R.layout.title_section, null);

        // Set the title number
        TextView titleText = titleSection.findViewById(R.id.title_text);
        titleText.setText("Title " + titleCounter);

        // Configure the EditText
        EditText noteContent = titleSection.findViewById(R.id.note_content);
        TextView characterCount = titleSection.findViewById(R.id.character_count);

        // Set up edit title button
        ImageButton editTitleButton = titleSection.findViewById(R.id.edit_title_button);
        editTitleButton.setOnClickListener(v -> showEditTitleDialog(titleText, titleCounter));

        // Set up delete button
        ImageButton deleteButton = titleSection.findViewById(R.id.delete_title_button);
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(titleSection, titleCounter));

        // Configure text watcher for content and character count
        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                characterCount.setText(s.length() + " characters");
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveNoteContent(titleCounter, s.toString());
            }
        });

        // Load any saved content
        String savedContent = getNoteContent(titleCounter);
        if (savedContent != null) {
            noteContent.setText(savedContent);
        }

        notesContainer.addView(titleSection);
        titleCounter++;

        // Save the new counter
        saveCounter();
    }

    private void showEditTitleDialog(TextView titleText, int titleNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(titleText.getText());
        input.setSelection(input.getText().toString().length());
        input.setPadding(32, 32, 32, 32);

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                titleText.setText(newTitle);
                saveTitleText(titleNumber, newTitle);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(View titleSection, int titleNumber) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Title")
                .setMessage("Are you sure you want to delete this title and its content?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notesContainer.removeView(titleSection);
                    deleteNoteContent(titleNumber);
                    deleteTitleText(titleNumber);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveCounter() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("title_counter", titleCounter);
        editor.apply();
    }

    private void saveTitleText(int titleNumber, String titleText) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("title_text_" + titleNumber, titleText);
        editor.apply();
    }

    private void deleteNoteContent(int titleNumber) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("note_content_" + titleNumber);
        editor.remove("title_text_" + titleNumber);
        editor.apply();
    }

    private void deleteTitleText(int titleNumber) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("title_text_" + titleNumber);
        editor.apply();
    }
}
