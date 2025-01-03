package com.example.project;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class SelfRevisionHOME extends AppCompatActivity {
    private LinearLayout coursesContainer;
    private Button addCourseButton;
    private ImageButton backButton;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<String> coursesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_revision_home);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Set up bottom navigation
        setupBottomNavigation();

        // Load any existing courses
        loadExistingCourses();
    }

    private void initializeViews() {
        coursesContainer = findViewById(R.id.courses_container);
        addCourseButton = findViewById(R.id.add_course);
        backButton = findViewById(R.id.backButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
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
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addCourse(String courseName) {
        // Create new button for the course
        Button courseButton = new Button(this);
        courseButton.setText(courseName);
        courseButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.course_button_color)));

        courseButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelfRevisionHOME.this, SelfRevisionCourse.class);
            intent.putExtra("course_name", courseName);
            startActivity(intent);
        });
        // Set button styling
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        courseButton.setLayoutParams(params);
        courseButton.setPadding(48, 48, 48, 48);
        courseButton.setTextSize(18);

        // Set corner radius
        MaterialButton materialButton = new MaterialButton(this);
        materialButton.setCornerRadius(72); // 24dp * 3 for rounded corners
        Drawable background = materialButton.getBackground();
        courseButton.setBackground(background);

        // Set click listener for the course button
        courseButton.setOnClickListener(v -> {
            // TODO: Navigate to course details/topics
            Toast.makeText(SelfRevisionHOME.this,
                    "Opening " + courseName,
                    Toast.LENGTH_SHORT).show();
        });

        // Add to container
        coursesContainer.addView(courseButton);

        // Save the course
        coursesList.add(courseName);
        saveCourses();
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

    private void loadExistingCourses() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Set<String> savedCourses = prefs.getStringSet("courses", new HashSet<>());
        coursesList = new ArrayList<>(savedCourses);

        // Sort courses alphabetically
        Collections.sort(coursesList);

        // Add buttons for existing courses
        for (String courseName : coursesList) {
            addCourse(courseName);
        }
    }

    private void saveCourses() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("courses", new HashSet<>(coursesList));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the selected item in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}