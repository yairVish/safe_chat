package com.proj.safe_chat.firebase_noti;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    //נקרא כאשר מתעדכן TOKEN
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("MyFirebaseIdService", "s: "+s);
            updateToken(s);
    }
    //פונקציה הנקראית לתוך onNewToken
    private void updateToken(String refreshToken){
        Token token=new Token(refreshToken);
        Log.d("MyFirebaseIdService", "token: "+token);
        Log.d("MyFirebaseIdService", "token.getToken(): "+token.getToken());
    }
}
