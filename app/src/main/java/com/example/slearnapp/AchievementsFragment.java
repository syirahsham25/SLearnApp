package com.example.slearnapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

//import com.google.firebase.firestore.QuerySnapshot;
import com.example.slearnapp.databinding.FragmentAchievementsBinding;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {
//    private AchievementsAdapter adapter;
//    private FragmentAchievementsBinding binding;
//    private List<Integer> checkedChipIds = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AchievementsFragment() {
        // Required empty public constructor
    }
    public static AchievementsFragment newInstance(String param1, String param2) {
        AchievementsFragment fragment = new AchievementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        adapter = new AchievementsAdapter();
//        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        if (binding != null) {
//            checkedChipIds = binding.cgFragmentAchievements.getCheckedChipIds();
//
//            FirestoreUtils.getAchievements().addSnapshotListener((snapshot, error) -> {
//                if (snapshot != null) {
//                    populateRecyclerView(snapshot);
//                }
//            });
//
//            binding.cgFragmentAchievements.setOnCheckedStateChangeListener((group, checkedIds) -> {
//                checkedChipIds = checkedIds;
//                FirestoreUtils.getAchievements().get().addOnSuccessListener(this::populateRecyclerView);
//            });
//
//            binding.rvFragmentDiscussions.setAdapter(adapter);
//            binding.rvFragmentDiscussions.setLayoutManager(new LinearLayoutManager(getContext()));
//        }
//    }
//
//    private void populateRecyclerView(QuerySnapshot snapshot) {
//        adapter.removeAll();
//
//        for (com.google.firebase.firestore.QueryDocumentSnapshot document : snapshot) {
//            Achievement achievement = document.toObject(Achievement.class);
//            achievement.setId(document.getId());
//
//            if (!checkedChipIds.isEmpty()) {
//                int chipId = checkedChipIds.get(0);
//
//                if (chipId == R.id.cFragmentAchievementsCoCurricular && !achievement.isAcademic()) {
//                    adapter.add(achievement);
//                } else if (achievement.isAcademic()) {
//                    adapter.add(achievement);
//                }
//            }
//        }
//
//        if (binding != null) {
//            binding.tvFragmentDiscussions.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
//        }
//    }
}