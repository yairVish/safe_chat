package com.proj.safe_chat.roomsql;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
    }

    public void insertUser(NoteUser note) {
        new InsertNoteUserAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotesUser() {
        new DeleteAllNotesUserAsyncTask(noteDao).execute();
    }

    public NoteUser getNoteUser() {
        return noteDao.getNoteUser();
    }

    public void insertMessage(Message note) {
        new InsertMessageAsyncTask(noteDao).execute(note);
    }

    public void updateMessage(Message message){
        new UpdateMessageAsyncTask(noteDao).execute(message);
    }

    public LiveData<List<Message>> getAllMessages(String my_uid, String o_uid){
        return noteDao.getAllMessages(my_uid, o_uid);
    }

    public LiveData<Message> getLastMessage(String my_uid, String o_uid){
        return noteDao.getLastMessage(my_uid, o_uid);
    }
    public LiveData<Message> getLastMessageOfAll(String query){return noteDao.getLastMessageOfAll(query);}

    public LiveData<List<Message>> getAllMessagesNotShow(String query) {
        return noteDao.getAllMessagesNotShow(query);
    }



    private static class InsertNoteUserAsyncTask extends AsyncTask<NoteUser, Void, Void> {
        private NoteDao noteDao;

        private InsertNoteUserAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(NoteUser... notes) {
            noteDao.insertUser(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesUserAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private DeleteAllNotesUserAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotesUser();
            return null;
        }
    }

    private static class InsertMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        private NoteDao noteDao;
        private InsertMessageAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Message... notes) {
            noteDao.insertMessage(notes[0]);
            return null;
        }
    }
    private static class UpdateMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        private NoteDao noteDao;
        private UpdateMessageAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Message... notes) {
            noteDao.updateMessage(notes[0]);
            return null;
        }
    }
}
