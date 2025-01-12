package com.example.slearnapp;


import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.slearnapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bnvActivityMain.setOnItemSelectedListener(item -> {
            Fragment fragment = getFragment(item);


            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fcvActivityMain.getId(), fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

                return true;
            } else {
                return false;
            }
        });
    }

    @Nullable
    private static Fragment getFragment(MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment = null;

        if (itemId == R.id.mActivityMainHome) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.mActivityMainDiscussions) {
            fragment = new DiscussionsFragment();
        }else if (itemId == R.id.mActivityMainAchievements) {
            fragment = new AchievementsFragment();
        }
        else if (itemId == R.id.mActivityMainProfile) {
            fragment = new ProfileFragment();
        }

        return fragment;
    }


}