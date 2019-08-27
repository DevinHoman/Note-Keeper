package com.example.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NotekeeperProviderContract {
    //Can not be constructed
    private  NotekeeperProviderContract(){}

    public static final String AUTHORITY = "com.example.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CoursesIdColumns{
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CourseColumns{

        public static final String COLUMN_COURSE_TITLE = "course_title";

    }

    protected interface NotesColumns{
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";

    }

    public static final class Courses implements CourseColumns , BaseColumns, CoursesIdColumns {
        public static final String PATH = "courses";
        //content://com.example.notekeeper.provider/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);
    }

    public static final class Notes implements NotesColumns , BaseColumns, CoursesIdColumns,CourseColumns {
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);

        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH_EXPANDED);

    }




}

