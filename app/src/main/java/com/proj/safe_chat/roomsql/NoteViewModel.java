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

    public LiveData<List<Message>> getAllMessages(String query){
        return repository.getAllMessages(query);
    }

    public LiveData<Message> getLastMessage(String query){
        return repository.getLastMessage(query);
    }
    public LiveData<Message> getLastMessageOfAll(){
        return repository.getLastMessageOfAll();
    }
    public LiveData<List<Message>> getAllMessagesNotShow(String query){
        return repository.getAllMessagesNotShow(query);
    }
}
