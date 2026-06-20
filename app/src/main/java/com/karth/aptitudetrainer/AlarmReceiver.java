package com.karth.aptitudetrainer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        Scheduler.createChannel(context);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AptitudeTrainer:alarm");
        wl.acquire(60_000L);
        try {
            Intent test = new Intent(context, TestActivity.class);
            test.putExtra("scheduled", true);
            test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent full = PendingIntent.getActivity(context, 5001, test, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, Scheduler.CHANNEL_ID) : new Notification.Builder(context);
            b.setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Aptitude test time")
                    .setContentText("Your scheduled aptitude test is starting now.")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setContentIntent(full)
                    .setFullScreenIntent(full, true);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(Scheduler.NOTIFICATION_ID, b.build());
            context.startActivity(test);
        } finally {
            Scheduler.scheduleNext(context);
            if (wl.isHeld()) wl.release();
        }
    }
}
