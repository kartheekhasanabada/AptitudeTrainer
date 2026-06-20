package com.karth.aptitudetrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TestActivity extends Activity {
    private List<Question> questions;
    private int[] selected;
    private int index = 0;
    private boolean completed = false;
    private boolean active = false;
    private long endAtMillis;
    private CountDownTimer timer;
    private LinearLayout root;
    private TextView header;
    private TextView timerView;
    private TextView questionView;
    private RadioGroup optionsGroup;
    private Button prevBtn;
    private Button nextBtn;
    private Button finishBtn;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(Scheduler.NOTIFICATION_ID);
        SharedPreferences sp = getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE);
        String difficulty = sp.getString("difficulty", "Easy");
        int count = sp.getInt("count", 5);
        questions = QuestionBank.forDifficulty(difficulty, count);
        selected = new int[questions.size()];
        for (int i = 0; i < selected.length; i++) selected[i] = -1;
        endAtMillis = System.currentTimeMillis() + Math.max(questions.size(), 1) * 60_000L;
        active = true;
        sp.edit().putBoolean("test_active", true).putBoolean("test_interrupted", false).apply();
        try { startLockTask(); Toast.makeText(this, "Screen pinning started. Finish the test to exit.", Toast.LENGTH_LONG).show(); } catch (Exception ignored) {}
        buildTestUi();
        showQuestion();
        startTimer();
    }

    private void buildTestUi() {
        ScrollView sv = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(Color.WHITE);
        sv.addView(root);
        header = text("", 20, true);
        timerView = text("", 16, true);
        questionView = text("", 18, true);
        root.addView(header);
        root.addView(timerView);
        root.addView(questionView);
        optionsGroup = new RadioGroup(this);
        optionsGroup.setOrientation(RadioGroup.VERTICAL);
        root.addView(optionsGroup);

        Button hintBtn = button("Show hint");
        root.addView(hintBtn);
        hintBtn.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Hint").setMessage(questions.get(index).hint).setPositiveButton("OK", null).show());

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setOrientation(LinearLayout.HORIZONTAL);
        prevBtn = button("Previous");
        nextBtn = button("Next");
        finishBtn = button("Finish test");
        row.addView(prevBtn);
        row.addView(nextBtn);
        root.addView(row);
        root.addView(finishBtn);
        prevBtn.setOnClickListener(v -> { saveSelected(); if (index > 0) { index--; showQuestion(); }});
        nextBtn.setOnClickListener(v -> { saveSelected(); if (index < questions.size()-1) { index++; showQuestion(); }});
        finishBtn.setOnClickListener(v -> confirmFinish());
        setContentView(sv);
    }

    private void startTimer() {
        timer = new CountDownTimer(endAtMillis - System.currentTimeMillis(), 1000) {
            @Override public void onTick(long ms) {
                long s = ms / 1000;
                timerView.setText("Time left: " + (s / 60) + "m " + (s % 60) + "s");
            }
            @Override public void onFinish() {
                Toast.makeText(TestActivity.this, "Time over. Test submitted.", Toast.LENGTH_LONG).show();
                finishTest();
            }
        };
        timer.start();
    }

    private void showQuestion() {
        Question q = questions.get(index);
        header.setText(q.difficulty + " aptitude test • Question " + (index + 1) + " of " + questions.size());
        questionView.setText("\n" + q.text + "\n");
        optionsGroup.removeAllViews();
        for (int i=0; i<q.options.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText((char)('A' + i) + ". " + q.options[i]);
            rb.setTextSize(16);
            rb.setId(100 + i);
            rb.setPadding(0, 12, 0, 12);
            optionsGroup.addView(rb);
        }
        if (selected[index] >= 0) optionsGroup.check(100 + selected[index]); else optionsGroup.clearCheck();
        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size()-1);
    }

    private void saveSelected() {
        int id = optionsGroup.getCheckedRadioButtonId();
        selected[index] = id >= 100 ? id - 100 : -1;
    }

    private void confirmFinish() {
        saveSelected();
        int unanswered = 0;
        for (int s : selected) if (s < 0) unanswered++;
        String msg = unanswered == 0 ? "Submit your answers now?" : "You have " + unanswered + " unanswered question(s). Submit anyway?";
        new AlertDialog.Builder(this).setTitle("Finish test").setMessage(msg).setNegativeButton("Continue test", null).setPositiveButton("Submit", (d,w) -> finishTest()).show();
    }

    private void finishTest() {
        if (completed) return;
        saveSelected();
        completed = true;
        active = false;
        if (timer != null) timer.cancel();
        try { stopLockTask(); } catch (Exception ignored) {}
        getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putBoolean("test_active", false).putBoolean("test_interrupted", false).apply();
        showResults();
    }

    private void showResults() {
        ScrollView sv = new ScrollView(this);
        LinearLayout rr = new LinearLayout(this);
        rr.setOrientation(LinearLayout.VERTICAL);
        rr.setPadding(32, 32, 32, 32);
        sv.addView(rr);
        int score = 0;
        for (int i=0; i<questions.size(); i++) if (selected[i] == questions.get(i).answerIndex) score++;
        TextView title = text("Test completed: " + score + "/" + questions.size(), 26, true);
        title.setTextColor(Color.rgb(0, 120, 80));
        rr.addView(title);
        for (int i=0; i<questions.size(); i++) {
            Question q = questions.get(i);
            TextView item = text("\nQ" + (i+1) + ". " + q.text + "\nYour answer: " + answerText(q, selected[i]) + "\nCorrect answer: " + answerText(q, q.answerIndex) + "\nExplanation: " + q.explanation, 15, false);
            item.setTextColor(selected[i] == q.answerIndex ? Color.rgb(0, 100, 60) : Color.rgb(160, 40, 40));
            rr.addView(item);
        }
        Button done = button("Back to home");
        rr.addView(done);
        done.setOnClickListener(v -> finish());
        setContentView(sv);
    }

    private String answerText(Question q, int ans) {
        if (ans < 0 || ans >= q.options.length) return "Not answered";
        return (char)('A' + ans) + ". " + q.options[ans];
    }

    private void showInterrupted() {
        completed = true;
        active = false;
        if (timer != null) timer.cancel();
        try { stopLockTask(); } catch (Exception ignored) {}
        getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putBoolean("test_active", false).putBoolean("test_interrupted", true).apply();
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 40, 40, 40);
        TextView msg = text("Test stopped", 26, true);
        msg.setTextColor(Color.rgb(180, 40, 40));
        ll.addView(msg);
        ll.addView(text("The test was closed before completion, so this attempt has been stopped. Start a new test from the home screen.", 17, false));
        Button b = button("Close"); ll.addView(b); b.setOnClickListener(v -> finish());
        setContentView(ll);
    }

    @Override public void onBackPressed() {
        Toast.makeText(this, "Back is disabled during the test. Submit to exit.", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onStop() {
        super.onStop();
        if (active && !completed && !isChangingConfigurations()) {
            showInterrupted();
        }
    }

    private TextView text(String s, int sp, boolean bold) { TextView t = new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.rgb(30, 35, 45)); if (bold) t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); return t; }
    private Button button(String s) { Button b = new Button(this); b.setText(s); b.setAllCaps(false); return b; }
}
