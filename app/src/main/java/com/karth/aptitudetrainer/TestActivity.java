package com.karth.aptitudetrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
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
    private String testDifficulty = "Easy";
    private CountDownTimer timer;
    private LinearLayout root;
    private LinearLayout questionCard;
    private TextView header;
    private TextView timerView;
    private TextView questionView;
    private TextView sourceView;
    private TextView progressText;
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
        testDifficulty = sp.getString("difficulty", "Easy");
        int count = sp.getInt("count", 5);
        questions = QuestionBank.forDifficulty(testDifficulty, count, ProgressStore.askedQuestionIds(this));
        if (questions.isEmpty()) {
            showNoFreshQuestions();
            return;
        }
        ProgressStore.recordAskedQuestions(this, questions);
        selected = new int[questions.size()];
        for (int i = 0; i < selected.length; i++) selected[i] = -1;
        endAtMillis = System.currentTimeMillis() + Math.max(questions.size(), 1) * 60_000L;
        active = true;
        sp.edit().putBoolean("test_active", true).putBoolean("test_interrupted", false).apply();
        try { startLockTask(); Toast.makeText(this, "Focus mode started. Finish the test to exit.", Toast.LENGTH_LONG).show(); } catch (Exception ignored) {}
        buildTestUi();
        showQuestion(false);
        startTimer();
    }

    private void buildTestUi() {
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        if (Build.VERSION.SDK_INT >= 16) sv.setBackground(UiKit.screenBackground());
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = UiKit.contentPadding(this);
        root.setPadding(pad, UiKit.dp(22), pad, UiKit.dp(28));
        sv.addView(root);

        LinearLayout top = UiKit.card(this);
        header = UiKit.text(this, "", 22, UiKit.INK, true);
        timerView = UiKit.text(this, "", 16, UiKit.HERMES_PURPLE, true);
        progressText = UiKit.text(this, "", 13, UiKit.MUTED, false);
        top.addView(UiKit.poweredByLabel(this));
        top.addView(header);
        top.addView(timerView);
        top.addView(progressText);
        root.addView(top);
        UiKit.fadeSlideIn(top, 0);

        questionCard = UiKit.card(this);
        questionView = UiKit.text(this, "", 19, UiKit.INK, true);
        sourceView = UiKit.text(this, "", 13, UiKit.CODE_APT_CYAN, true);
        sourceView.setPadding(0, UiKit.dp(8), 0, UiKit.dp(4));
        questionCard.addView(questionView);
        questionCard.addView(sourceView);
        optionsGroup = new RadioGroup(this);
        optionsGroup.setOrientation(RadioGroup.VERTICAL);
        questionCard.addView(optionsGroup);
        root.addView(questionCard);
        UiKit.fadeSlideIn(questionCard, 100);

        Button hintBtn = UiKit.secondaryButton(this, "Show hint");
        root.addView(hintBtn);
        hintBtn.setOnClickListener(v -> {
            UiKit.pulseOnce(hintBtn);
            Question q = questions.get(index);
            new AlertDialog.Builder(this)
                    .setTitle("Hint")
                    .setMessage(q.hint + "\n\nSource: " + q.sourceLabel())
                    .setPositiveButton("OK", null)
                    .show();
        });

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setOrientation(LinearLayout.HORIZONTAL);
        prevBtn = UiKit.secondaryButton(this, "Previous");
        nextBtn = UiKit.secondaryButton(this, "Next");
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, -2, 1);
        btnLp.setMargins(UiKit.dp(4), UiKit.dp(8), UiKit.dp(4), UiKit.dp(8));
        row.addView(prevBtn, btnLp);
        row.addView(nextBtn, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(row);
        finishBtn = UiKit.primaryButton(this, "Finish test");
        root.addView(finishBtn);

        prevBtn.setOnClickListener(v -> { saveSelected(); if (index > 0) { index--; showQuestion(true); }});
        nextBtn.setOnClickListener(v -> { saveSelected(); if (index < questions.size() - 1) { index++; showQuestion(true); }});
        finishBtn.setOnClickListener(v -> { UiKit.pulseOnce(finishBtn); confirmFinish(); });
        setContentView(sv);
    }

    private void showNoFreshQuestions() {
        completed = true;
        active = false;
        LinearLayout ll = new LinearLayout(this);
        if (Build.VERSION.SDK_INT >= 16) ll.setBackground(UiKit.screenBackground());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(UiKit.dp(30), UiKit.dp(36), UiKit.dp(30), UiKit.dp(36));
        LinearLayout card = UiKit.card(this);
        card.addView(UiKit.poweredByLabel(this));
        card.addView(UiKit.text(this, "No new questions left", 28, UiKit.INK, true));
        card.addView(UiKit.text(this, "You have already seen every " + testDifficulty + " question in this app, so this test was not started.", 16, UiKit.MUTED, false));
        Button b = UiKit.primaryButton(this, "Back to dashboard");
        card.addView(b);
        ll.addView(card);
        b.setOnClickListener(v -> finish());
        setContentView(ll);
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

    private void showQuestion(boolean animate) {
        Runnable update = () -> renderQuestion(questions.get(index));
        if (animate) {
            UiKit.crossFadeContent(questionCard, update);
        } else {
            update.run();
        }
    }

    private void renderQuestion(Question q) {
        header.setText(q.difficulty + " aptitude • Question " + (index + 1) + " of " + questions.size());
        int answered = 0;
        for (int s : selected) if (s >= 0) answered++;
        progressText.setText("Answered " + answered + "/" + questions.size() + " • Screenshots blocked • Secure focus mode");
        questionView.setText(q.text);
        sourceView.setText(q.sourceLabel());
        optionsGroup.removeAllViews();
        for (int i = 0; i < q.options.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText((char)('A' + i) + ". " + q.options[i]);
            rb.setTextSize(UiKit.responsiveSp(this, 16));
            rb.setTextColor(UiKit.INK);
            rb.setTypeface(Typeface.DEFAULT_BOLD);
            rb.setId(100 + i);
            rb.setPadding(UiKit.dp(8), UiKit.dp(14), UiKit.dp(8), UiKit.dp(14));
            optionsGroup.addView(rb);
            UiKit.fadeSlideIn(rb, 40L * i);
        }
        if (selected[index] >= 0) optionsGroup.check(100 + selected[index]); else optionsGroup.clearCheck();
        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
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
        new AlertDialog.Builder(this).setTitle("Finish test").setMessage(msg).setNegativeButton("Continue test", null).setPositiveButton("Submit", (d, w) -> finishTest()).show();
    }

    private void finishTest() {
        if (completed) return;
        saveSelected();
        completed = true;
        active = false;
        if (timer != null) timer.cancel();
        try { stopLockTask(); } catch (Exception ignored) {}
        int score = calculateScore();
        ProgressStore.recordCompletedTest(this, testDifficulty, score, questions.size());
        getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putBoolean("test_active", false).putBoolean("test_interrupted", false).apply();
        showResults(score);
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) if (selected[i] == questions.get(i).answerIndex) score++;
        return score;
    }

    private void showResults(int score) {
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        if (Build.VERSION.SDK_INT >= 16) sv.setBackground(UiKit.screenBackground());
        LinearLayout rr = new LinearLayout(this);
        rr.setOrientation(LinearLayout.VERTICAL);
        int pad = UiKit.contentPadding(this);
        rr.setPadding(pad, UiKit.dp(22), pad, UiKit.dp(28));
        sv.addView(rr);

        int percent = questions.isEmpty() ? 0 : Math.round(score * 100f / questions.size());
        LinearLayout summary = UiKit.card(this);
        TextView title = UiKit.text(this, "Test completed", 28, UiKit.INK, true);
        TextView scoreView = UiKit.text(this, score + "/" + questions.size() + " • " + percent + "%", 32, percent >= 60 ? UiKit.HERMES_GREEN : UiKit.DANGER, true);
        summary.addView(title);
        summary.addView(scoreView);
        summary.addView(UiKit.text(this, "Your dashboard has been updated.", 14, UiKit.MUTED, false));
        rr.addView(summary);
        UiKit.scalePopIn(summary, 0);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            LinearLayout item = UiKit.card(this);
            boolean ok = selected[i] == q.answerIndex;
            item.addView(UiKit.text(this, (ok ? "✓ Correct" : "✕ Review") + " • Q" + (i + 1), 16, ok ? UiKit.HERMES_GREEN : UiKit.DANGER, true));
            item.addView(UiKit.text(this, q.text, 15, UiKit.INK, true));
            item.addView(UiKit.text(this, q.sourceLabel(), 12, UiKit.CODE_APT_CYAN, true));
            item.addView(UiKit.text(this, "Your answer: " + answerText(q, selected[i]), 14, UiKit.MUTED, false));
            item.addView(UiKit.text(this, "Correct answer: " + answerText(q, q.answerIndex), 14, UiKit.HERMES_GREEN, true));
            TextView expTitle = UiKit.text(this, "Step-by-step explanation", 14, UiKit.INK, true);
            expTitle.setPadding(0, UiKit.dp(8), 0, UiKit.dp(4));
            item.addView(expTitle);
            item.addView(UiKit.text(this, q.explanation, 14, UiKit.MUTED, false));
            rr.addView(item);
            UiKit.fadeSlideIn(item, 60L * i);
        }
        Button done = UiKit.primaryButton(this, "Back to dashboard");
        rr.addView(done);
        done.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
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
        ProgressStore.recordInterrupted(this);
        getSharedPreferences(Scheduler.PREFS, MODE_PRIVATE).edit().putBoolean("test_active", false).putBoolean("test_interrupted", true).apply();
        LinearLayout ll = new LinearLayout(this);
        if (Build.VERSION.SDK_INT >= 16) ll.setBackground(UiKit.screenBackground());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(UiKit.dp(30), UiKit.dp(36), UiKit.dp(30), UiKit.dp(36));
        LinearLayout card = UiKit.card(this);
        TextView msg = UiKit.text(this, "Test stopped", 28, UiKit.DANGER, true);
        card.addView(msg);
        card.addView(UiKit.text(this, "The test was closed before completion, so this attempt has been stopped and recorded as interrupted.", 16, UiKit.MUTED, false));
        Button b = UiKit.primaryButton(this, "Back to dashboard");
        card.addView(b);
        ll.addView(card);
        b.setOnClickListener(v -> finish());
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
}
