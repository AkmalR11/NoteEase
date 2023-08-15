package com.ar.noteease;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ar.noteease.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    //membuat variabel
    EditText editText_title, editText_notes;
    ImageView imageView_save;
    Notes notes;
    boolean isOldNotes = false;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        //mengambil view
        imageView_save = findViewById(R.id.imageView_save);
        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);

        //catatan baru
        notes = new Notes();
        try {
            //mengambil data dari intent
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            editText_title.setText(notes.getTitle());
            editText_notes.setText(notes.getNotes());
            isOldNotes = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        notes = (Notes) getIntent().getSerializableExtra("old_note");

        //menambahkan event klik pada gambar save
        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //memasukan data
                String title = editText_title.getText().toString();
                String description = editText_notes.getText().toString();

                //pengecekan jika kosong
                if (description.isEmpty()) {
                    Toast.makeText(NotesTakerActivity.this, "Waduh, deskripsi masih kosong nih.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //pembuatan tanggal
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                //pengecekan jika ada data yang lama
                if (!isOldNotes){
                    notes = new Notes();
                }

                //pembuatan alert dialog
                builder = new AlertDialog.Builder(NotesTakerActivity.this);
                builder.setMessage("Pastikan data yang dimasukan sudah benar.").setCancelable(false)
                        //kondisi true
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes.setTitle(title);
                                notes.setNotes(description);
                                notes.setDate(formatter.format(date));

                                Intent intent = new Intent();
                                intent.putExtra("note", notes);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //menampilkan alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Simpan Data?");
                alertDialog.show();
            }
        });
    }
}