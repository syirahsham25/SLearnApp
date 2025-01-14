package com.example.slearnapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// import com.google.android.material.button.MaterialButton; // Uncomment if using MaterialButton

import com.example.slearnapp.FeedbackAssignment;
import com.example.slearnapp.FirestoreUtils;
import com.example.slearnapp.R;
import com.example.slearnapp.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FeedbackAssignmentAdapter extends RecyclerView.Adapter<FeedbackAssignmentAdapter.ViewHolder> {

    private final List<FeedbackAssignment> feedbackAssignments = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_feedback_assignment, parent, false
        );
        return new ViewHolder(view); // Ensure we return the correct ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FeedbackAssignment feedbackAssignment = feedbackAssignments.get(position);
        Context context = viewHolder.itemView.getContext();

        // Log the details to confirm the data exists
        Log.d("FeedbackAssignmentAdapter", "Binding feedbackAssignment: " +
                "\nassignmentTitle: " + feedbackAssignment.getAssignmentTitle() +
                "\nteacherName: " + feedbackAssignment.getTeacherName() +
                "\nusername: " + feedbackAssignment.getUsername() +
                "\nfeedbackMessage: " + feedbackAssignment.getFeedbackMessage() +
                "\nscore: " + feedbackAssignment.getScore() +
                "\ntimestamp: " + feedbackAssignment.getTimestamp());

        // Set the views as usual
        viewHolder.assignmentTitle.setText("Assignment Title: " + feedbackAssignment.getAssignmentTitle());
        viewHolder.teacherName.setText("Teacher: " + feedbackAssignment.getTeacherName());
        viewHolder.username.setText("" + feedbackAssignment.getUsername());
        viewHolder.feedbackMessage.setText("Feedback: \"" + feedbackAssignment.getFeedbackMessage() + "\"");
        viewHolder.score.setText(String.format(Locale.getDefault(), "Score: %s", feedbackAssignment.getScore()));
        viewHolder.timestamp.setText("Time: " + Utils.formatTimestamp(feedbackAssignment.getTimestamp()));

        // Give feedback Button
        viewHolder.giveFeedbackButton.setOnClickListener(v -> {
            Toast.makeText(context, "Feedback submitted for " + feedbackAssignment.getAssignmentTitle(), Toast.LENGTH_SHORT).show();
            // Example action: Update feedback on Firestore
            FirebaseFirestore.getInstance()
                    .collection("FeedbackAssignments")
                    .document(feedbackAssignment.getId())
                    .update("feedbackMessage", "Feedback Updated!")
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Feedback Updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Share Button
        viewHolder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, feedbackAssignment.getFeedbackMessage() + "\nScore: " + feedbackAssignment.getScore() + "\nUsername: " + feedbackAssignment.getUsername() + "\n\n\n" + feedbackAssignment.getAssignmentTitle()+ " feedback by " + feedbackAssignment.getTeacherName());
            shareIntent.putExtra(Intent.EXTRA_TITLE, feedbackAssignment.getAssignmentTitle()+ " feedback by " + feedbackAssignment.getTeacherName());


            context.startActivity(Intent.createChooser(shareIntent, null));
        });

    }

    @Override
    public int getItemCount() {
        return feedbackAssignments.size();
    }

    public FeedbackAssignment getItem(int position) {
        return feedbackAssignments.get(position);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < feedbackAssignments.size()) {
            feedbackAssignments.remove(position);
            notifyItemRemoved(position);  // Notify the adapter that an item was removed
        }
    }

    public void add(FeedbackAssignment feedbackAssignment) {
        feedbackAssignments.add(feedbackAssignment);
        notifyItemInserted(feedbackAssignments.indexOf(feedbackAssignment));
    }

    public void removeAll() {
        while (!feedbackAssignments.isEmpty()) {
            feedbackAssignments.remove(0);
            notifyItemRemoved(0);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView assignmentTitle;
        final TextView teacherName;
        final TextView username;
        final TextView feedbackMessage;
        final TextView score;
        final TextView timestamp;
        final Button giveFeedbackButton;

        final Button share;

        public ViewHolder(@NonNull View view) {
            super(view);
            assignmentTitle = view.findViewById(R.id.tvAssignmentTitle);
            teacherName = view.findViewById(R.id.tvTeacherName);
            username = view.findViewById(R.id.tvUsername);
            feedbackMessage = view.findViewById(R.id.tvFeedbackMessage);
            score = view.findViewById(R.id.tvScore);
            timestamp = view.findViewById(R.id.tvTimestamp);
            giveFeedbackButton = view.findViewById(R.id.btnGiveFeedback);
            share = view.findViewById(R.id.btnGiveShare);

        }
    }
}
