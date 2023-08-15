package com.ar.noteease;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ar.noteease.Models.Notes;

public class ViewNoteActivity extends AppCompatActivity{

    //variabel
    TextView textView_title, textView_notes;
    Notes notes;
    boolean isOldNotes = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        textView_notes = findViewById(R.id.textView_notes);

        notes = new Notes();
        try {
            //mengambil data dari intent
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            textView_notes.setText(notes.getNotes());
            isOldNotes = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        notes = (Notes) getIntent().getSerializableExtra("old_note");

        //memunculkan tombol back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //mengganti judul action bar
        getSupportActionBar().setTitle(notes.getTitle());

    }
    //Fungsi tombol back
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}