package com.proj.safe_chat.firebase_noti;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification {
    APIService apiService;
    private Data data;
    private String token;
    //בנאי המחלקה
    public SendNotification(String token,Data data){
        this.token=token;
        this.data=data;
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }
    //פונקציה השולחת את ההתראה
    public void sendNotification(){
        RootModel rootModel
                = new RootModel(token,null,data);
        apiService.sendNotification(rootModel)
                .enqueue(new Callback<MyResponse>(){
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response){
                        Log.d("TAG", "Response: "+response);
                        Log.d("TAG", "token: "+token);
                        if(response.code()==200){
                            if(response.body().success!=1){
                                Log.d("TAG", "Failed!: ");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t){
                        Log.d("TAG", "onFailure: ");
                    }
                });
    }
}
