package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TeacherTestsActivity extends AppCompatActivity {
    private Button addQuizzButton;
    private Button reviewQuizzButton;
    private ImageButton backButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_addquizz);

        // Initialize views
        initializeViews();
        // Set up click listeners
        setupClickListeners();
        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        addQuizzButton = findViewById(R.id.rv_quizz_btn);
        reviewQuizzButton = findViewById(R.id.add_quizz_btn);
        backButton = findViewById(R.id.backButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupClickListeners() {
        // Add Quiz Button Click Listener
        addQuizzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherTestsActivity.this, TeacherAddQuizActivity.class);
                startActivity(intent);
            }
        });

        // Review Quiz Button Click Listener
        reviewQuizzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherTestsActivity.this, TeacherReviewActivity.class);
                startActivity(intent);
            }
        });

        // Back Button Click Listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(TeacherTestsActivity.this, HomeActivity.class));
                    return true;
                }
                else if (itemId == R.id.nav_discussion) {
                    startActivity(new Intent(TeacherTestsActivity.this, DiscussionActivity.class));
                    return true;
                }
                else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(TeacherTestsActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the selected item in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Assuming this is part of home section
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Add any custom back button behavior here if needed
    }


}
