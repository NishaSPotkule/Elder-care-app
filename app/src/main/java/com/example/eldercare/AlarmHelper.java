package com.example.eldercare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmHelper {

    public static void setDailyAlarm(Context context, String name, int hour, int minute) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("name", name);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);


        int requestCode = name.hashCode();

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                pi
        );
    }
}