package com.example.slearn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slearn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class StudyPlanner extends Fragment {

    Button loginButton;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_planner, container, false);

        // Initialize Views
        recyclerView = view.findViewById(R.id.task_list);
        MaterialButton btnAddTask = view.findViewById(R.id.btn_add_task);

        // Initialize Task List and Adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        // Add Task Button Listener
        btnAddTask.setOnClickListener(v -> {

            showTaskInputDialog(null, -1);

        });

        // Swipe Gestures
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We are not implementing drag-and-drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    // Get the task that is about to be deleted
                    Task taskToDelete = taskList.get(position);

                    // Remove the task from the list temporarily
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);

                    // Show Snackbar with "Delete" and "Cancel" options
                    Snackbar snackbar = Snackbar.make(recyclerView, "Delete task?", Snackbar.LENGTH_LONG)
                            .setAction("DELETE", v -> {
                                // Delete the task permanently when "DELETE" is clicked
                                taskAdapter.deleteTask(position);
                                Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setActionTextColor(Color.RED)
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    // Handle dismissing Snackbar after canceling
                                    if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                        // If "Cancel" is clicked, undo the deletion
                                        taskList.add(position, taskToDelete);  // Restore the task
                                    }
                                }
                            });

                    // Add the "Cancel" action for undoing the deletion
                    snackbar.setAction("CANCEL", v -> {
                        // Revert the deletion by restoring the task
                        taskList.add(position, taskToDelete);
                        taskAdapter.notifyItemInserted(position);  // Notify the adapter
                        Toast.makeText(getContext(), "Task restored", Toast.LENGTH_SHORT).show();
                    });

                    snackbar.show(); // Show the Snackbar
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Trigger task editing when swiped right
                    Task taskToEdit = taskList.get(position);
                    showTaskInputDialog(taskToEdit, position);
                }
            }


        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }


    public void showTaskInputDialog(@Nullable Task task, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_task_input, null); // Inflate the custom layout for the dialog
        String dialogTitle = (task == null) ? "Add Task" : "Edit Task";

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create(); // Create the dialog

        // Get references to the UI components in the dialog
        EditText taskInput = dialogView.findViewById(R.id.task_input);
        MaterialButton btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        // If editing, pre-fill the input field with the current task name
        if (task != null) {
            taskInput.setText(task.getName());
        }

        // Set the save button click listener
        btnSaveTask.setOnClickListener(v -> {
            String taskName = taskInput.getText().toString();
            if (!taskName.isEmpty()) {
                if (task != null) {
                    task.setName(taskName);  // Update the task name
                    taskAdapter.notifyItemChanged(position);  // Update the task at the specified position
                } else {
                    taskAdapter.addTask(new Task(taskName));  // Add a new task
                }
                dialog.dismiss();  // Close the dialog
            } else {
                Toast.makeText(getContext(), "Please enter a task name", Toast.LENGTH_SHORT).show(); // Handle empty task name
            }
        });

        dialog.show();  // Show the dialog
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        Button btn1 = view.findViewById(R.id.btn_event);

        View.OnClickListener CLSelfRevision = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestStudyPlanner2);
            }
        };
        btn1.setOnClickListener(CLSelfRevision);

    }











    // Task Model Class
    public static class Task {
        private String name;
        private boolean isDone;

        public Task(String name) {
            this.name = name;
            this.isDone = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isDone() {
            return isDone;
        }

        public void setDone(boolean done) {
            isDone = done;
        }
    }

    // RecyclerView Adapter Class
    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<Task> tasks;

        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.checkBox.setText(task.getName());
            holder.checkBox.setChecked(task.isDone());
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        public void addTask(Task task) {
            tasks.add(task);
            notifyItemInserted(tasks.size() - 1);
        }

        public void deleteTask(int position) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }

        // ViewHolder Class
        static class TaskViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBox;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.task_checkbox);
            }
        }
    }
}
