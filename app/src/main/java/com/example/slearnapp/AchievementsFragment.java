package com.example.slearnapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.slearnapp.Achievement;
import com.example.slearnapp.AchievementsAdapter;
import com.example.slearnapp.FirestoreUtils;
import com.example.slearnapp.R;
import com.example.slearnapp.databinding.FragmentAchievementsBinding;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {
    private AchievementsAdapter adapter;
    private FragmentAchievementsBinding binding;
    private List<Integer> checkedChipIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new AchievementsAdapter();
        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
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
            checkedChipIds = binding.cgFragmentAchievements.getCheckedChipIds();

            FirestoreUtils.getAchievements().addSnapshotListener((snapshot, error) -> {
                if (snapshot != null) {
                    populateRecyclerView(snapshot);
                }
            });

            binding.cgFragmentAchievements.setOnCheckedStateChangeListener((group, checkedIds) -> {
                checkedChipIds = checkedIds;
                FirestoreUtils.getAchievements().get().addOnSuccessListener(this::populateRecyclerView);
                Log.d("AchievementsFragment", "Checked Chip IDs: " + checkedChipIds);
            });

            binding.rvFragmentDiscussions.setAdapter(adapter);
            binding.rvFragmentDiscussions.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void populateRecyclerView(QuerySnapshot snapshot) {
        adapter.removeAll();

        if (!checkedChipIds.isEmpty()) {
            // Reset the filter logic, handle both academic and co-curricular chips being checked
            boolean filterAcademic = checkedChipIds.contains(R.id.cFragmentAchievements);
            boolean filterCoCurricular = checkedChipIds.contains(R.id.cFragmentAchievementsCoCurricular);

            for (QueryDocumentSnapshot document : snapshot) {
                Achievement achievement = document.toObject(Achievement.class);
                achievement.setId(document.getId());

                // Debugging Logs
                Log.d("AchievementsFragment", "Achievement ID: " + achievement.getId());
                Log.d("AchievementsFragment", "isAcademic: " + achievement.isAcademic());
                Log.d("AchievementsFragment", "isAcademic from Firestore: " + achievement.isAcademic());

                // Add academic achievements if the academic chip is selected
                if (filterAcademic && achievement.isAcademic()) {
                    Log.d("AchievementsFragment", "Adding Academic Achievement");
                    adapter.add(achievement);
                }

                // Add co-curricular achievements if the co-curricular chip is selected
                if (filterCoCurricular && !achievement.isAcademic()) {
                    Log.d("AchievementsFragment", "Adding Co-Curricular Achievement");
                    adapter.add(achievement);
                }
            }
        }

        if (binding != null) {
            binding.tvFragmentDiscussions.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

}
