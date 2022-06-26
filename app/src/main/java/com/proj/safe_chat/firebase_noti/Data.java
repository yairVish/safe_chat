package com.proj.safe_chat.firebase_noti;

//מחזיק את המידע שנשלח כהראה לפיירבייס
public class Data {
    private String type;
    private String fromId;
    private String to;
    private String fromName;

    //בנאי המחלקה
    public Data(String type, String fromId, String to,String fromName) {
        this.type = type;
        this.fromId = fromId;
        this.to = to;
        this.fromName=fromName;
    }
    //פעולות GET SET סטנדרטיות
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getfromId() {
        return fromId;
    }

    public void setfromId(String fromId) {
        this.fromId = fromId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
