package com.proj.safe_chat.firebase_noti;

import com.google.gson.annotations.SerializedName;

//האובייקט הכללי אותו שולחים כהתראה מחזיק מידע וTOKEN של המשתמש שצריך לקבל את ההתראה
public class RootModel {

    @SerializedName("to") //  "to" changed to token
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

    @SerializedName("data")
    private Data data;

    //בנאי המחלקה
    public RootModel(String token, NotificationModel notification, Data data) {
        this.token = token;
        this.notification = notification;
        this.data = data;
    }

    //פעולות GET SET סטנדרטיות
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }
}
