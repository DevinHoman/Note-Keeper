package com.example.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    private NoteRecyclerAdapter noteRecyclerAdapter;
    private RecyclerView recyclerItems;
    private LinearLayoutManager notesLinearManager;
    private CourseRecyclerAdapter courseRecyclerAdapter;
    private GridLayoutManager coursesLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbOpenHelper = new NoteKeeperOpenHelper(this);
        //db = mDbOpenHelper.getWritableDatabase();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main2Activity.this, NoteActivity.class));
            }
        });



        initializeDisplayContent();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // adapterNote.notifyDataSetChanged();
        // Loader mangager not working, TODO change to Room and Live Data after finished with course
       //getLoaderManager().restartLoader(LOADER_NOTES,null,this);
        loadNotes();


        updateNavHeader();
    }

    private void loadNotes() {
       SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] noteColumns = {
                NoteInfoEntry.getQName(NoteInfoEntry._ID),
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_TITLE};

        //note_info JOIN course_info ON note_info.course_id = course_info.course_id
        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName( NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + " ," + NoteInfoEntry.COLUMN_NOTE_TITLE;

        final Cursor noteCursor = db.query(tablesWithJoin, noteColumns, null, null,
                null, null, noteOrderBy);


        noteRecyclerAdapter.changeCursor(noteCursor);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView)headerView.findViewById(R.id.text_user_name);
        TextView textEmailAdress = (TextView)headerView.findViewById(R.id.text_email_adress);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("key_signature" ,"");
        String emailAdress = pref.getString("key_email","");

        textUserName.setText(userName);
        textEmailAdress.setText(emailAdress);
    }

    private void initializeDisplayContent() {

        DataManager.loadFromDatabase(mDbOpenHelper);
        recyclerItems = findViewById(R.id.list_items);
        notesLinearManager = new LinearLayoutManager(this);
        coursesLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));



        noteRecyclerAdapter = new NoteRecyclerAdapter(this,null);

        List<CourseInfo> course = DataManager.getInstance().getCourses();
        courseRecyclerAdapter = new CourseRecyclerAdapter(this,course);

        displayNotes();

    }

    private void displayNotes() {
        recyclerItems.setLayoutManager(notesLinearManager);
        recyclerItems.setAdapter(noteRecyclerAdapter);


        selectNavigationMenuItem(R.id.nav_notes);

    }

    private void selectNavigationMenuItem(int id) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses(){

        recyclerItems.setLayoutManager(coursesLayoutManager);
        recyclerItems.setAdapter(courseRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_courses);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
           displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();
        }  else if (id == R.id.nav_share) {
           // handleSelection(R.string.nav_share_message);
            handleShare();
        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view,"Share to -" +
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .getString("key_socialnetwork","")
                                    ,Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view,message_id,Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES) {
            loader = new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                    final String[] noteColumns = {
                            NoteInfoEntry._ID,
                           NoteInfoEntry.COLUMN_NOTE_TITLE,
                            NoteInfoEntry.COLUMN_COURSE_ID};

                    final String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
                    return db.query(NoteInfoEntry.TABLE_NAME, noteColumns, null, null,
                            null, null, noteOrderBy);
                }
            };
        }

        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES) {
            noteRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES) {
            noteRecyclerAdapter.changeCursor(null);
        }

    }
}
