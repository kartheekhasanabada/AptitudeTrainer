package com.karth.aptitudetrainer;

import android.Manifest;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

public class MainActivity extends Activity {
    private int selectedHour = 7;
    private int selectedMinute = 0;
    private String selectedDifficulty = "Easy";
    private int selectedCount = 5;
    private TextView scheduleText;
    private TextView countHint;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Scheduler.createChannel(this);
        loadPrefs();
        requestImportantPermissions();
        buildUi();
    }

    private void loadPrefs() {
        SharedPreferences sp = getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE);
        selectedHour = sp.getInt("hour", 7);
        selectedMinute = sp.getInt("minute", 0);
        selectedDifficulty = sp.getString("difficulty", "Easy");
        selectedCount = sp.getInt("count", 5);
    }

    private void requestImportantPermissions() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        }
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                try { startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)); } catch (Exception ignored) {}
            }
        }
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 36, 36, 36);
        root.setBackgroundColor(Color.rgb(245, 248, 252));
        sv.addView(root);

        TextView title = text("Aptitude Trainer", 30, true);
        title.setTextColor(Color.rgb(27, 54, 93));
        root.addView(title);
        root.addView(text("Schedule a daily company-interview style aptitude test. During a test screenshots are blocked and Android screen pinning/lock-task is requested to discourage closing.", 15, false));

        scheduleText = text(Scheduler.scheduleSummary(this), 18, true);
        scheduleText.setPadding(0, 28, 0, 12);
        root.addView(scheduleText);

        Button timeBtn = button(String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
        root.addView(timeBtn);
        timeBtn.setOnClickListener(v -> new TimePickerDialog(this, (view, hour, minute) -> {
            selectedHour = hour; selectedMinute = minute;
            timeBtn.setText(String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
        }, selectedHour, selectedMinute, true).show());

        root.addView(label("Difficulty level"));
        Spinner difficulty = new Spinner(this);
        String[] diffs = new String[]{"Easy", "Medium", "Hard"};
        difficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diffs));
        for (int i=0; i<diffs.length; i++) if (diffs[i].equals(selectedDifficulty)) difficulty.setSelection(i);
        root.addView(difficulty);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { selectedDifficulty = diffs[position]; updateCountHint(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        root.addView(label("Number of questions"));
        Spinner count = new Spinner(this);
        Integer[] counts = new Integer[]{5, 10};
        count.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, counts));
        count.setSelection(selectedCount >= 10 ? 1 : 0);
        root.addView(count);
        count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { selectedCount = counts[position]; }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        countHint = text("", 13, false);
        root.addView(countHint);
        updateCountHint();

        Button save = button("Save schedule");
        root.addView(save);
        save.setOnClickListener(v -> {
            Scheduler.saveSchedule(this, selectedHour, selectedMinute, selectedDifficulty, selectedCount);
            scheduleText.setText(Scheduler.scheduleSummary(this));
            Toast.makeText(this, "Daily test scheduled securely", Toast.LENGTH_LONG).show();
        });

        Button startNow = button("Start practice test now");
        root.addView(startNow);
        startNow.setOnClickListener(v -> {
            getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putString("difficulty", selectedDifficulty).putInt("count", selectedCount).apply();
            Intent it = new Intent(this, TestActivity.class);
            it.putExtra("scheduled", false);
            startActivity(it);
        });

        TextView note = text("Security note: Android allows absolute forced kiosk mode only for device-owner apps. This app uses exact alarms, full-screen notification, no INTERNET permission, private storage, screenshot blocking, and screen pinning request.", 13, false);
        note.setPadding(0, 28, 0, 0);
        root.addView(note);
        setContentView(sv);
    }

    private void updateCountHint() {
        if (countHint != null) countHint.setText("Available " + selectedDifficulty + " questions: " + QuestionBank.countForDifficulty(selectedDifficulty) + ". Questions are shuffled each test.");
    }

    private TextView label(String s) { TextView t = text(s, 16, true); t.setPadding(0, 22, 0, 6); return t; }
    private TextView text(String s, int sp, boolean bold) { TextView t = new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.rgb(35, 40, 50)); if (bold) t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); return t; }
    private Button button(String s) { Button b = new Button(this); b.setText(s); b.setAllCaps(false); b.setGravity(Gravity.CENTER); return b; }
}
