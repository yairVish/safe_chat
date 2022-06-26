package com.proj.safe_chat.firebase_noti;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

//ממשק המגדיר את מבנה הבקשה לשליחת התראה לפיירבייס
public interface APIService {
    @Headers(
            {
            "Content-Type:application/json",
            "Authorization:key=AAAAaMWMOb4:APA91bFVHlcbAPu50-EyxWaxUmx7xgnsZO4Hhrj3KW06xLdBkL_t-hVw1IOYtNbtu2fTz5B9Gza94KircioW0l9arZQS4xWJ_vQv1JAVQqg9IqCGwV5S9Ze1rzORh2xco2ZUXJj0RE6o"
            }
    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body RootModel text);
}
