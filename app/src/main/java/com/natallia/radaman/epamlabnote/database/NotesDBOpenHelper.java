package com.natallia.radaman.epamlabnote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDBOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "main";

    public NotesDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NotesTable.TABLE_NAME+" ("
                + NotesTable.KEY_ID + " INTEGER PRIMARY KEY,"
                + NotesTable.KEY_TITLE + " TEXT,"
                + NotesTable.KEY_TEXT + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
