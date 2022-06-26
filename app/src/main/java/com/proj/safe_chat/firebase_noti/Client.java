package com.proj.safe_chat.firebase_noti;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//להגדרת הretrofit - ספרייה לשליחת בקשות HTTP
public class Client {
    public static Retrofit retrofit=null;
    public static Retrofit getClient(String url){
        Log.d("Client", "url: "+url);
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.d("Client", "retrofit: "+retrofit);
        }
        return retrofit;
    }
}
