package com.proj.safe_chat.firebase_noti;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.proj.safe_chat.R;
import com.proj.safe_chat.StartActivity;

import java.util.Random;


public class MyFirebaseMessaginig extends FirebaseMessagingService {
    public static boolean activeChat=false;
    public static String idUser="";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage);
    }
    private void showNotification( RemoteMessage remoteMessage){
        Uri defultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int idNote=getNotificationId();
        Intent intent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,idNote,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.safechat)
                .setContentTitle("New Message!")
                .setContentText("new message from "+remoteMessage.getData().get("fromName"))
                .setAutoCancel(true)
                .setSound(defultSound)
                .setContentIntent(pendingIntent);
        Log.d("onMessageReceived", "builder: "+builder);
        NotificationManager noti =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId("com.proj.safe_chat");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    "com.proj.safe_chat",
                    "safe_chat",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if(noti != null){
                noti.createNotificationChannel(channel);
            }
        }
        Log.d("onMessageReceived", "noti: "+noti);
        if(!activeChat||!idUser.equals(remoteMessage.getData().get("fromId"))) {
            Log.d("onMessageReceived", "noti22: "+noti);
            noti.notify(idNote, builder.build());
        }
    }
    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }
}
