package com.natallia.radaman.epamlabnote.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.natallia.radaman.epamlabnote.Note;

public class NotesAddTask extends AsyncTask<String, Integer, Note> {
    private NotesDBOpenHelper dbOpenHelper;
    private OnNoteAddCallback callback;

    public interface OnNoteAddCallback {
        void onAdd(Note note);
    }

    public NotesAddTask(NotesDBOpenHelper dbOpenHelper, OnNoteAddCallback callback) {
        this.dbOpenHelper = dbOpenHelper;
        this.callback = callback;
    }

    @Override
    protected Note doInBackground(String... strings) {
        Note note;
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        if (strings.length > 1)
            note = NotesTable.add(strings[0], strings[1], database);
        else
            note = NotesTable.add(strings[0], database);
        database.close();
        return note;
    }

    @Override
    protected void onPostExecute(Note note) {
        super.onPostExecute(note);
        if (callback != null)
            callback.onAdd(note);
    }
}
