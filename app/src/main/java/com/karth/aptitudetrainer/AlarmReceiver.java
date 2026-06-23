package com.karth.aptitudetrainer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        Scheduler.createChannel(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        int wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            wakeFlags |= PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        PowerManager.WakeLock wl = pm == null ? null : pm.newWakeLock(wakeFlags, "AptitudeTrainer:alarm");
        if (wl != null) wl.acquire(90_000L);
        try {
            Intent test = new Intent(context, TestActivity.class);
            test.putExtra("scheduled", true);
            test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            PendingIntent full = PendingIntent.getActivity(context, 5001, test, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, Scheduler.CHANNEL_ID) : new Notification.Builder(context);
            b.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Aptitude test time")
                    .setContentText("Your scheduled test is starting now. Tap if it is not already open.")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setVibrate(new long[]{0, 350, 180, 350})
                    .setLights(Color.rgb(0, 113, 227), 800, 1200)
                    .setAutoCancel(true)
                    .setContentIntent(full)
                    .setFullScreenIntent(full, true);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.notify(Scheduler.NOTIFICATION_ID, b.build());
            try {
                context.startActivity(test);
            } catch (Exception ignored) {}
        } finally {
            Scheduler.scheduleNext(context);
            if (wl != null && wl.isHeld()) wl.release();
        }
    }
}
