package com.natallia.radaman.epamlabnote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesGetAllTask extends AsyncTask<Boolean, Integer, ArrayList<HashMap<String, Object>>> {
    NotesDBOpenHelper dbOpenHelper;
    OnNotesGetAllCallback callback;

    public NotesGetAllTask(NotesDBOpenHelper dbOpenHelper, OnNotesGetAllCallback callback) {
        this.dbOpenHelper = dbOpenHelper;
        this.callback = callback;
    }

    public interface OnNotesGetAllCallback {
        void onGetAll(ArrayList<HashMap<String, Object>> bookmarks);
    }

    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(Boolean... withText) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String[] columns = withText[0]
                ? new String[]{"id", "title", "text"}
                : new String[]{"id", "title"};

        Cursor cursor = db.query(NotesTable.TABLE_NAME, columns, null, null,
                null, null, NotesTable.KEY_ID);
        ArrayList<HashMap<String, Object>> results = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> row = new HashMap<>(columns.length + 1);
                for (String column : columns) {
                    int columnIdx = cursor.getColumnIndex(column);
                    if (cursor.getType(columnIdx) == Cursor.FIELD_TYPE_INTEGER) {
                        row.put(column, cursor.getInt(columnIdx));
                    } else {
                        row.put(column, cursor.getString(columnIdx));
                    }
                }

                results.add(row);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, Object>> hashMaps) {
        super.onPostExecute(hashMaps);
        if (callback != null) callback.onGetAll(hashMaps);
    }
}
