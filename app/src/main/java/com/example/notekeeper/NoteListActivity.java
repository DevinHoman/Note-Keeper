package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> adapterNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this,MainActivity.class));
            }
        });

        intializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterNote.notifyDataSetChanged();
    }

    private void intializeDisplayContent() {

        final ListView listNotes = findViewById(R.id.list_notes);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        adapterNote = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,notes);

        listNotes.setAdapter(adapterNote);

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Makes a nested class
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(NoteListActivity.this,MainActivity.class);
               // NoteInfo note = (NoteInfo)listNotes.getItemAtPosition(position);
                intent.putExtra(MainActivity.NOTE_POSITION,position);
                startActivity(intent);
            }
        });
    }

}
