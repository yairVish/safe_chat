package com.proj.safe_chat.roomsql;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//אובייקט המייצג את המשתמש שהתחבר לאחרונה

@Entity(tableName = "user")
public class NoteUser {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String email;

    private String password;

    //בנאי המחלקה
    public NoteUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //פעולות GET SET סטנדרטיות
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
