package com.example.slearnapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyPlanner2 extends AppCompatActivity {

    private TextView tvCurrentMonth, tvEventDetails;
    private Button btnPreviousMonth, btnNextMonth, btnEditEvent;
    private HashMap<String, String> eventMap;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GridView gridCalendar;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_study_planner2); // Use your layout file

        calendar = Calendar.getInstance();
        eventMap = new HashMap<>();

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Load saved events
        loadEvents();

        // Initialize views
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        gridCalendar = findViewById(R.id.gridCalendar);
        tvEventDetails = findViewById(R.id.tvEventDetails);
        btnEditEvent = findViewById(R.id.btnEditEvent);

        updateCalendar();

        btnPreviousMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        gridCalendar.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDate = getDateFromPosition(position);
            if (eventMap.containsKey(selectedDate)) {
                tvEventDetails.setText(selectedDate + "\n" + eventMap.get(selectedDate));
            } else {
                tvEventDetails.setText(selectedDate + "\nNo Events");
            }
        });

        btnEditEvent.setOnClickListener(v -> {
            String date = tvEventDetails.getText().toString().split("\n")[0];
            openEventDialog(date);
        });
    }

    private void updateCalendar() {
        String monthYear = android.text.format.DateFormat.format("MMMM yyyy", calendar).toString();
        tvCurrentMonth.setText(monthYear);

        List<String> days = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add("");
        }
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        CalendarAdapter adapter = new CalendarAdapter(this, days, eventMap);
        gridCalendar.setAdapter(adapter);
    }

    private String getDateFromPosition(int position) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int offset = position - calendar.get(Calendar.DAY_OF_WEEK) + 2;
        calendar.set(Calendar.DAY_OF_MONTH, offset);
        return android.text.format.DateFormat.format("dd MMMM yyyy", calendar).toString();
    }

    private void openEventDialog(String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_event, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etEventInput = dialogView.findViewById(R.id.etEventInput);
        Button btnSaveEvent = dialogView.findViewById(R.id.btnSaveEvent);
        Button btnCancelEvent = dialogView.findViewById(R.id.btnCancelEvent);

        tvDialogTitle.setText("Add Event for " + date);

        if (eventMap.containsKey(date)) {
            etEventInput.setText(eventMap.get(date));
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSaveEvent.setOnClickListener(v -> {
            String eventName = etEventInput.getText().toString().trim();
            if (!eventName.isEmpty()) {
                eventMap.put(date, eventName);
                editor.putString(date, eventName);
                editor.apply();
                tvEventDetails.setText(date + "\n" + eventName);
                Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
            } else {
                eventMap.remove(date);
                editor.remove(date);
                editor.apply();
                tvEventDetails.setText("");
                Toast.makeText(this, "Event cleared!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            updateCalendar();
        });

        btnCancelEvent.setOnClickListener(v -> dialog.dismiss());
    }

    private void loadEvents() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            eventMap.put(entry.getKey(), entry.getValue().toString());
        }
    }
}


class CalendarAdapter extends android.widget.BaseAdapter {
    private Context context;
    private List<String> days;
    private HashMap<String, String> eventMap;

    public CalendarAdapter(Context context, List<String> days, HashMap<String, String> eventMap) {
        this.context = context;
        this.days = days;
        this.eventMap = eventMap;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return days.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = (TextView) convertView;
        String day = days.get(position);
        textView.setText(day);

        String date = getDateFromPosition(position);
        if (eventMap.containsKey(date)) {
            textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            textView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    private String getDateFromPosition(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int offset = position - calendar.get(Calendar.DAY_OF_WEEK) + 2;
        calendar.set(Calendar.DAY_OF_MONTH, offset);
        return android.text.format.DateFormat.format("dd MMMM yyyy", calendar).toString();
    }
}

class EventDialog extends AlertDialog {
    private Context context;
    private String date;
    private HashMap<String, String> eventMap;
    private Runnable onEventSaved;

    protected EventDialog(Context context, String date, HashMap<String, String> eventMap, Runnable onEventSaved) {
        super(context);
        this.context = context;
        this.date = date;
        this.eventMap = eventMap;
        this.onEventSaved = onEventSaved;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_event, null);

        // Set the custom view for the dialog
        setView(view);

        // Initialize UI elements
        TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etEventInput = view.findViewById(R.id.etEventInput);
        Button btnSaveEvent = view.findViewById(R.id.btnSaveEvent);

        // Set dialog title to include the selected date
        tvDialogTitle.setText("Add Event for " + date);

        // Pre-fill the EditText if an event already exists for the date
        if (eventMap.containsKey(date)) {
            etEventInput.setText(eventMap.get(date));
        }

        // Save button listener
        btnSaveEvent.setOnClickListener(v -> {
            String eventName = etEventInput.getText().toString();
            if (!eventName.isEmpty()) {
                // Save the event to the map
                eventMap.put(date, eventName);

                // Notify the user
                Toast.makeText(context, "Event saved!", Toast.LENGTH_SHORT).show();

                // Trigger the callback to update the calendar
                onEventSaved.run();

                // Close the dialog
                dismiss();
            } else {
                Toast.makeText(context, "Event cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
