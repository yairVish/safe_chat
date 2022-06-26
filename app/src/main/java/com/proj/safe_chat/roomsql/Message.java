package com.proj.safe_chat.roomsql;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

//אובייקט המייצג את ההודעה

@Entity(tableName = "messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String body;

    private String toId;

    private String fromId;

    private boolean show;

    private long time;

    //בנאי המחלקה
    public Message(String body, String toId, String fromId, boolean show, long time) {
        this.body = body;
        this.toId = toId;
        this.fromId = fromId;
        this.show = show;
        this.time = time;
    }

    //פעולות GET SET סטנדרטיות
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getToId() {
        return toId;
    }

    public String getFromId() {
        return fromId;
    }

    public boolean isShow() {
        return show;
    }

    public long getTime() {
        return time;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
