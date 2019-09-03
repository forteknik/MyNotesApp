package com.forteknik.mynotes2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.forteknik.mynotes2.entity.NoteModel;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.forteknik.mynotes2.db.DatabaseContract.NoteColums.DATE;
import static com.forteknik.mynotes2.db.DatabaseContract.NoteColums.DESCRIPTION;
import static com.forteknik.mynotes2.db.DatabaseContract.NoteColums.TITLE;
import static com.forteknik.mynotes2.db.DatabaseContract.TABLE_NOTE;

public class NoteHelper {
    private static final String DATABASE_TABLE = TABLE_NOTE;
    private static DatabaseHelper databaseHelper;
    private static  NoteHelper INSTANCE;

    private static SQLiteDatabase database;


    private NoteHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);

    }

    public static NoteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoteHelper(context);
                }
            }
        }

        return INSTANCE;
    }

    public void opedDB() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void closeDB() {
        databaseHelper.close();

        if (database.isOpen()) database.close();
    }

    public ArrayList<NoteModel> getAllNotes() {
        ArrayList<NoteModel> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null);

        cursor.moveToFirst();
        NoteModel noteModel;
        if (cursor.getCount() > 0) {
            do {
                noteModel = new NoteModel();
                noteModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                noteModel.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                noteModel.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                noteModel.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));


                arrayList.add(noteModel);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public long insertNote(NoteModel noteModel) {
        ContentValues args = new ContentValues();

        args.put(TITLE, noteModel.getTitle());
        args.put(DESCRIPTION, noteModel.getDescription());
        args.put(DATE, noteModel.getDate());
        return database.insert(DATABASE_TABLE, null, args);
    }

    public int updateNote(NoteModel noteModel) {
        ContentValues args = new ContentValues();
        args.put(TITLE, noteModel.getTitle());
        args.put(DESCRIPTION, noteModel.getDescription());
        args.put(DATE, noteModel.getDate());

        return database.update(DATABASE_TABLE, args, _ID + "= '" + noteModel.getId() + "'", null);
    }

    public int deleteNote(int id) {
        return database.delete(DATABASE_TABLE, _ID + "= '" + id+ "'", null);
    }
}
