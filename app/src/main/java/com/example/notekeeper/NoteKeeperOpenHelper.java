package com.example.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import com.example.notekeeper.NoteKeeperDatabaseContract.*;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notekeeper.db";
    public static final int DATABASE_VERSION = 2;
    public NoteKeeperOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE course_info (course_id, course_title)
        final String SQL_CREATE_TABLE_COURSES =
                "CREATE TABLE " + CourseInfoEntry.TABLE_NAME + " (" +
                        CourseInfoEntry._ID + " INTEGER PRIMARY KEY, " +
                        CourseInfoEntry.COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, " +
                        CourseInfoEntry.COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

        final String SQL_CREATE_TABLE_NOTES =
                "CREATE TABLE " + NoteInfoEntry.TABLE_NAME + " (" +
                        NoteInfoEntry._ID + " INTEGER PRIMARY KEY, " +
                        NoteInfoEntry.COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                        NoteInfoEntry.COLUMN_NOTE_TEXT + " TEXT, " +
                        NoteInfoEntry.COLUMN_COURSE_ID + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_TABLE_COURSES);
        db.execSQL(SQL_CREATE_TABLE_NOTES);
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
        //getWritableDatabase();
        //insertDummyData();
        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){

            db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

        }

    }


    
}
