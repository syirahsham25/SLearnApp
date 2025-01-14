package com.example.slearnapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.view.View;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.slearnapp.FeedbackActivityAssignment;
import com.example.slearnapp.FeedbackAssignmentAdapter;
import com.example.slearnapp.FirestoreUtils;
import com.example.slearnapp.R;
import com.example.slearnapp.databinding.ActivityFeedbackAssignmentBinding;

import com.example.slearnapp.ProfileFragment;

public class FeedbackActivityAssignment extends AppCompatActivity {

    private Button btnAssignmentFeedbacks, btnTeacherFeedbacks;

    private ImageButton backButton;

    private FeedbackAssignmentAdapter adapter;
    private ActivityFeedbackAssignmentBinding binding;

    FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String username, userEmail, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // Get the current user
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // You can use the email or uid, or fetch username from Firestore if you store it
            userEmail = currentUser.getEmail();
            userID = currentUser.getUid();
            Log.d("FeedbackActivity", "Logged in as: " + username);  // Debug log for user authentication
        } else {
            // Handle the case when the user is not logged in
            Log.e("FeedbackActivity", "No user is logged in");
            return;  // Exit early if no user is authenticated
        }

        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle Firestore errors
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                }
            }
        });

        setContentView(R.layout.activity_feedback_assignment);



        // Initialize binding
        binding = ActivityFeedbackAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup adapter
        adapter = new FeedbackAssignmentAdapter();
        binding.rvActivityFeedbackAssignment.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityFeedbackAssignment.setAdapter(adapter);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Go back to the previous activity
            onBackPressed(); // Or alternatively, finish() could be used
        });

        // Firestore query to fetch feedback assignments
        FirestoreUtils.getFeedbackAssignment()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null) {
                        Log.d("FeedbackActivity", "Firestore data fetched successfully.");
                        populateRecyclerView(snapshot);
                    } else {
                        Log.e("FeedbackActivity", "Error fetching Firestore data: " + (error != null ? error.getMessage() : "Unknown error"));
                    }
                });

        // Swipe-to-delete setup
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(binding.rvActivityFeedbackAssignment);

        // Menu item click listener
        binding.mtActivityFeedbackAssignment.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.mFragmentMainAddPost) {
            showFeedbackDialog();
            return true;
        }
        return false;
    }

    // Method to show the input dialog for creating a feedback assignment
    private void showFeedbackDialog() {
        // Create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Feedback Assignment");

        // Create a LinearLayout to hold the EditText fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input fields
        final EditText titleInput = new EditText(this);
        titleInput.setHint("Assignment Title");
        final EditText feedbackInput = new EditText(this);
        feedbackInput.setHint("Feedback Message");
        final EditText scoreInput = new EditText(this);
        scoreInput.setHint("Score");
        final EditText teacherInput = new EditText(this);
        teacherInput.setHint("Teacher's Name");

        // Add the input fields to the layout
        layout.addView(titleInput);
        layout.addView(feedbackInput);
        layout.addView(scoreInput);
        layout.addView(teacherInput);

        // Set the view of the dialog to the layout containing all EditTexts
        builder.setView(layout);

        // Set the buttons for the dialog
        builder.setPositiveButton("Add Feedback", (dialog, which) -> {
            // Get the user input
            String title = titleInput.getText().toString().trim();
            String feedback = feedbackInput.getText().toString().trim();
            String score = scoreInput.getText().toString().trim();
            String teacher = teacherInput.getText().toString().trim();

            // Validate input
            if (title.isEmpty() || feedback.isEmpty() || score.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(FeedbackActivityAssignment.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new FeedbackAssignment object
            FeedbackAssignment newFeedback = new FeedbackAssignment(
                    title,                          // assignmentTitle
                    feedback,                       // feedbackMessage
                    score,                          // score
                    teacher,                        // teacherName
                    username,                       // username
                    new Timestamp(System.currentTimeMillis() / 1000, 0)  // timestamp
            );

            // Add feedback to Firestore
            FirestoreUtils.getFeedbackAssignment()
                    .add(newFeedback)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FeedbackActivity", "Feedback added to Firestore with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FeedbackActivity", "Error adding feedback to Firestore: " + e.getMessage());
                    });

            // Close the dialog
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();

    }

    private void populateRecyclerView(@NonNull QuerySnapshot snapshot) {
        adapter.removeAll();
        snapshot.forEach(document -> {
            FeedbackAssignment feedbackAssignment = document.toObject(FeedbackAssignment.class);
            feedbackAssignment.setId(document.getId());
            adapter.add(feedbackAssignment);
        });

        // Show or hide the empty view
        if (binding != null) {
            binding.tvActivityFeedbackAssignment.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        public SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); // Support both left and right swipe
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;  // We are not interested in move operations
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            FeedbackAssignment feedbackAssignment = adapter.getItem(position);

            // Delete from Firestore
            FirestoreUtils.getFeedbackAssignment().document(feedbackAssignment.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FeedbackActivity", "Feedback deleted from Firestore with ID: " + feedbackAssignment.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FeedbackActivity", "Error deleting feedback from Firestore: " + e.getMessage());
                    });

            // Remove the item from the adapter
            adapter.removeItem(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;  // Avoid memory leaks by cleaning up the binding
    }


}
