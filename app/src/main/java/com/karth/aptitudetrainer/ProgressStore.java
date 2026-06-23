package com.karth.aptitudetrainer;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public final class ProgressStore {
    private static final String PREFS = Scheduler.PREFS;
    private static final String ASKED_QUESTION_IDS = "asked_question_ids";

    private ProgressStore() {}

    public static void recordCompletedTest(Context ctx, String difficulty, int score, int total) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int tests = sp.getInt("progress_tests", 0) + 1;
        int questions = sp.getInt("progress_questions", 0) + total;
        int correct = sp.getInt("progress_correct", 0) + score;
        int percent = total == 0 ? 0 : Math.round(score * 100f / total);
        int best = Math.max(sp.getInt("progress_best", 0), percent);
        int streak = computeStreak(sp.getString("progress_last_day", ""), sp.getInt("progress_streak", 0));
        sp.edit()
                .putInt("progress_tests", tests)
                .putInt("progress_questions", questions)
                .putInt("progress_correct", correct)
                .putInt("progress_best", best)
                .putInt("progress_last_score", score)
                .putInt("progress_last_total", total)
                .putInt("progress_last_percent", percent)
                .putString("progress_last_difficulty", difficulty)
                .putString("progress_last_day", today())
                .putString("progress_last_time", new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(new Date()))
                .putInt("progress_streak", streak)
                .apply();
    }

    public static void recordInterrupted(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putInt("progress_interrupted", sp.getInt("progress_interrupted", 0) + 1).apply();
    }

    public static Set<String> askedQuestionIds(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return new HashSet<>(sp.getStringSet(ASKED_QUESTION_IDS, new HashSet<String>()));
    }

    public static void recordAskedQuestions(Context ctx, List<Question> questions) {
        if (questions == null || questions.isEmpty()) return;
        Set<String> ids = askedQuestionIds(ctx);
        for (Question question : questions) {
            ids.add(question.stableId());
        }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putStringSet(ASKED_QUESTION_IDS, ids)
                .apply();
    }

    public static int unseenCountForDifficulty(Context ctx, String difficulty) {
        return unseenCountForPractice(ctx, difficulty, QuestionBank.ALL_COMPANIES);
    }

    public static int unseenCountForPractice(Context ctx, String difficulty, String company) {
        Set<String> askedIds = askedQuestionIds(ctx);
        int used = 0;
        String prefix = difficulty + "|";
        String selectedCompany = QuestionBank.normalizeCompany(company);
        for (String id : askedIds) {
            if (id.startsWith(prefix) && (QuestionBank.ALL_COMPANIES.equals(selectedCompany) || id.startsWith(prefix + selectedCompany + "|"))) used++;
        }
        return Math.max(0, QuestionBank.countForDifficulty(difficulty, selectedCompany) - used);
    }

    public static Snapshot snapshot(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int tests = sp.getInt("progress_tests", 0);
        int q = sp.getInt("progress_questions", 0);
        int correct = sp.getInt("progress_correct", 0);
        int avg = q == 0 ? 0 : Math.round(correct * 100f / q);
        return new Snapshot(
                tests,
                q,
                correct,
                avg,
                sp.getInt("progress_best", 0),
                sp.getInt("progress_streak", 0),
                sp.getInt("progress_interrupted", 0),
                sp.getInt("progress_last_score", 0),
                sp.getInt("progress_last_total", 0),
                sp.getInt("progress_last_percent", 0),
                sp.getString("progress_last_difficulty", "Not attempted"),
                sp.getString("progress_last_time", "No tests completed yet")
        );
    }

    private static int computeStreak(String lastDay, int oldStreak) {
        String today = today();
        if (today.equals(lastDay)) return Math.max(1, oldStreak);
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd", Locale.US);
            long diff = f.parse(today).getTime() - f.parse(lastDay).getTime();
            long days = diff / (24L * 60L * 60L * 1000L);
            return days == 1 ? oldStreak + 1 : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private static String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
    }

    public static final class Snapshot {
        public final int tests;
        public final int questions;
        public final int correct;
        public final int averagePercent;
        public final int bestPercent;
        public final int streak;
        public final int interrupted;
        public final int lastScore;
        public final int lastTotal;
        public final int lastPercent;
        public final String lastDifficulty;
        public final String lastTime;

        Snapshot(int tests, int questions, int correct, int averagePercent, int bestPercent, int streak, int interrupted, int lastScore, int lastTotal, int lastPercent, String lastDifficulty, String lastTime) {
            this.tests = tests;
            this.questions = questions;
            this.correct = correct;
            this.averagePercent = averagePercent;
            this.bestPercent = bestPercent;
            this.streak = streak;
            this.interrupted = interrupted;
            this.lastScore = lastScore;
            this.lastTotal = lastTotal;
            this.lastPercent = lastPercent;
            this.lastDifficulty = lastDifficulty;
            this.lastTime = lastTime;
        }
    }
}
