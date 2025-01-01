package com.example.slearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editUsername, editPassword;
    private Button saveChanges;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editProfileName);
        editEmail = findViewById(R.id.editProfileEmail);
        editUsername = findViewById(R.id.editProfileUsername);
        editPassword = findViewById(R.id.editProfilePassword);
        saveChanges = findViewById(R.id.btnSaveChanges);

        showUserData();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        Button btn1 = view.findViewById(R.id.btnSaveChanges);

        View.OnClickListener CLSelfRevision = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestProfile);
            }
        };
        btn1.setOnClickListener(CLSelfRevision);

    }



    private void showUserData() {
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        currentUsername = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        editName.setText(name);
        editEmail.setText(email);
        editUsername.setText(currentUsername); // Original username for identification
        editPassword.setText(password);
    }

    private void updateUserData() {
        String updatedName = editName.getText().toString().trim();
        String updatedEmail = editEmail.getText().toString().trim();
        String updatedUsername = editUsername.getText().toString().trim();
        String updatedPassword = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedEmail) ||
                TextUtils.isEmpty(updatedUsername) || TextUtils.isEmpty(updatedPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // Remove old username if changed
        if (!currentUsername.equals(updatedUsername)) {
            reference.child(currentUsername).removeValue();
        }

        // Update new or existing data
        reference.child(updatedUsername).child("name").setValue(updatedName);
        reference.child(updatedUsername).child("email").setValue(updatedEmail);
        reference.child(updatedUsername).child("username").setValue(updatedUsername);
        reference.child(updatedUsername).child("password").setValue(updatedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        // Redirect back to ProfileFragment or MainActivity
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.putExtra("name", updatedName);
                        intent.putExtra("email", updatedEmail);
                        intent.putExtra("username", updatedUsername);
                        intent.putExtra("password", updatedPassword);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
