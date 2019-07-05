package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context Context;
    private final List<CourseInfo> courses;
    private final LayoutInflater layoutInflater;

    public CourseRecyclerAdapter(Context Context, List<CourseInfo> courses) {
        this.Context = Context;
        layoutInflater = LayoutInflater.from(Context);
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = layoutInflater.inflate(R.layout.item_course_list,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CourseInfo course = courses.get(position);
        viewHolder.textCourse.setText(course.getTitle());
        viewHolder.currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public final TextView textCourse;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Snackbar.make(view,courses.get(currentPosition).getTitle(),
                           Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
