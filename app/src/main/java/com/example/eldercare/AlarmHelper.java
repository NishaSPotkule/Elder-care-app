package com.example.eldercare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmHelper {

    public static void setDailyAlarm(Context context, int medId, int hour, int minute) {

        Toast.makeText(context, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("id", medId);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);

        int requestCode = (medId + "" + hour + minute).hashCode();

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {

                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pi
                    );

                } else {
                    // Fallback if permission not granted
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pi
                    );
                }

            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pi
                );
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}