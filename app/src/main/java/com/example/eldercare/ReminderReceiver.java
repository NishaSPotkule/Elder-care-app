package com.example.eldercare;

import android.app.*;
import android.content.*;
import android.os.*;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String medName = intent.getStringExtra("name");
        int hour = intent.getIntExtra("hour", 8);
        int minute = intent.getIntExtra("minute", 0);

        AppDatabase db = AppDatabase.getInstance(context);
        Medicine med = db.medicineDao().getByName(medName);

        if (med != null) {
            med.taken = false;
            med.alarmTime = System.currentTimeMillis();
            db.medicineDao().update(med);
        }

        showNotification(context, medName);

        startMissedCheck(context, medName);


        AlarmHelper.setDailyAlarm(context, medName, hour, minute);
    }


    private void startMissedCheck(Context context, String name) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            AppDatabase db = AppDatabase.getInstance(context);
            Medicine med = db.medicineDao().getByName(name);

            if (med != null && !med.taken) {
                showMissedNotification(context, name);
            }

        }, 15 * 60 * 1000);
    }


    private void showNotification(Context context, String name) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "med_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Medicine Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );


            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 500, 500});

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("💊 Time to take medicine")
                .setContentText(name)
                .setSmallIcon(android.R.drawable.ic_dialog_info)


                .setDefaults(NotificationCompat.DEFAULT_ALL)


                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private void showMissedNotification(Context context, String name) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "med_channel")
                        .setContentTitle("⚠ Missed Medicine!")
                        .setContentText("You missed: " + name)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)


                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}