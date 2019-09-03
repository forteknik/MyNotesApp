package com.forteknik.mynotes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.forteknik.mynotes2.db.NoteHelper;
import com.forteknik.mynotes2.entity.NoteModel;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_NOTE = "extra_note";

    private boolean isEdit = false;
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;

    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 202;

    public static final int RESULT_DELETE = 301;

    private NoteModel noteModel;
    private int position;
    private NoteHelper noteHelper;

    private EditText edTitle, edDescription;
    private Button btnSubmit;

    private final int ALERT_DIALOG_DELETE = 10;
    private final int ALERT_DIALOG_CLOSE = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edDescription = findViewById(R.id.edit_descriptiom);
        edTitle = findViewById(R.id.edit_title);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        noteModel = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (noteModel != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }else {
            noteModel = new NoteModel();
        }

        String actionBarTitle;
        String btnTitle;

        //button change
        if (isEdit == true) {
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (noteModel != null) {
                edTitle.setText(noteModel.getTitle());
                edDescription.setText(noteModel.getDescription());
            }
        }else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnSubmit.setText(btnTitle);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_submit) {
            String title = edTitle.getText().toString().trim();
            String description = edDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                edTitle.setError("Field tidak boleh kosong!");
                return;
            }

            noteModel.setTitle(title);
            noteModel.setDescription(description);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, noteModel);
            intent.putExtra(EXTRA_POSITION, position);

            if (isEdit) {
                long result = noteHelper.updateNote(noteModel);
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent);
                    finish();
                }else {
                    Toast.makeText(this, "Gagal mengupload data", Toast.LENGTH_SHORT).show();

                }
            }else {
                noteModel.setDate(getCurrenDate());
                long result = noteHelper.insertNote(noteModel);

                if (result > 0) {
                    noteModel.setId((int)result);
                    setResult(RESULT_ADD, intent);
                    finish();

                }else {
                    Toast.makeText(this, " Gagal menambah data!", Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_main, menu);

        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        }else {
            dialogTitle = "Hapus Note";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }

        AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setTitle(dialogTitle);
        builderDialog
                .setMessage(dialogMessage)
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        if (isDialogClose) {
                            finish();
                        }else {
                            long result = noteHelper.deleteNote(noteModel.getId());

                            if (result > 0) {
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            }else {
                                Toast.makeText(NoteAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("TIDAk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builderDialog.create();
        alertDialog.show();
    }

    private String getCurrenDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }
}
