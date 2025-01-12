package com.example.slearnapp;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class StudyPlanner extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_study_planner); // Change this to your activity layout

        // Initialize Views
        recyclerView = findViewById(R.id.task_list);
        Button btnAddTask = findViewById(R.id.btn_add_task);

        Button button1 = findViewById(R.id.btn_event);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyPlanner.this, StudyPlanner2.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize Task List and Adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Add Task Button Listener
        btnAddTask.setOnClickListener(v -> showTaskInputDialog(null, -1));

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
                                Toast.makeText(StudyPlanner.this, "Task deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setActionTextColor(Color.RED)
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                        taskList.add(position, taskToDelete);  // Restore the task
                                    }
                                }
                            });

                    snackbar.setAction("CANCEL", v -> {
                        taskList.add(position, taskToDelete);
                        taskAdapter.notifyItemInserted(position);
                        Toast.makeText(StudyPlanner.this, "Task restored", Toast.LENGTH_SHORT).show();
                    });

                    snackbar.show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    Task taskToEdit = taskList.get(position);
                    showTaskInputDialog(taskToEdit, position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void showTaskInputDialog(@Nullable Task task, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_task_input, null);
        String dialogTitle = (task == null) ? "Add Task" : "Edit Task";

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        EditText taskInput = dialogView.findViewById(R.id.task_input);
        MaterialButton btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        if (task != null) {
            taskInput.setText(task.getName());
        }

        btnSaveTask.setOnClickListener(v -> {
            String taskName = taskInput.getText().toString();
            if (!taskName.isEmpty()) {
                if (task != null) {
                    task.setName(taskName);
                    taskAdapter.notifyItemChanged(position);
                } else {
                    taskAdapter.addTask(new Task(taskName));
                }
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_task_item, parent, false);
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
                checkBox = itemView.findViewById(R.id.task_checbox);
            }
        }
    }
}
