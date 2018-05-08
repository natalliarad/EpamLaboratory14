package com.natallia.radaman.epamlabnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.natallia.radaman.epamlabnote.database.NotesAddTask;
import com.natallia.radaman.epamlabnote.database.NotesDBOpenHelper;
import com.natallia.radaman.epamlabnote.database.NotesDeleteByIdTask;
import com.natallia.radaman.epamlabnote.database.NotesTable;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements AbsListView.OnItemClickListener {
    private NotesDBOpenHelper dbOpenHelper;

    private ListView listView;
    private TextView emptyListView;
    private SimpleCursorAdapter notesAdapter;
    private AlertDialog createItemDialog;

    private DialogInterface.OnClickListener onNoteAddClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog dialogNew = (AlertDialog) dialog;
            EditText textView = dialogNew.findViewById(R.id.note_add_text_view);
            String title = textView.getText().toString();
            if (!title.isEmpty()) {
                new NotesAddTask(dbOpenHelper, new NotesAddTask.OnNoteAddCallback() {
                    @Override
                    public void onAdd(Note note) {
                        notesAdapter.changeCursor(getNotesCursor());
                    }
                }).execute(title);
            }
            dialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        emptyListView = findViewById(R.id.empty_list_view);
        dbOpenHelper = new NotesDBOpenHelper(this);

        initCreateItemDialog();
        initListViewAdapter();

        setListViewBehaviour();
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.notes_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        private HashSet<Long> selected = new HashSet<>();

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked) {
                selected.add(id);
            } else {
                selected.remove(id);
            }
            mode.invalidate();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.note_action_mode, menu);
            selected.clear();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.note_edit).setVisible(selected.size() == 1);
            menu.findItem(R.id.note_delete).setVisible(!selected.isEmpty());
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.note_edit: {
                    Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                    intent.putExtra(NoteEditActivity.INTENT_ID_KEY, selected.iterator().next());
                    startActivity(intent);
                    mode.finish();
                    return true;
                }
                case R.id.note_delete: {
                    Long[] ids = new Long[selected.size()];
                    int idx = 0;
                    for (long id : selected) {
                        ids[idx++] = id;
                    }

                    new NotesDeleteByIdTask(dbOpenHelper, new NotesDeleteByIdTask
                            .OnNoteDeleteCallback() {
                        @Override
                        public void onDelete(int count, Long[] params) {
                            notesAdapter.changeCursor(getNotesCursor());
                        }
                    }).execute(ids);
                    mode.finish();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteViewsActivity.class);
        intent.putExtra(NoteViewsActivity.INTENT_ID_KEY, id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.changeCursor(getNotesCursor());
    }

    /**
     * Request cursor with id, title fields from Notes table and _id for
     * compatibility with SimpleCursorAdapter
     *
     * @return Cursor
     */
    private Cursor getNotesCursor() {
        return dbOpenHelper.getReadableDatabase().query(
                NotesTable.TABLE_NAME,
                new String[]{NotesTable.KEY_ID + " _id", NotesTable.KEY_ID, NotesTable.KEY_TITLE},
                null, null, null, null, NotesTable.KEY_ID
        );
    }

    private void initCreateItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.note_dialog_title);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.note_dialog_positive, onNoteAddClickListener);

        View dialogView = View.inflate(builder.getContext(), R.layout.notes_add_layout, null);
        builder.setView(dialogView);

        createItemDialog = builder.create();
        createItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                AlertDialog dialogNew = (AlertDialog) dialogInterface;
                EditText textView = dialogNew.findViewById(R.id.note_add_text_view);
                textView.setText("");
            }
        });
    }

    private void initListViewAdapter() {
        notesAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_1,
                null,
                new String[]{NotesTable.KEY_TITLE},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
    }

    private void setListViewBehaviour() {
        listView.setAdapter(notesAdapter);
        listView.setEmptyView(emptyListView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_add: {
                createItemDialog.show();
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
