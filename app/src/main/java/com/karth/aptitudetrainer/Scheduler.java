package com.karth.aptitudetrainer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;

public final class Scheduler {
    public static final String PREFS = "secure_aptitude_prefs";
    public static final String CHANNEL_ID = "daily_test_channel";
    public static final int ALARM_REQUEST = 2001;
    public static final int NOTIFICATION_ID = 301;

    private Scheduler() {}

    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Daily aptitude test", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Opens the scheduled aptitude test at your selected time.");
            ch.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(ch);
        }
    }

    public static void saveSchedule(Context ctx, int hour, int minute, String difficulty, String company, int count) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit()
                .putInt("hour", hour)
                .putInt("minute", minute)
                .putString("difficulty", difficulty)
                .putString("company", QuestionBank.normalizeCompany(company))
                .putInt("count", count)
                .putBoolean("scheduled", true)
                .apply();
        scheduleNext(ctx);
    }

    public static void scheduleNext(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!sp.getBoolean("scheduled", false)) return;
        int hour = sp.getInt("hour", 7);
        int minute = sp.getInt("minute", 0);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.setAction("com.karth.aptitudetrainer.START_TEST");
        PendingIntent pi = PendingIntent.getBroadcast(ctx, ALARM_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        }
    }

    public static void cancelSchedule(Context ctx) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.setAction("com.karth.aptitudetrainer.START_TEST");
        PendingIntent pi = PendingIntent.getBroadcast(ctx, ALARM_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pi);
        pi.cancel();
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(NOTIFICATION_ID);
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("scheduled", false)
                .apply();
    }

    public static Intent exactAlarmSettingsIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        return null;
    }

    public static String scheduleSummary(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!sp.getBoolean("scheduled", false)) return "No daily test scheduled yet.";
        return String.format("Daily test: %02d:%02d • %s • %s • %d questions",
                sp.getInt("hour", 7),
                sp.getInt("minute", 0),
                sp.getString("difficulty", "Easy"),
                sp.getString("company", QuestionBank.ALL_COMPANIES),
                sp.getInt("count", 5));
    }
}
