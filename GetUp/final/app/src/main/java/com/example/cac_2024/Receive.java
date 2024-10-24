package com.example.cac_2024;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

public class Receive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = context.getSharedPreferences("timePref", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        Random rand = new Random();
        int startHr = sharedPref.getInt("startHour", 0);
        int startMin = sharedPref.getInt("startMin", 0);
        int startTime = startHr * 60 + startMin;
        int endHr = sharedPref.getInt("endHour", 0);
        int endMin = sharedPref.getInt("endMin", 0);
        int endTime = endHr * 60 + endMin;
        int randTime = rand.nextInt((endTime - startTime) + 1) + startTime;
        int randHour = randTime / 60;
        int randMin = randTime % 60;
        editor.putInt("notifHour", randHour);
        editor.putInt("notifMin", randMin);
        editor.apply();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, randHour);
        calendar.set(Calendar.MINUTE, randMin);
        calendar.set(Calendar.MINUTE, randMin);
        calendar.set(Calendar.SECOND, 0);

        Intent myIntent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        }
//        Toast.makeText(context.getApplicationContext(), "Alarm for " + calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();

    }
}
