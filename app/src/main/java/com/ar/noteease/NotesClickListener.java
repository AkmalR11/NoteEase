package com.ar.noteease;

import androidx.cardview.widget.CardView;

import com.ar.noteease.Models.Notes;

public interface NotesClickListener {
    // method click dan long clik
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
