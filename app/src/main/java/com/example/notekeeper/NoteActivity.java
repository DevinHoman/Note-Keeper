package com.example.notekeeper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    public static final int COURSE_LOADER = 1;
    public final String TAG = getClass().getSimpleName();
    public static final String NOTE_ID ="com.example.notekeeper.NOTE_ID";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0),"","");
    private Boolean isNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private Boolean mIsCanceling = false;
    private String originalNoteCourseID;
    private String originalNoteTitle;
    private String originalNoteText;
    private NoteKeeperOpenHelper dbOpenHelper;
    private Cursor noteCursor;
    private int courseIdPos;
    private int noteTitlePos;
    private int noteTextPos;
    private SimpleCursorAdapter adapterCourses;
    private boolean courseQueryFinished;
    private boolean noteQueryFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbOpenHelper = new NoteKeeperOpenHelper(this);



        mSpinnerCourses = findViewById(R.id.spinner_courses);


        adapterCourses = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_item,null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1},0);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        getSupportLoaderManager().initLoader(COURSE_LOADER,null,this);

        readDisplayStateValues();
        if(savedInstanceState == null){
            saveOriginalNoteValues();
        }else{
            restoreOriginalNoteValue(savedInstanceState);
        }

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);


        if(!isNewNote)
            getSupportLoaderManager().initLoader(LOADER_NOTES,null,this);
        Log.d(TAG,"onCreate");

    }

    private void loadCourseData() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String[] columns = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };

        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, columns, null,null,
                null,null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        adapterCourses.changeCursor(cursor);
    }

    private void loadNoteData() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String courseId = "android_intents";
        String titleStart = "dynamic";


        String selection = NoteInfoEntry._ID + " = ?";

        String[] selectionArgs = {Integer.toString(mNoteId)};


        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };

        noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, selection, selectionArgs,
                null,null,null);

        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        noteCursor.moveToNext();
        displayNote();



    }

    @Override
    protected void onDestroy() {
        dbOpenHelper.close();
        super.onDestroy();
    }

    private void restoreOriginalNoteValue(Bundle savedInstanceState) {
       originalNoteCourseID = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
       originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
       originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;
        originalNoteCourseID = mNote.getCourse().getCourseId();
        originalNoteTitle = mNote.getTitle();
        originalNoteText = mNote.getText();

    }

    private void displayNote() {

            String courseID = noteCursor.getString(courseIdPos);
            String noteTitle = noteCursor.getString(noteTitlePos);
            String noteText = noteCursor.getString(noteTextPos);




            int courseIndex = getIndexCourseId(courseID);
            mSpinnerCourses.setSelection(courseIndex);
            mTextNoteTitle.setText(noteTitle);
            mTextNoteText.setText(noteText);
            //mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0),"","");

            noteCursor.close();



    }

    private int getIndexCourseId(String courseID) {
        Cursor cursor = adapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if (courseID.equals(cursorCourseId))
                break;


                courseRowIndex++;
                more = cursor.moveToNext();


        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        isNewNote = mNoteId == ID_NOT_SET;
        if(isNewNote){
            createNewNote();
        }
        Log.i(TAG,"mNoteId:" + mNoteId);
        //mNote = DataManager.getInstance().getNotes().get(mNoteId);

    }

    private void createNewNote() {
        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, "");
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "");
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "");

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        mNoteId = (int) db.insert(NoteInfoEntry.TABLE_NAME,null,values);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }else if(id == R.id.action_cancel){
            mIsCanceling = true;
            finish();
        }else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCanceling){
            Log.i(TAG,"Canceling note at position :"+ mNoteId);
            if(isNewNote) {
                deleteNoteFromDatabase();
            }else{
                storePreviousNoteValues();
            }
        }else {
            saveNote();
        }
        Log.d(TAG,"onPause");
    }

    private void deleteNoteFromDatabase() {
        final String selection = NoteInfoEntry._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mNoteId)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                db.delete(NoteInfoEntry.TABLE_NAME,selection,selectionArgs);
                return null;
            }
        };
        task.execute();

    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseID);
        mNote.setCourse(course);
        mNote.setTitle(originalNoteTitle);
        mNote.setText(originalNoteText);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ORIGINAL_NOTE_COURSE_ID,originalNoteCourseID);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,originalNoteText);
    }

    private void saveNote() {
        String courseID = selectedCourseId();
        String noteTitle = mTextNoteTitle.getText().toString();
        String noteText = mTextNoteText.getText().toString();
        saveNoteToDatabase(courseID,noteTitle,noteText);
    }

    private String selectedCourseId() {
        int selectedPos = mSpinnerCourses.getSelectedItemPosition();
        Cursor cursor = adapterCourses.getCursor();
        cursor.moveToPosition(selectedPos);
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdPos);
        return courseId;
    }

    private void saveNoteToDatabase(String courseID ,String noteTitle , String noteText){
        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID,courseID);
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE,noteTitle);
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT,noteText);

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.update(NoteInfoEntry.TABLE_NAME,values,selection,selectionArgs);
    }

    private void sendEmail() {
        CourseInfo course =(CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check out what I learned in the Pluralsight course \""
            + course.getTitle() + "\"n" + mTextNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES) {
            loader = createLoaderNotes();
        } else if(id == COURSE_LOADER) {
            loader = createLoaderCourses();
        }
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        courseQueryFinished = false;
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
                String[] columns = {
                        CourseInfoEntry.COLUMN_COURSE_TITLE,
                        CourseInfoEntry.COLUMN_COURSE_ID,
                        CourseInfoEntry._ID
                };

                return db.query(CourseInfoEntry.TABLE_NAME, columns, null,null,
                        null,null, CourseInfoEntry.COLUMN_COURSE_TITLE);

            }
        };
    }

    private CursorLoader createLoaderNotes() {
        noteQueryFinished = false;
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

                String selection = NoteInfoEntry._ID + " = ?";

                String[] selectionArgs = {Integer.toString(mNoteId)};


                String[] noteColumns = {
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT
                };

                return db.query(NoteInfoEntry.TABLE_NAME, noteColumns, selection, selectionArgs,
                        null,null,null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES)
            loadFinishedNotes(data);

        else if(loader.getId() == COURSE_LOADER){
            adapterCourses.changeCursor(data);
            courseQueryFinished = true;
            displayNoteWhenQueryIsFinished();
        }


    }

    private void loadFinishedNotes(Cursor data) {
        noteCursor = data;
        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        noteCursor.moveToNext();
        noteQueryFinished = true;
        displayNoteWhenQueryIsFinished();
    }

    private void displayNoteWhenQueryIsFinished() {
        if(noteQueryFinished && courseQueryFinished)
            displayNote();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            if(noteCursor != null)
                noteCursor.close();
        }else if(loader.getId()== COURSE_LOADER){
            adapterCourses.changeCursor(null);
        }

    }
}