package com.example.notekeeper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.example.notekeeper.NotekeeperProviderContract.Courses;
import com.example.notekeeper.NotekeeperProviderContract.CoursesIdColumns;
import com.example.notekeeper.NotekeeperProviderContract.Notes;

public class NotekeeperProvider extends ContentProvider {

    public static final String MIME_VENDER_TYPE = "vnd." + NotekeeperProviderContract.AUTHORITY + ".";
    private  NoteKeeperOpenHelper mDbOpenHelper;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    public static final int NOTES_ROW = 3;
    public static final int COURSES_ROW = 4;
    public static final int NOTES_EXPANDED_ROW = 5;

    static {
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY, Notes.PATH, NOTES);
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY,Notes.PATH_EXPANDED, NOTES_EXPANDED);
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY,Notes.PATH +"/#", NOTES_ROW);
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY,Courses.PATH + "/#",COURSES_ROW);
        sUriMatcher.addURI(NotekeeperProviderContract.AUTHORITY,Notes.PATH_EXPANDED + "/#",NOTES_EXPANDED_ROW);
    }

    public NotekeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowID = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case NOTES:
                nRows = db.delete(NoteInfoEntry.TABLE_NAME,selection,selectionArgs);
                //content://com.example.notekeeper.provider/notes/1
                break;
            case COURSES:
                nRows = db.delete(CourseInfoEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case NOTES_EXPANDED :
                Log.d("NOTES_EXPANDED","Read only");
                break;
            case COURSES_ROW:
                rowID = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowID)};
                nRows = db.delete(CourseInfoEntry.TABLE_NAME,rowSelection,rowSelectionArgs);
                break;
            case NOTES_ROW :
                rowID = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowID)};
                nRows = db.delete(NoteInfoEntry.TABLE_NAME,rowSelection,rowSelectionArgs);
                break;
            case NOTES_EXPANDED_ROW :
                Log.d("NOTES_EXPANDED_ROW","Read only");
                break;

        }
        return nRows ;
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES :
                //vnd.android.cursor.dir/vnd.come.example.notekeeper.provider.courses
            mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                    MIME_VENDER_TYPE + Courses.PATH;
            break;
            case NOTES :
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDER_TYPE + Notes.PATH;
                break;
            case NOTES_EXPANDED :
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDER_TYPE + Notes.PATH_EXPANDED;
                break;
            case NOTES_ROW :
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        MIME_VENDER_TYPE + Notes.PATH;

        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowID = -1;
        Uri rowUri = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case NOTES:
                rowID = db.insert(NoteInfoEntry.TABLE_NAME,null,values);
                //content://com.example.notekeeper.provider/notes/1
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI,rowID);
                break;
            case COURSES:
                rowID = db.insert(CourseInfoEntry.TABLE_NAME,null,values);
                ContentUris.withAppendedId(Courses.CONTENT_URI,rowID);
                break;
            case NOTES_EXPANDED :
                Log.d("NOTES_EXPANDED","Read only");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + uriMatch);

        }

        return  rowUri;
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
                case COURSES:
                    cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                case NOTES:
                    cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;

                case NOTES_EXPANDED:
                    cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                    break;

                case NOTES_ROW:
                    long rowID = ContentUris.parseId(uri);
                    String rowSelection = NoteInfoEntry._ID + " = ?";
                    String[] rowSelectionArgs = new String[]{Long.toString(rowID)};
                    cursor = db.query(NoteInfoEntry.TABLE_NAME,projection,rowSelection,rowSelectionArgs,
                            null,null,null);
            }
            return cursor;
    }

    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection,
                                    String selection, String[] selectionArgs, String sortOrder) {

        String[] columns = new String[projection.length];
        for(int idx = 0 ; idx < projection.length ; idx++){
            columns[idx] = projection[idx].equals(BaseColumns._ID) ||
                    projection[idx].equals(CoursesIdColumns.COLUMN_COURSE_ID)?
                    NoteInfoEntry.getQName(projection[idx]) : projection[idx];

        }

        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin,columns,selection,selectionArgs,null,null,sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES :
                nRows = db.update(CourseInfoEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case NOTES :
                nRows = db.update(NoteInfoEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case NOTES_EXPANDED :
                Log.d("Notes_expanded","read only");
                break;
            case COURSES_ROW :
                rowId = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(CourseInfoEntry.TABLE_NAME,values,rowSelection,rowSelectionArgs);
                break;
            case NOTES_ROW :
                rowId = ContentUris.parseId(uri);
                rowSelection = NoteInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(NoteInfoEntry.TABLE_NAME,values,rowSelection,rowSelectionArgs);
                break;
            case NOTES_EXPANDED_ROW :
                Log.d("Notes_expanded_row","read only");
                break;

        }
        return nRows;

    }
}
