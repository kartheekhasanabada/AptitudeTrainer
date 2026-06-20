package com.karth.aptitudetrainer;

import android.Manifest;
import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int selectedHour = 7;
    private int selectedMinute = 0;
    private String selectedDifficulty = "Easy";
    private int selectedCount = 5;
    private TextView scheduleText;
    private TextView countHint;
    private LinearLayout dashboardCard;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Scheduler.createChannel(this);
        loadPrefs();
        requestImportantPermissions();
        buildUi();
    }

    @Override protected void onResume() {
        super.onResume();
        if (dashboardCard != null) refreshDashboard();
        if (scheduleText != null) scheduleText.setText(Scheduler.scheduleSummary(this));
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
        if (Build.VERSION.SDK_INT >= 16) sv.setBackground(UiKit.screenBackground());
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(UiKit.dp(20), UiKit.dp(22), UiKit.dp(20), UiKit.dp(28));
        sv.addView(root);

        LinearLayout hero = UiKit.card(this);
        TextView eyebrow = UiKit.text(this, "HERMES × GLASS LEARNING", 12, UiKit.HERMES_BLUE, true);
        TextView title = UiKit.text(this, "Aptitude Trainer", 34, UiKit.INK, true);
        TextView subtitle = UiKit.text(this, "Daily interview-style practice with secure scheduled tests, hints, explanations and progress tracking.", 15, UiKit.MUTED, false);
        hero.addView(eyebrow);
        hero.addView(title);
        hero.addView(subtitle);
        root.addView(hero);

        dashboardCard = UiKit.card(this);
        root.addView(dashboardCard);
        refreshDashboard();

        LinearLayout scheduleCard = UiKit.card(this);
        scheduleCard.addView(UiKit.text(this, "Daily Test Setup", 22, UiKit.INK, true));
        scheduleCard.addView(UiKit.text(this, "Choose when the app should remind and open the test.", 14, UiKit.MUTED, false));

        scheduleText = UiKit.text(this, Scheduler.scheduleSummary(this), 16, UiKit.HERMES_PURPLE, true);
        scheduleText.setPadding(0, UiKit.dp(16), 0, UiKit.dp(12));
        scheduleCard.addView(scheduleText);

        Button timeBtn = UiKit.secondaryButton(this, String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
        scheduleCard.addView(timeBtn);
        timeBtn.setOnClickListener(v -> new TimePickerDialog(this, (view, hour, minute) -> {
            selectedHour = hour; selectedMinute = minute;
            timeBtn.setText(String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
        }, selectedHour, selectedMinute, true).show());

        scheduleCard.addView(sectionLabel("Difficulty level"));
        Spinner difficulty = new Spinner(this);
        String[] diffs = new String[]{"Easy", "Medium", "Hard"};
        difficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diffs));
        for (int i=0; i<diffs.length; i++) if (diffs[i].equals(selectedDifficulty)) difficulty.setSelection(i);
        scheduleCard.addView(difficulty);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { selectedDifficulty = diffs[position]; updateCountHint(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        scheduleCard.addView(sectionLabel("Number of questions"));
        Spinner count = new Spinner(this);
        Integer[] counts = new Integer[]{5, 10};
        count.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, counts));
        count.setSelection(selectedCount >= 10 ? 1 : 0);
        scheduleCard.addView(count);
        count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { selectedCount = counts[position]; }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        countHint = UiKit.text(this, "", 13, UiKit.MUTED, false);
        scheduleCard.addView(countHint);
        updateCountHint();

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(0, UiKit.dp(18), 0, 0);
        Button save = UiKit.primaryButton(this, "Save schedule");
        Button startNow = UiKit.secondaryButton(this, "Start practice test now");
        actions.addView(save);
        actions.addView(startNow);
        scheduleCard.addView(actions);

        save.setOnClickListener(v -> {
            Scheduler.saveSchedule(this, selectedHour, selectedMinute, selectedDifficulty, selectedCount);
            scheduleText.setText(Scheduler.scheduleSummary(this));
            Toast.makeText(this, "Daily test scheduled securely", Toast.LENGTH_LONG).show();
        });
        startNow.setOnClickListener(v -> {
            getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putString("difficulty", selectedDifficulty).putInt("count", selectedCount).apply();
            Intent it = new Intent(this, TestActivity.class);
            it.putExtra("scheduled", false);
            startActivity(it);
        });
        root.addView(scheduleCard);

        LinearLayout security = UiKit.card(this);
        security.addView(UiKit.text(this, "Security Mode", 20, UiKit.INK, true));
        security.addView(UiKit.text(this, "No internet permission • private storage • screenshots blocked during tests • exact alarms • best-effort screen pinning.", 14, UiKit.MUTED, false));
        root.addView(security);

        setContentView(sv);
    }

    private void refreshDashboard() {
        dashboardCard.removeAllViews();
        ProgressStore.Snapshot p = ProgressStore.snapshot(this);
        dashboardCard.addView(UiKit.text(this, "Student Dashboard", 22, UiKit.INK, true));
        dashboardCard.addView(UiKit.text(this, "Your private progress summary on this phone.", 14, UiKit.MUTED, false));

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(UiKit.metricCard(this, String.valueOf(p.tests), "Tests", UiKit.HERMES_BLUE));
        row1.addView(UiKit.metricCard(this, p.averagePercent + "%", "Average", UiKit.HERMES_GREEN));
        dashboardCard.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(UiKit.metricCard(this, p.bestPercent + "%", "Best", UiKit.HERMES_PURPLE));
        row2.addView(UiKit.metricCard(this, String.valueOf(p.streak), "Day streak", Color.rgb(255, 145, 50)));
        dashboardCard.addView(row2);

        TextView last = UiKit.text(this, "Last test: " + p.lastDifficulty + " • " + (p.lastTotal == 0 ? "No score yet" : p.lastScore + "/" + p.lastTotal + " (" + p.lastPercent + "%)") + "\n" + p.lastTime, 14, UiKit.MUTED, false);
        last.setPadding(0, UiKit.dp(10), 0, UiKit.dp(6));
        dashboardCard.addView(last);

        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(p.averagePercent);
        dashboardCard.addView(bar, new LinearLayout.LayoutParams(-1, UiKit.dp(12)));
        TextView details = UiKit.text(this, "Solved correctly: " + p.correct + "/" + p.questions + " questions • Interrupted attempts: " + p.interrupted, 13, UiKit.MUTED, false);
        details.setPadding(0, UiKit.dp(8), 0, 0);
        dashboardCard.addView(details);
    }

    private void updateCountHint() {
        if (countHint != null) countHint.setText("Available " + selectedDifficulty + " questions: " + QuestionBank.countForDifficulty(selectedDifficulty) + ". Questions are shuffled each test.");
    }

    private TextView sectionLabel(String s) { TextView t = UiKit.text(this, s, 15, UiKit.INK, true); t.setPadding(0, UiKit.dp(18), 0, UiKit.dp(6)); return t; }
}
