package com.example.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Notekeeper.db";
    public static final int DATABASE_VERSION = 1;

   // SQLiteDatabase database;

    public NoteKeeperOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
        //database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
            db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE);
            Log.d("SQL", "After SQL exec statements");

            DatabaseDataWorker worker = new DatabaseDataWorker(db);
            worker.insertCourses();
            worker.insertSampleNotes();
        } catch (Exception e) {
            Log.d("SQL", e.getMessage());
        }
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop old table
        //db.execSQL("DROP TABLE IF EXISTS " + NoteKeeperDatabaseContract.CourseInfoEntry.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + NoteKeeperDatabaseContract.NoteInfoEntry.TABLE_NAME);

       // onCreate(db);

    }
}
