package com.natallia.radaman.epamlabnote;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.natallia.radaman.epamlabnote.database.NotesDBOpenHelper;
import com.natallia.radaman.epamlabnote.database.NotesGetByIdTask;
import com.natallia.radaman.epamlabnote.database.NotesTable;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteViewsActivity extends AppCompatActivity {
    public static final String INTENT_ID_KEY = "id";
    private NotesDBOpenHelper dbOpenHelper;
    private long id;

    private TextView titleView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_views);

        Intent intent = getIntent();
        id = intent.getLongExtra(INTENT_ID_KEY, -1);
        if (id == -1){
            finish();
            return;
        }
        dbOpenHelper = new NotesDBOpenHelper(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NotesGetByIdTask(dbOpenHelper, new NotesGetByIdTask.OnNotessGetByIdCallback() {
            @Override
            public void onGetById(ArrayList<HashMap<String, Object>> bookmarks) {
                if (bookmarks.size() > 0){
                    HashMap<String, Object> res = bookmarks.get(0);
                    setContent(res);
                }
            }
        }).setWithText(true).execute(id);
    }

    private void initView(){
        titleView = findViewById(R.id.title);
        textView = findViewById(R.id.text);

        if (getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setContent(HashMap<String, Object> data){
        titleView.setText((String) data.get(NotesTable.KEY_TITLE));
        textView.setText((String) data.get(NotesTable.KEY_TEXT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(R.string.edit);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra(NoteEditActivity.INTENT_ID_KEY, id);
        item.setIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
