package com.example.notekeeper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.notekeeper.NotekeeperProviderContract.Notes;

public class NoteUploader {
    private final String TAG = getClass().getSimpleName();

    private final Context mContext;
    private boolean canceled;

    public NoteUploader(Context context){
        mContext = context;
    }

    public Boolean isCancelled(){return canceled;}

    public void cancel(){
        canceled = true;
    }

    public void doUpload(Uri dataUri){
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT
        };

        Cursor cursor = mContext.getContentResolver().query(dataUri,columns,null,null,null);
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG,"***** Upload Start **** - " + dataUri);
        canceled = false;
        while(!canceled && cursor.moveToNext()){
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if(!noteTitle.equals("")){
                Log.i(TAG,"**** Uploading note: " + courseId + "|" + noteTitle + "|" + noteText);
                simulateLongRunningWork();
            }
        }

    }

    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(2000);
        } catch(Exception ex) {}
    }

}
