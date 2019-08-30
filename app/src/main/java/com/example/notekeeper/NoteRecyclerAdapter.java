package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;



public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context Context;

    private Cursor mCursor;
    private final LayoutInflater layoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;


    public NoteRecyclerAdapter(Context context,Cursor cursor) {
        Context = context;
        mCursor = cursor;
        layoutInflater = LayoutInflater.from(Context);

        populateColumnPosition();

    }

    private void populateColumnPosition() {
        if(mCursor == null)
            return;
        //get column indexes from cursor
        mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.item_note_list,viewGroup,false);
        //View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note_list,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        String course = mCursor.getString(mCoursePos);

        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        viewHolder.textCourse.setText(course);
        viewHolder.textTitle.setText(noteTitle);
        viewHolder.id = id;
    }

    @Override
    public int getItemCount() {
        //if(mCursor == null) return 0 else return mCursor.getCount
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public final TextView textCourse;
        public final TextView textTitle;
        public int id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Context, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, id);
                    Context.startActivity(intent);
                }
            });

        }
    }


}
