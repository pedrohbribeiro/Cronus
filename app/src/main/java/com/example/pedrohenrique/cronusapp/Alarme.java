package com.example.pedrohenrique.cronusapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class Alarme extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(context.getSharedPreferences("configuracoes",0).getBoolean("notificar",true)) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                setarAlarme(context);//Se o celular tiver sido ligado agora seta os alarmes do dia
            } else {
                if (intent.getStringExtra("tipo").equals("resetDiario")) {
                    setarAlarme(context);//Se tiver passado de meia noite, seta os alarmes do dia
                } else if (intent.getStringExtra("tipo").equals("notificação")) {
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Cria o canal de notificação
                        CharSequence name = "Cronus";
                        String description = "Canal de notificação para o app Cronus";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel("CronusNotification", name, importance);
                        channel.setDescription(description);
                        NotificationManager manager = context.getSystemService(NotificationManager.class);
                        manager.createNotificationChannel(channel);

                        //Notificação
                        Notification.Builder mBuilder = new Notification.Builder(context, "CronusNotification")
                                .setSmallIcon(R.drawable.ic_menu_send)
                                .setContentTitle("Cronus")
                                .setContentText("Está no horário da atividade " + intent.getStringExtra("nome"));
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(intent.getIntExtra("id", 1), mBuilder.build());
                    } else {
                        //Notificação
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                                .setSmallIcon(R.drawable.ic_menu_send)
                                .setContentTitle("Cronus")
                                .setContentText("Está no horário da atividade " + intent.getStringExtra("nome"))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setDefaults(Notification.DEFAULT_ALL);
                        NotificationManagerCompat.from(context).notify(intent.getIntExtra("id", 1), mBuilder.build());
                    }
                    //wl.release();
                }
            }
        }
    }

    public void setarAlarme(Context context) {
        if(context.getSharedPreferences("configuracoes",0).getBoolean("notificar",true)) {
            ArrayList<Atividade> atividades = new Atividade(context).buscarAtividades(Calendar.getInstance(), 1);
            for (Atividade atividade : atividades) {
                Calendar setcalendar = Calendar.getInstance();
                setcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(String.format("%04d", atividade.getHorario()).substring(0, 2)));
                setcalendar.set(Calendar.MINUTE, Integer.parseInt(String.format("%04d", atividade.getHorario()).substring(2)));
                setcalendar.set(Calendar.SECOND, 0);
                if (setcalendar.after(Calendar.getInstance())) {//Verifica se a notificação a ser enviada é depois do horário atual
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context, Alarme.class);
                    i.putExtra("tipo", "notificação");
                    i.putExtra("nome", atividade.getNome());
                    i.putExtra("id", atividade.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, atividade.getId(), i, 0);
                    manager.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                }
            }
            //Seta o "alarme" para fazer o reset diário das notificações
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(context, Alarme.class);
            intent.putExtra("tipo", "resetDiario");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public void cancelarAlarme(Context context, Atividade atividade) {//Cancela alarmes já existentes
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarme.class);
        i.putExtra("tipo", "notificação");
        i.putExtra("nome", atividade.getNome());
        i.putExtra("id", atividade.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, atividade.getId(), i, 0);
        manager.cancel(pendingIntent);
    }
}
