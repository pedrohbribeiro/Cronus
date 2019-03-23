package com.example.pedrohenrique.cronusapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationService extends JobIntentService {



    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        if (!bundle.isEmpty()) {
            if (intent.getStringExtra("tipo").equals("resetDiario")) {
                ArrayList<Atividade> atividades = new Atividade(this).buscarAtividades(Calendar.getInstance(), 1);
                for (Atividade atividade : atividades) {
                    Calendar setcalendar = Calendar.getInstance();
                    setcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(String.format("%04d", atividade.getHorario()).substring(0, 2)));
                    setcalendar.set(Calendar.MINUTE, Integer.parseInt(String.format("%04d", atividade.getHorario()).substring(2)));
                    setcalendar.set(Calendar.SECOND, 0);
                    if (setcalendar.after(Calendar.getInstance())) {
                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent i = new Intent(this, Alarme.class);
                        i.putExtra("tipo", "notificação");
                        i.putExtra("nome", atividade.getNome());
                        i.putExtra("id", atividade.getId());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, atividade.getId(), i, 0);
                        manager.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                    }
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Intent i = new Intent(this, Alarme.class);
                i.putExtra("tipo", "resetDiario");
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            } else if (intent.getStringExtra("tipo").equals("notificação")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Notification.Builder mBuilder = new Notification.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_menu_send)
                            .setContentTitle("Cronus")
                            .setContentText("Está no horário da atividade " + intent.getStringExtra("nome"));
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(intent.getIntExtra("id", 1), mBuilder.build());
                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                            .setSmallIcon(R.drawable.ic_menu_send)
                            .setContentTitle("Cronus")
                            .setContentText("Está no horário da atividade " + intent.getStringExtra("nome"))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_ALL);
                    NotificationManagerCompat.from(this).notify(intent.getIntExtra("id", 1), mBuilder.build());
                }
            }
        }
    }
}
