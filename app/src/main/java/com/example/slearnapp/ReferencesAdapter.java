package com.example.slearnapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReferencesAdapter extends RecyclerView.Adapter<ReferencesAdapter.ViewHolder> {

    private final List<Reference> references = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_references, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Reference reference = references.get(position);
        Context context = viewHolder.itemView.getContext();

        // Log details to confirm data exists
        Log.d("ReferencesAdapter", "Binding reference: " +
                "\nresourceName: " + reference.getResourceName() +
                "\nresourceURL: " + reference.getResourceURL() +
                "\nsubject: " + reference.getSubject() +
                "\nusername: " + reference.getUsername() +
                "\ntimestamp: " + reference.getTimestamp());

        // Set the views as usual
        viewHolder.resourceName.setText("Resource Name: " + reference.getResourceName());
        viewHolder.resourceURL.setText("Resource URL: " + reference.getResourceURL());
        viewHolder.subject.setText("Subject: " + reference.getSubject());
        viewHolder.username.setText("" + reference.getUsername());
        viewHolder.timestamp.setText("Time: " + Utils.formatTimestamp(reference.getTimestamp()));

        // Open Resource Button
        viewHolder.openResourceButton.setOnClickListener(v -> {
            Toast.makeText(context, "Opening " + reference.getResourceName(), Toast.LENGTH_SHORT).show();
            // Example action: Open URL in browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(reference.getResourceURL()));
            context.startActivity(intent);
        });

        // Share Button
        viewHolder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Resource: " + reference.getResourceName() + "\nSubject: " + reference.getSubject() +
                            "\nURL: " + reference.getResourceURL() + "\nShared by: " + reference.getUsername());
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Check out this resource: " + reference.getResourceName());

            context.startActivity(Intent.createChooser(shareIntent, null));
        });
    }

    @Override
    public int getItemCount() {
        return references.size();
    }

    public Reference getItem(int position) {
        return references.get(position);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < references.size()) {
            references.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(Reference reference) {
        references.add(reference);
        notifyItemInserted(references.indexOf(reference));
    }

    public void removeAll() {
        while (!references.isEmpty()) {
            references.remove(0);
            notifyItemRemoved(0);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView resourceName;
        final TextView resourceURL;
        final TextView subject;
        final TextView username;
        final TextView timestamp;
        final Button openResourceButton;
        final Button share;

        public ViewHolder(@NonNull View view) {
            super(view);
            resourceName = view.findViewById(R.id.tvResourceName);
            resourceURL = view.findViewById(R.id.tvResourceURL);
            subject = view.findViewById(R.id.tvSubject);
            username = view.findViewById(R.id.tvUsername);
            timestamp = view.findViewById(R.id.tvTimestamp);
            openResourceButton = view.findViewById(R.id.btnOpenResource);
            share = view.findViewById(R.id.btnGiveShare);
        }
    }
}
