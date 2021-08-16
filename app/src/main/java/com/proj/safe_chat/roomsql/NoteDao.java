package com.proj.safe_chat.roomsql;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insertUser(NoteUser noteUser);

    @Query("DELETE FROM user")
    void deleteAllNotesUser();

    @Query("SELECT * FROM user LIMIT 1")
    NoteUser getNoteUser();

    /////////////////////////////////////////////////////////////////////////////////message

    @Insert
    void insertMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Query("SELECT * FROM messages WHERE toId LIKE :query AND show LIKE 0")
    LiveData<List<Message>> getAllMessagesNotShow(String query);

    @Query("SELECT * FROM messages WHERE toId LIKE :query")
    LiveData<List<Message>> getAllMessages(String query);

    @Query("SELECT * FROM messages WHERE toId LIKE :query ORDER BY id DESC LIMIT 1")
    LiveData<Message> getLastMessage(String query);

    @Query("SELECT * FROM messages ORDER BY id DESC LIMIT 1")
    LiveData<Message> getLastMessageOfAll();
}
