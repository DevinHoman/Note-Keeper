package com.example.notekeeper;

import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.junit.Assert.*;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;


import java.util.List;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class NextThroughNotesTest {
   /* @Rule
    public ActivityTestRule<Main2Activity> activityActivityTestRule =
            new ActivityTestRule<>(Main2Activity.class);

    @Test
    public void NextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions
                .<NoteRecyclerAdapter.ViewHolder>actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        int index = 0;
        NoteInfo note = notes.get(index);

        onView(withId(R.id.spinner_courses)).check(
                matches(withSpinnerText(note.getCourse().getTitle())));

        onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
        onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

    }*/

}