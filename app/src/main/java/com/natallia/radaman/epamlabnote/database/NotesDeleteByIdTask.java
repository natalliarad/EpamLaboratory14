package com.natallia.radaman.epamlabnote.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class NotesDeleteByIdTask extends AsyncTask<Long, Integer, Integer> {
    private NotesDBOpenHelper dbOpenHelper;
    private OnNoteDeleteCallback callback;

    private Long[] params;

    public NotesDeleteByIdTask(NotesDBOpenHelper dbOpenHelper, OnNoteDeleteCallback callback) {
        this.dbOpenHelper = dbOpenHelper;
        this.callback = callback;
    }

    public interface OnNoteDeleteCallback {
        void onDelete(int count, Long[] params);
    }

    @Override
    protected Integer doInBackground(Long... ids) {
        params = ids;
        if (ids.length == 0) return -1;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int count = NotesTable.delete(ids, db);
        db.close();
        return count;
    }

    @Override
    protected void onPostExecute(Integer count) {
        super.onPostExecute(count);
        if (callback != null)
            callback.onDelete(count, params);
    }
}
