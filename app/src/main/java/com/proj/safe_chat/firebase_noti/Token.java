package com.proj.safe_chat.firebase_noti;

import android.util.Log;

public class Token {
    private String token;
    public Token(String token){
        this.token=token;
        Log.d("Token", "getToken1: "+token);
    }
    public Token(){

    }
    public void setToken(String token){
        this.token=token;
    }
    public String getToken(){
        Log.d("Token", "getToken2: "+token);
        return token;
    }
}
