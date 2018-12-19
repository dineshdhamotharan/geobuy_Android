package apps.codette.geobuy.service;

import com.google.firebase.messaging.FirebaseMessagingService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import apps.codette.geobuy.NotificationActivity;
import apps.codette.geobuy.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService  {

    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;

    @Override
    public void onMessageReceived(RemoteMessage message) {

        sendMyNotification(message.getNotification());
      //  toast(message.getNotification().getBody());
       // toast(message.getNotification().getTitle());
    }


    private void toast(String s) {
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("WrongConstant")
    private void sendMyNotification(RemoteMessage.Notification notification) {
        Log.i("msg", notification.getBody());
        Intent myIntent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        myNotification = new NotificationCompat.Builder(this)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setTicker(notification.getTitle())
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo)
                .build();

        notificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);



    }
}
