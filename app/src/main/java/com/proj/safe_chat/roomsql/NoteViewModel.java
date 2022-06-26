package com.proj.safe_chat.roomsql;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
    }

    //פעולות גישור לשאילתות בDao - המחלקה אחראית לגשר בין הUI לDATA
    public void insertUser(NoteUser note) {
        repository.insertUser(note);
    }
    public void deleteAllNotesUser() {
        repository.deleteAllNotesUser();
    }
    public NoteUser getNoteUser() {
        return repository.getNoteUser();
    }

    public void insertMessage(Message note) {
        repository.insertMessage(note);
    }

    public void updateMessage(Message note) {
        repository.updateMessage(note);
    }

    public LiveData<List<Message>> getAllMessages(String my_uid, String o_uid){
        return repository.getAllMessages(my_uid, o_uid);
    }

    public LiveData<Message> getLastMessage(String my_uid, String o_uid){
        return repository.getLastMessage(my_uid, o_uid);
    }
    public LiveData<Message> getLastMessageOfAll(String query){
        return repository.getLastMessageOfAll(query);
    }
    public LiveData<List<Message>> getAllMessagesNotShow(String query){
        return repository.getAllMessagesNotShow(query);
    }
}
