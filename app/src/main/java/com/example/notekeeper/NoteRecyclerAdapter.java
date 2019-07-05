package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context Context;
    private final List<NoteInfo> notes;
    private final LayoutInflater layoutInflater;

    public NoteRecyclerAdapter(Context Context, List<NoteInfo> notes) {
        this.Context = Context;
        layoutInflater = LayoutInflater.from(Context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.item_note_list,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        NoteInfo note = notes.get(position);
        viewHolder.textCourse.setText(note.getCourse().getTitle());
        viewHolder.textTitle.setText(note.getTitle());
        viewHolder.currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public final TextView textCourse;
        public final TextView textTitle;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Context,MainActivity.class);
                    intent.putExtra(MainActivity.NOTE_POSITION,currentPosition);
                    Context.startActivity(intent);
                }
            });
        }
    }
}
