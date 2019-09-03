package com.forteknik.mynotes2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "dbnoteapp";

    public static final int DATABASE_VERSION = 1;
    public static final String SQL_CREATE_TABLE_NOTE =
            String.format("CREATE TABLE %s" +
                            " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "%s TEXT NOT NULL,"+
                    "%s TEXT NOT NULL,"+
                    "%s TEXT NOT NULL)",

                    DatabaseContract.TABLE_NOTE,
                    DatabaseContract.NoteColums._ID,
                    DatabaseContract.NoteColums.TITLE,
                    DatabaseContract.NoteColums.DESCRIPTION,
                    DatabaseContract.NoteColums.DATE
            );


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ DatabaseContract.TABLE_NOTE);
        onCreate(db);

    }
}
