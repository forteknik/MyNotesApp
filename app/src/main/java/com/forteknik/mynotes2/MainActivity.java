package com.forteknik.mynotes2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.forteknik.mynotes2.adapter.NoteAdapter;
import com.forteknik.mynotes2.db.NoteHelper;
import com.forteknik.mynotes2.entity.NoteModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.forteknik.mynotes2.NoteAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadNotesCallback {

    private RecyclerView rvNote;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private static final String EXTRA_STATE = "extra_state";
    private NoteHelper noteHelper;
    private NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notes");

        }

        rvNote = findViewById(R.id.rv_note);
        rvNote.setLayoutManager(new LinearLayoutManager(this));
        rvNote.setHasFixedSize(true);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.opedDB();

        progressBar = findViewById(R.id.progresBar);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);


        noteAdapter = new NoteAdapter(this);
        rvNote.setAdapter(noteAdapter);

        if (savedInstanceState == null) {
            new LoadNotesAsync(noteHelper, this).execute();

        } else {
            ArrayList<NoteModel> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                noteAdapter.setListNote(list);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, noteAdapter.getListNotes());
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);

        }
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<NoteModel> noteModels) {
        progressBar.setVisibility(View.INVISIBLE);
        noteAdapter.setListNote(noteModels);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.closeDB();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(rvNote, message, Snackbar.LENGTH_SHORT).show();
    }


    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<NoteModel>> {
        private final WeakReference<NoteHelper> weakNoteHelper;
        private final WeakReference<LoadNotesCallback> weakCallback;


        private LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }


        @Override
        protected ArrayList<NoteModel> doInBackground(Void... voids) {

            return weakNoteHelper.get().getAllNotes();
        }


        @Override
        protected void onPostExecute(ArrayList<NoteModel> noteModels) {
            super.onPostExecute(noteModels);
            weakCallback.get().postExecute(noteModels);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {

                    NoteModel noteModel = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    noteAdapter.addItem(noteModel);
                    rvNote.smoothScrollToPosition(noteAdapter.getItemCount() - 1);
                    showSnackBarMessage("Satu item berhasil Ditambahkan");

                }
            } else if (requestCode == REQUEST_UPDATE) {
                if (resultCode == NoteAddUpdateActivity.RESULT_UPDATE) {

                    NoteModel noteModel = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                    noteAdapter.updateItem(position, noteModel);

                    rvNote.smoothScrollToPosition(position);
                    showSnackBarMessage("satu item telah diubah");

                } else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                    noteAdapter.deleteItem(position);
                    showSnackBarMessage("satu item telah dihapus");
                }
            }


        }
    }
}

