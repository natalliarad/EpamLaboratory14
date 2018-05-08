package com.natallia.radaman.epamlabnote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.natallia.radaman.epamlabnote.database.NotesDBOpenHelper;
import com.natallia.radaman.epamlabnote.database.NotesGetByIdTask;
import com.natallia.radaman.epamlabnote.database.NotesTable;
import com.natallia.radaman.epamlabnote.database.NotesUpdateTask;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteEditActivity extends AppCompatActivity {
    public static String INTENT_ID_KEY = "id";
    private NotesDBOpenHelper dbOpenHelper;

    private long id;
    private EditText titleView;
    private EditText textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Intent intent = getIntent();
        id = intent.getLongExtra(INTENT_ID_KEY, -1);
        if (id == -1) {
            finish();
            return;
        }

        dbOpenHelper = new NotesDBOpenHelper(this);

        initView();

        new NotesGetByIdTask(dbOpenHelper, new NotesGetByIdTask.OnNotessGetByIdCallback() {
            @Override
            public void onGetById(ArrayList<HashMap<String, Object>> bookmarks) {
                if (bookmarks.size() > 0) {
                    HashMap<String, Object> res = bookmarks.get(0);
                    setContent(res);
                }
            }
        }).setWithText(true).execute(id);
    }

    private void setContent(HashMap<String, Object> bookmark) {
        titleView.setText((String) bookmark.get(NotesTable.KEY_TITLE));
        textView.setText((String) bookmark.get(NotesTable.KEY_TEXT));
    }

    private void initView() {
        titleView = findViewById(R.id.details_title_view);
        textView = findViewById(R.id.details_text_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(R.string.details_save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (titleView.getText().length() != 0) {
            finishEdit();
            return true;
        }
        return false;
    }

    private void finishEdit() {
        String newTitle = titleView.getText().toString();
        String newText = textView.getText().toString();

        HashMap<String, Object> item = new HashMap<>(3);
        item.put(NotesTable.KEY_ID, id);
        item.put(NotesTable.KEY_TITLE, newTitle);
        item.put(NotesTable.KEY_TEXT, newText);

        new NotesUpdateTask(dbOpenHelper, new NotesUpdateTask.OnNotesUpdateCallback() {
            @Override
            public void onUpdate(ArrayList<HashMap<String, Object>> updates) {
                finish();
            }
        }).execute(item);
    }
}
