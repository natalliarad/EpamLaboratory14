package com.natallia.radaman.epamlabnote.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesUpdateTask extends
        AsyncTask<HashMap<String, Object>, Integer, ArrayList<HashMap<String, Object>>> {
    NotesDBOpenHelper dbOpenHelper;
    OnNotesUpdateCallback callback;

    public NotesUpdateTask(NotesDBOpenHelper dbOpenHelper, OnNotesUpdateCallback callback) {
        this.dbOpenHelper = dbOpenHelper;
        this.callback = callback;
    }

    public interface OnNotesUpdateCallback {
        void onUpdate(ArrayList<HashMap<String, Object>> updates);
    }

    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(HashMap<String, Object>... params) {
        ArrayList<HashMap<String, Object>> updates = new ArrayList<>(params.length);

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        for (HashMap<String, Object> item : params) {
            if (!item.containsKey(NotesTable.KEY_ID))
                continue;
            long id = (long) item.get(NotesTable.KEY_ID);
            String title = (String) item.get(NotesTable.KEY_TITLE);
            String text = (String) item.get(NotesTable.KEY_TEXT);
            if (NotesTable.updateItem(id, title, text, db)) {
                updates.add(item);
            }
        }
        return updates;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, Object>> hashMaps) {
        super.onPostExecute(hashMaps);
        if (callback != null) callback.onUpdate(hashMaps);
    }
}
