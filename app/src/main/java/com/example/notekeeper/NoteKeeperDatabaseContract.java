package com.example.notekeeper;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
    //Constructors
    private NoteKeeperDatabaseContract() {} // make non-creatable

    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //CREATE INDEX course_info_index1 ON course_info (course_title)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_COURSE_TITLE + ")";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }


    }

    public static final class NoteInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_NOTE_TITLE + ")";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }


    }
}
