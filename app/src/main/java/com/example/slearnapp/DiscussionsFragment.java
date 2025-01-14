package com.example.slearnapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.slearnapp.Discussion;
import com.example.slearnapp.DiscussionsAdapter;
import com.example.slearnapp.FirestoreUtils;
import com.example.slearnapp.R;
import com.example.slearnapp.databinding.FragmentDiscussionsBinding;

import java.util.Random;

import se.emirbuc.randomsentence.RandomSentences;

public class DiscussionsFragment extends Fragment {
    private com.example.slearnapp.DiscussionsAdapter adapter;
    private FragmentDiscussionsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new DiscussionsAdapter();
        binding = FragmentDiscussionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (binding != null) {
            // Firestore query to fetch discussions
            FirestoreUtils.getDiscussions().orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((snapshot, error) -> {
                        if (snapshot != null) {
                            populateRecyclerView(snapshot);
                        }
                    });

            // Menu item click listener
            binding.mtFragmentDiscussions.setOnMenuItemClickListener(this::onMenuItemClick);

            // RecyclerView setup
            binding.rvFragmentDiscussions.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvFragmentDiscussions.setAdapter(adapter);
        }
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.mFragmentMainAddPost) {
            // Add a new discussion to Firestore
            FirestoreUtils.getDiscussions().add(new com.example.slearnapp.Discussion(
                    RandomSentences.generateRandomSentence(RandomSentences.Length.LONG),
                    new Random().nextInt(31),
                    new Random().nextInt(11),
                    RandomSentences.generateRandomSentence(RandomSentences.Length.SHORT)
            ));
            return true;
        }
        return false;
    }

    private void populateRecyclerView(@NonNull QuerySnapshot snapshot) {
        adapter.removeAll();
        snapshot.forEach(document -> {
            com.example.slearnapp.Discussion discussion = document.toObject(Discussion.class);
            discussion.setId(document.getId());
            adapter.add(discussion);
        });

        // Show or hide the empty view
        if (binding != null) {
            binding.tvFragmentDiscussions.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }
}