package com.example.slearnapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    // Firebase and UI components
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    Button logoutButton, btnEditProfile;
    TextView email, fname, fname1, username, password;
    FirebaseUser user;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // Bind UI components
        logoutButton = view.findViewById(R.id.btnLogout);
//        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        email = view.findViewById(R.id.profileEmail);
        username = view.findViewById(R.id.profileUsername);
        password = view.findViewById(R.id.profilePassword);
        fname1 = view.findViewById(R.id.profileName);
        fname = view.findViewById(R.id.titleName);

        // Get current user
        user = auth.getCurrentUser();
        if (user == null) {
            // Redirect to login if user is not logged in
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return view;
        }

        userId = user.getUid();

        // Set the user's email
        email.setText(user.getEmail());

        // fname.setText(user.getFName());

        // Fetch and display user details from Firestore
        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle Firestore errors
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    username.setText(documentSnapshot.getString("username"));
                    password.setText(documentSnapshot.getString("password"));
                    fname.setText(documentSnapshot.getString("fName"));
                    fname1.setText(documentSnapshot.getString("fName"));

                }
            }
        });

        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

//        // Edit profile button functionality
//        btnEditProfile.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), EditProfile.class);
//            startActivity(intent);
//        });

        return view;
    }
}
