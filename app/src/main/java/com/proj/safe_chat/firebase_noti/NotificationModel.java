package com.proj.safe_chat.firebase_noti;

//מחזיק את המידע כל ההתראה - כרגע אין שימוש כל המידע שצריך נמצא בDATA
public class NotificationModel {

    private String title;
    private String body;
    //בנאי המחלקה
    public NotificationModel(String title, String body) {
        this.title = title;
        this.body = body;
    }
    //פעולות GET SET סטנדרטיות
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
