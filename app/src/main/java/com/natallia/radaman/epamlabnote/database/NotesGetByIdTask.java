package com.natallia.radaman.epamlabnote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

public class NotesGetByIdTask extends AsyncTask<Long, Integer, ArrayList<HashMap<String, Object>>> {
    NotesDBOpenHelper dbOpenHelper;
    OnNotessGetByIdCallback callback;
    private boolean withText = false;

    public interface OnNotessGetByIdCallback {
        void onGetById(ArrayList<HashMap<String, Object>> bookmarks);
    }

    public NotesGetByIdTask(NotesDBOpenHelper dbOpenHelper, OnNotessGetByIdCallback callback) {
        this.dbOpenHelper = dbOpenHelper;
        this.callback = callback;
    }

    public NotesGetByIdTask setWithText(boolean withText) {
        this.withText = withText;
        return this;
    }

    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(Long... ids) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String[] columns = withText
                ? new String[]{"id", "title", "text"}
                : new String[]{"id", "title"};

        String constraint;
        StringBuilder sb = new StringBuilder();
        for (long id : ids) {
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append(id);
        }
        constraint = "id IN (" + sb.toString() + ")";

        Cursor cursor = db.query(NotesTable.TABLE_NAME, columns, constraint, null,
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
        if (callback != null) callback.onGetById(hashMaps);
    }
}
