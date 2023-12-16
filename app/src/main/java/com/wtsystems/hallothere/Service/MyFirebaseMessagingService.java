package com.wtsystems.hallothere.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wtsystems.hallothere.MainActivity;
import com.wtsystems.hallothere.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage notification) {
        if(notification.getNotification() != null){
            String title = notification.getNotification().getTitle();
            String body = notification.getNotification().getBody();

            SendNotification(title, body);
        }
    }

    private void SendNotification(String titulo, String corpo){

        //Configurações para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Criar a notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setSound(uriSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //Recuperar Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //Verificar compatibilidade a partir do Android Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel  = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //Enviar notificação
        notificationManager.notify(0, notificacao.build());

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }
}
