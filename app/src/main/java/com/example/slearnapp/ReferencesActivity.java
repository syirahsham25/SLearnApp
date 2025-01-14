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
import com.example.slearnapp.ReferencesActivity;
import com.example.slearnapp.Reference;
import com.example.slearnapp.ReferencesAdapter;
import com.example.slearnapp.FirestoreUtils;
import com.example.slearnapp.R;
import com.example.slearnapp.databinding.ActivityReferencesBinding;

import com.example.slearnapp.ProfileFragment;

public class ReferencesActivity extends AppCompatActivity {

    private Button btnReferences, btnTeacherReferences;

    private ImageButton backButton;

    private ReferencesAdapter adapter;
    private ActivityReferencesBinding binding;

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
            Log.d("ReferencesActivity", "Logged in as: " + username);  // Debug log for user authentication
        } else {
            // Handle the case when the user is not logged in
            Log.e("ReferencesActivity", "No user is logged in");
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

        setContentView(R.layout.activity_references);


        // Initialize binding
        binding = ActivityReferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup adapter
        adapter = new ReferencesAdapter();
        binding.rvActivityReferences.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityReferences.setAdapter(adapter);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Go back to the previous activity
            onBackPressed(); // Or alternatively, finish() could be used
        });

        // Firestore query to fetch References
        FirestoreUtils.getReferences()
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null) {
                        Log.d("ReferencesActivity", "Firestore data fetched successfully.");
                        populateRecyclerView(snapshot);
                    } else {
                        Log.e("ReferencesActivity", "Error fetching Firestore data: " + (error != null ? error.getMessage() : "Unknown error"));
                    }
                });

        // Swipe-to-delete setup
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(binding.rvActivityReferences);

        // Menu item click listener
        binding.mtActivityReferences.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.mFragmentMainAddPost) {
            showReferencesDialog();
            return true;
        }
        return false;
    }

    // Method to show the input dialog for creating a Reference
    private void showReferencesDialog() {
        // Create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New References");

        // Create a LinearLayout to hold the EditText fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input fields
        final EditText resourceNameInput = new EditText(this);
        resourceNameInput.setHint("References Name");
        final EditText resourceURLInput = new EditText(this);
        resourceURLInput.setHint("References Message");
        final EditText subjectInput = new EditText(this);
        subjectInput.setHint("Subject");

        // Add the input fields to the layout
        layout.addView(resourceNameInput);
        layout.addView(resourceURLInput);
        layout.addView(subjectInput);

        // Set the view of the dialog to the layout containing all EditTexts
        builder.setView(layout);

        // Set the buttons for the dialog
        builder.setPositiveButton("Add References", (dialog, which) -> {
            // Get the user input
            String resourceName = resourceNameInput.getText().toString().trim();
            String resourceURL = resourceURLInput.getText().toString().trim();
            String subject = subjectInput.getText().toString().trim();

            // Validate input
            if (resourceName.isEmpty() || resourceURL.isEmpty() || subject.isEmpty()) {
                Toast.makeText(ReferencesActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new Reference object
            Reference newReference = new Reference (
                    resourceName,                          // resourceName
                    resourceURL,                       // referenceMessage
                    subject,                          // score
                    username,                        // teacherName
                    new Timestamp(System.currentTimeMillis() / 1000, 0)  // timestamp
            );

            // Add References to Firestore
            FirestoreUtils.getReferences()
                    .add(newReference)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("ReferencesActivity", "Reference added to Firestore with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ReferencesActivity", "Error adding reference to Firestore: " + e.getMessage());
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
            Reference reference = document.toObject(Reference.class);
            reference.setId(document.getId());
            adapter.add(reference);
        });

        // Show or hide the empty view
        if (binding != null) {
            binding.tvActivityReferences.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
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
            Reference reference = adapter.getItem(position);

            // Delete from Firestore
            FirestoreUtils.getReferences().document(reference.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ReferencesActivity.this, "Reference deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ReferencesActivity.this, "Failed to delete reference", Toast.LENGTH_SHORT).show();
                    });

            // Remove the item from the RecyclerView
            adapter.removeItem(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;  // Avoid memory leaks by cleaning up the binding
    }
}
