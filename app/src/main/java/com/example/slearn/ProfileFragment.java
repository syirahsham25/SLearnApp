package com.example.slearn;

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
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView profileName, profileEmail, profileUsername, profilePassword;
    private TextView titleName, titleUsername;
    private Button editProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePassword = view.findViewById(R.id.profilePassword);
        titleName = view.findViewById(R.id.titleName);
        editProfile = view.findViewById(R.id.btnEditProfile);

        showUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passUserData();
            }
        });

        return view;
    }

    private void showUserData() {
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();

            String nameUser = intent.getStringExtra("name");
            String emailUser = intent.getStringExtra("email");
            String usernameUser = intent.getStringExtra("username");
            String passwordUser = intent.getStringExtra("password");

            titleName.setText(nameUser);
            profileName.setText(nameUser);
            profileEmail.setText(emailUser);
            profileUsername.setText(usernameUser);
            profilePassword.setText(passwordUser);
        }
    }

    private void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    intent.putExtra("name", nameFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        Button btn1 = view.findViewById(R.id.btnEditProfile);

        View.OnClickListener LLEditProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestEditProfile);
            }
        };
        btn1.setOnClickListener(LLEditProfile);

    }



}
