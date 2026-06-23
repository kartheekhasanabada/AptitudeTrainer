package com.karth.aptitudetrainer;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int MAX_TEST_QUESTIONS = 10000;
    private int selectedHour = 7;
    private int selectedMinute = 0;
    private String selectedDifficulty = "Easy";
    private String selectedCompany = QuestionBank.ALL_COMPANIES;
    private int selectedCount = 5;
    private TextView scheduleText;
    private TextView countHint;
    private EditText countInput;
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
        selectedCompany = QuestionBank.normalizeCompany(sp.getString("company", QuestionBank.ALL_COMPANIES));
        selectedCount = clampQuestionCount(sp.getInt("count", 5));
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
        if (Build.VERSION.SDK_INT >= 34) {
            android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nm != null && !nm.canUseFullScreenIntent()) {
                try {
                    Intent i = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                } catch (Exception ignored) {}
            }
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && Build.VERSION.SDK_INT >= 23 && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            try {
                Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                i.setData(Uri.parse("package:" + getPackageName()));
                startActivity(i);
            } catch (Exception ignored) {}
        }
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        if (Build.VERSION.SDK_INT >= 16) sv.setBackground(UiKit.screenBackground());
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiKit.contentPadding(this);
        root.setPadding(pad, UiKit.dp(22), pad, UiKit.dp(28));
        sv.addView(root);

        LinearLayout hero = UiKit.card(this);
        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView logo = UiKit.appLogo(this);
        LinearLayout brandText = new LinearLayout(this);
        brandText.setOrientation(LinearLayout.VERTICAL);
        brandText.setPadding(UiKit.dp(12), 0, 0, 0);
        brandText.addView(UiKit.poweredByLabel(this));
        brandRow.addView(logo);
        brandRow.addView(brandText, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = UiKit.text(this, "Aptitude Trainer", 34, UiKit.INK, true);
        title.setPadding(0, UiKit.dp(14), 0, 0);
        TextView subtitle = UiKit.text(this, "Daily interview-style practice with secure scheduled tests, hints, step-by-step explanations and progress tracking.", 15, UiKit.MUTED, false);
        subtitle.setPadding(0, UiKit.dp(6), 0, 0);

        hero.addView(brandRow);
        hero.addView(title);
        hero.addView(subtitle);
        root.addView(hero);
        UiKit.scalePopIn(hero, 0);

        dashboardCard = UiKit.card(this);
        if (Build.VERSION.SDK_INT >= 16) dashboardCard.setBackground(UiKit.dashboardGlass());
        root.addView(dashboardCard);
        refreshDashboard();
        UiKit.fadeSlideIn(dashboardCard, 80);

        LinearLayout scheduleCard = UiKit.card(this);
        scheduleCard.addView(UiKit.text(this, "Daily Test Setup", 22, UiKit.INK, true));
        scheduleCard.addView(UiKit.text(this, "Choose when the app should remind and open the test.", 14, UiKit.MUTED, false));

        scheduleText = UiKit.text(this, Scheduler.scheduleSummary(this), 16, UiKit.HERMES_PURPLE, true);
        scheduleText.setPadding(0, UiKit.dp(16), 0, UiKit.dp(12));
        scheduleCard.addView(scheduleText);

        Button timeBtn = UiKit.secondaryButton(this, String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
        scheduleCard.addView(timeBtn);
        timeBtn.setOnClickListener(v -> {
            UiKit.pulseOnce(timeBtn);
            new TimePickerDialog(this, (view, hour, minute) -> {
                selectedHour = hour;
                selectedMinute = minute;
                timeBtn.setText(String.format("Choose daily time: %02d:%02d", selectedHour, selectedMinute));
            }, selectedHour, selectedMinute, true).show();
        });

        scheduleCard.addView(sectionLabel("Difficulty level"));
        Spinner difficulty = new Spinner(this);
        String[] diffs = new String[]{"Easy", "Medium", "Hard"};
        difficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diffs));
        for (int i = 0; i < diffs.length; i++) if (diffs[i].equals(selectedDifficulty)) difficulty.setSelection(i);
        scheduleCard.addView(difficulty);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { selectedDifficulty = diffs[position]; updateCountHint(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        scheduleCard.addView(sectionLabel("Company focus"));
        Spinner company = new Spinner(this);
        String[] companies = QuestionBank.companyChoices();
        company.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companies));
        company.setSelection(indexOf(companies, selectedCompany));
        scheduleCard.addView(company);
        company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCompany = companies[position];
                updateCountHint();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        scheduleCard.addView(sectionLabel("Number of questions"));
        countInput = new EditText(this);
        countInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        countInput.setSingleLine(true);
        countInput.setText(String.valueOf(selectedCount));
        countInput.setTextSize(UiKit.responsiveSp(this, 18));
        countInput.setTextColor(UiKit.INK);
        countInput.setHint("Enter questions");
        countInput.setPadding(UiKit.dp(14), UiKit.dp(12), UiKit.dp(14), UiKit.dp(12));
        if (Build.VERSION.SDK_INT >= 16) countInput.setBackground(UiKit.inputBackground());
        scheduleCard.addView(countInput, new LinearLayout.LayoutParams(-1, -2));
        countHint = UiKit.text(this, "", 13, UiKit.MUTED, false);
        scheduleCard.addView(countHint);
        updateCountHint();

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(0, UiKit.dp(18), 0, 0);
        Button save = UiKit.primaryButton(this, "Save schedule");
        Button startNow = UiKit.secondaryButton(this, "Start practice test now");
        Button cancelSchedule = UiKit.dangerButton(this, "Cancel schedule");
        actions.addView(save);
        actions.addView(startNow);
        actions.addView(cancelSchedule);
        scheduleCard.addView(actions);

        save.setOnClickListener(v -> {
            UiKit.pulseOnce(save);
            selectedCount = readRequestedCount();
            Scheduler.saveSchedule(this, selectedHour, selectedMinute, selectedDifficulty, selectedCompany, selectedCount);
            scheduleText.setText(Scheduler.scheduleSummary(this));
            Toast.makeText(this, "Daily " + selectedCompany + " test scheduled with " + selectedCount + " questions", Toast.LENGTH_LONG).show();
        });
        cancelSchedule.setOnClickListener(v -> {
            UiKit.pulseOnce(cancelSchedule);
            Scheduler.cancelSchedule(this);
            scheduleText.setText(Scheduler.scheduleSummary(this));
            Toast.makeText(this, "Daily test schedule cancelled", Toast.LENGTH_LONG).show();
        });
        startNow.setOnClickListener(v -> {
            UiKit.pulseOnce(startNow);
            selectedCount = readRequestedCount();
            getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE)
                    .edit()
                    .putString("difficulty", selectedDifficulty)
                    .putString("company", selectedCompany)
                    .putInt("count", selectedCount)
                    .apply();
            Intent it = new Intent(this, TestActivity.class);
            it.putExtra("scheduled", false);
            startActivity(it);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        root.addView(scheduleCard);
        UiKit.fadeSlideIn(scheduleCard, 160);

        LinearLayout security = UiKit.card(this);
        security.addView(UiKit.text(this, "Security Mode", 20, UiKit.INK, true));
        security.addView(UiKit.text(this, "No internet permission • private storage • screenshots blocked during tests • exact alarms • best-effort screen pinning.", 14, UiKit.MUTED, false));
        root.addView(security);
        UiKit.fadeSlideIn(security, 240);

        setContentView(sv);
    }

    private void refreshDashboard() {
        dashboardCard.removeAllViews();
        ProgressStore.Snapshot p = ProgressStore.snapshot(this);
        TextView dashTitle = UiKit.text(this, "Learning Dashboard", 24, UiKit.INK, true);
        TextView dashSub = UiKit.text(this, "A colorful glass view for consistency, focus, and interview readiness.", 14, UiKit.MUTED, false);
        dashboardCard.addView(dashTitle);
        dashboardCard.addView(dashSub);

        LinearLayout focus = new LinearLayout(this);
        focus.setOrientation(LinearLayout.VERTICAL);
        focus.setPadding(UiKit.dp(14), UiKit.dp(12), UiKit.dp(14), UiKit.dp(12));
        if (Build.VERSION.SDK_INT >= 16) focus.setBackground(UiKit.learningGlass(UiKit.HERMES_BLUE));
        TextView focusTitle = UiKit.text(this, "Today's Focus", 13, UiKit.MUTED, true);
        TextView focusValue = UiKit.text(this, selectedCompany + " • " + selectedDifficulty + " • " + selectedCount + " questions", 18, UiKit.HERMES_BLUE, true);
        TextView focusHint = UiKit.text(this, "Keep your streak alive with one focused session.", 13, UiKit.MUTED, false);
        focus.addView(focusTitle);
        focus.addView(focusValue);
        focus.addView(focusHint);
        LinearLayout.LayoutParams focusLp = new LinearLayout.LayoutParams(-1, -2);
        focusLp.setMargins(0, UiKit.dp(12), 0, UiKit.dp(8));
        dashboardCard.addView(focus, focusLp);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(UiKit.metricCard(this, String.valueOf(p.tests), "Tests", UiKit.HERMES_BLUE));
        row1.addView(UiKit.metricCard(this, p.averagePercent + "%", "Average", UiKit.HERMES_GREEN));
        dashboardCard.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(UiKit.metricCard(this, p.bestPercent + "%", "Best", UiKit.HERMES_PURPLE));
        row2.addView(UiKit.metricCard(this, String.valueOf(p.streak), "Day streak", UiKit.LEARNING_GOLD));
        dashboardCard.addView(row2);

        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.addView(UiKit.learningTile(this, "Questions Solved", String.valueOf(p.questions), UiKit.HERMES_PURPLE));
        row3.addView(UiKit.learningTile(this, "Accuracy Count", String.valueOf(p.correct), UiKit.LEARNING_PINK));
        dashboardCard.addView(row3);

        TextView last = UiKit.text(this, "Last test: " + p.lastDifficulty + " • " + (p.lastTotal == 0 ? "No score yet" : p.lastScore + "/" + p.lastTotal + " (" + p.lastPercent + "%)") + "\n" + p.lastTime, 14, UiKit.MUTED, false);
        last.setPadding(0, UiKit.dp(10), 0, UiKit.dp(6));
        dashboardCard.addView(last);

        LinearLayout progressPanel = new LinearLayout(this);
        progressPanel.setOrientation(LinearLayout.VERTICAL);
        progressPanel.setPadding(UiKit.dp(14), UiKit.dp(12), UiKit.dp(14), UiKit.dp(12));
        if (Build.VERSION.SDK_INT >= 16) progressPanel.setBackground(UiKit.learningGlass(p.averagePercent >= 70 ? UiKit.HERMES_GREEN : UiKit.LEARNING_GOLD));
        progressPanel.addView(UiKit.text(this, "Progress Glow", 13, UiKit.MUTED, true));
        progressPanel.addView(UiKit.text(this, p.averagePercent + "% average accuracy", 20, p.averagePercent >= 70 ? UiKit.HERMES_GREEN : UiKit.LEARNING_GOLD, true));
        LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(-1, -2);
        progressLp.setMargins(0, UiKit.dp(8), 0, UiKit.dp(8));
        dashboardCard.addView(progressPanel, progressLp);

        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(p.averagePercent);
        dashboardCard.addView(bar, new LinearLayout.LayoutParams(-1, UiKit.dp(12)));
        TextView details = UiKit.text(this, "Correct answers: " + p.correct + "/" + p.questions + " • Interrupted attempts: " + p.interrupted, 13, UiKit.MUTED, false);
        details.setPadding(0, UiKit.dp(8), 0, 0);
        dashboardCard.addView(details);
    }

    private void updateCountHint() {
        if (countHint != null) {
            int total = QuestionBank.countForDifficulty(selectedDifficulty, selectedCompany);
            int fresh = ProgressStore.unseenCountForPractice(this, selectedDifficulty, selectedCompany);
            String focus = QuestionBank.ALL_COMPANIES.equals(selectedCompany) ? "mixed company" : selectedCompany;
            countHint.setText("Fresh " + selectedDifficulty + " " + focus + " questions: " + fresh + "/" + total + ". Type any test size up to " + MAX_TEST_QUESTIONS + ".");
        }
    }

    private int readRequestedCount() {
        int requested = selectedCount;
        if (countInput != null) {
            try {
                requested = Integer.parseInt(countInput.getText().toString().trim());
            } catch (Exception ignored) {
                requested = selectedCount;
            }
        }
        int clamped = clampQuestionCount(requested);
        if (countInput != null && clamped != requested) {
            countInput.setText(String.valueOf(clamped));
            Toast.makeText(this, "Question count adjusted to " + clamped, Toast.LENGTH_SHORT).show();
        }
        return clamped;
    }

    private int clampQuestionCount(int count) {
        if (count < 1) return 1;
        return Math.min(count, MAX_TEST_QUESTIONS);
    }

    private int indexOf(String[] values, String selected) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(selected)) return i;
        }
        return 0;
    }

    private TextView sectionLabel(String s) { TextView t = UiKit.text(this, s, 15, UiKit.INK, true); t.setPadding(0, UiKit.dp(18), 0, UiKit.dp(6)); return t; }
}
