package com.proj.safe_chat.firebase_noti;

import android.util.Log;

//אובייקט העוטף את TOKEN על מנת לנהל את עדכון הTOKEN כמו שצריך - כרגע אין צורך כי הTOKEN מתעדכן בכל כניסה לאפליקציה
public class Token {
    private String token;
    public Token(String token){
        this.token=token;
        Log.d("Token", "getToken1: "+token);
    }
    //לעדכן את הTOKEN
    public void setToken(String token){
        this.token=token;
    }
    public String getToken(){
        Log.d("Token", "getToken2: "+token);
        return token;
    }
}
