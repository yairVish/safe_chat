package com.proj.safe_chat.firebase_noti;

import android.util.Log;

//מחזיק את הסטטוס קוד של תגובת הHTTP
public class MyResponse {
    public int success;
    public MyResponse(){
        Log.d("MyResponse", "success: "+success);
    }
}
