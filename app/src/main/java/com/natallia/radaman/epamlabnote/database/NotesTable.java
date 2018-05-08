package com.natallia.radaman.epamlabnote.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.natallia.radaman.epamlabnote.Note;

public class NotesTable {
    public static final String TABLE_NAME = "notes";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";

    static Note add(String title, SQLiteDatabase db) {
        return add(title, "", db);
    }

    /**
     * Add new note into the table and return it
     *
     * @param title
     * @param text
     * @param db    writable database
     * @return Newly created bookmark or null if task fails
     */

    static Note add(String title, String text, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, title);
        values.put(KEY_TEXT, text);

        long id = db.insert(TABLE_NAME, null, values);

        if (id == -1) return null;

        return new Note(id, title, text);
    }

    static boolean updateItem(long id, String title, String text, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        if (title != null)
            values.put(KEY_TITLE, title);
        if (text != null)
            values.put(KEY_TEXT, text);

        if (values.size() == 0)
            return false;

        return 0 != db.update(TABLE_NAME, values, "id = " + id, null);
    }

    static int delete(Long[] ids, SQLiteDatabase db) {
        String constraint;
        if (ids == null) {
            constraint = "1";
        } else {
            StringBuilder sb = new StringBuilder();
            for (long id : ids) {
                if (sb.length() != 0) {
                    sb.append(',');
                }
                sb.append(id);
            }
            constraint = "id IN (" + sb.toString() + ")";
        }
        return db.delete(NotesTable.TABLE_NAME, constraint, null);
    }
}
