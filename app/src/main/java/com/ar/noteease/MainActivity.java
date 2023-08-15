package com.ar.noteease;
import static com.ar.noteease.R.string.alert_delete_acc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ar.noteease.Adapters.NotesListAdapter;
import com.ar.noteease.Database.RoomDB;
import com.ar.noteease.Models.Notes;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{


    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    SearchView searchView_home;
    Notes selectedNote;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mendapatkan widget/layout
        recyclerView = findViewById(R.id.recycler_home);
        fab_add = findViewById(R.id.fab_add);
        searchView_home = findViewById(R.id.searchView_home);

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecycle(notes);

        //menambahkan event klik untuk floating action button (tombol tambah)
        fab_add.setOnClickListener(new View.OnClickListener() {

            @Override
            //ketika floating action button diklik memindahkan ke layout untuk tambah data
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        //menambahkan event klik untuk search view
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //ketika query diubah, maka akan menampilkan data yang sesuai dengan query
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        //event untuk memunculkan catatan masih kosong
        View view = findViewById(R.id.textView_no_notes);
        if (notes == null || notes.size() == 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        SearchView searchView = (SearchView) findViewById(R.id.searchView_home);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        //mengganti warna search view(hint)
        searchEditText.setTextColor(getResources().getColor(R.color.yellow_gedang));
        searchEditText.setHintTextColor(getResources().getColor(R.color.yellow_gedang));

    }


    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //fungsi tambah data
        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
        //fungsi edit data
        else if (requestCode == 102) {
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }


    private void updateRecycle(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        //fungsi untuk view catatan
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        //memunculkan menu
        @Override
        public void onLongClick(Notes notes, CardView cardView) {
           selectedNote = new Notes();
           selectedNote = notes;
           showPopup(cardView);
        }
    };

    //fungsi menampilkan menu
    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @SuppressLint({"NotifyDataSetChanged", "NonConstantResourceId"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            //menampilkan star/pin
            case R.id.pin:
                if (selectedNote.isPinned()){
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Unppinned!", Toast.LENGTH_SHORT).show();
                }
                else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Pinned!", Toast.LENGTH_SHORT).show();
                }

                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;

                //fungsi pindah ke layout untuk edit data
                case R.id.edit:

                    Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                    intent.putExtra("old_note", selectedNote);
                    startActivityForResult(intent, 102);
                    return true;

                    //fungsi hapus data
                case R.id.delete:
                    //memunculkan alert dialog
                    builder = new AlertDialog.Builder(this);
//                    builder.setMessage("Konfirmasi Hapus").setTitle(R.string.title);
                    //jika true
                    builder.setMessage("Pesan akan dihapus selamanya, Anda yakin?").setCancelable(false)
                            .setPositiveButton(R.string.hapus, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            database.mainDAO().delete(selectedNote);
                            notes.remove(selectedNote);
                            notesListAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, alert_delete_acc, Toast.LENGTH_SHORT).show();
                        }
                        //jika false
                    }).setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, R.string.alert_delete_cancel, Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Konfirmasi Hapus");
                    alertDialog.show();

            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;

    }

    //fungsi halaman privacy policy dan about
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.privacypolicy){
            String url = "https://docs.google.com/document/d/1BfiRCJdEaHYqGWkiUCvYq8NZsw9_-ie8SsO01q_eN4w/edit?usp=sharing";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (item.getItemId()==R.id.about){
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

}