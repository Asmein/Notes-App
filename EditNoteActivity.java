// EditNoteActivity.java

package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private EditText etNoteContent;
    private Button btnSaveNote;
    private Button btnDeleteNote;
    private LinearLayout layoutEditNote;
    private View themeWhite;
    private View themeYellow;
    private View themeBlue;
    private View themeGreen;
    private View themePink;

    private int noteIndex = -1;
    private List<Note> notesList;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnDeleteNote = findViewById(R.id.btnDeleteNote);
        layoutEditNote = findViewById(R.id.layoutEditNote);
        themeWhite = findViewById(R.id.theme_white);
        themeYellow = findViewById(R.id.theme_yellow);
        themeBlue = findViewById(R.id.theme_blue);
        themeGreen = findViewById(R.id.theme_green);
        themePink = findViewById(R.id.theme_pink);

        sharedPreferences = getSharedPreferences("MyNotesData", MODE_PRIVATE);
        gson = new Gson();
        loadNotes();

        noteIndex = getIntent().getIntExtra("note_index", -1);
        if (noteIndex != -1) {
            Note existingNote = notesList.get(noteIndex);
            etNoteTitle.setText(existingNote.getTitle());
            etNoteContent.setText(existingNote.getContent());
            layoutEditNote.setBackgroundColor(existingNote.getBackgroundColor());
        } else {
            layoutEditNote.setBackgroundColor(getResources().getColor(R.color.note_theme_white)); // Default
        }

        themeWhite.setOnClickListener(v -> setBackgroundColor(R.color.note_theme_white));
        themeYellow.setOnClickListener(v -> setBackgroundColor(R.color.note_theme_light_yellow));
        themeBlue.setOnClickListener(v -> setBackgroundColor(R.color.note_theme_light_blue));
        themeGreen.setOnClickListener(v -> setBackgroundColor(R.color.note_theme_light_green));
        themePink.setOnClickListener(v -> setBackgroundColor(R.color.note_theme_light_pink));

        btnSaveNote.setOnClickListener(v -> saveNote());
        btnDeleteNote.setOnClickListener(v -> deleteNote());

        if (noteIndex == -1) {
            btnDeleteNote.setVisibility(View.GONE);
        }
    }

    private void setBackgroundColor(int colorResId) {
        layoutEditNote.setBackgroundColor(getResources().getColor(colorResId));
    }

    private void loadNotes() {
        String json = sharedPreferences.getString("notes", null);
        Type type = new TypeToken<List<Note>>() {}.getType();
        notesList = gson.fromJson(json, type);
        if (notesList == null) {
            notesList = new ArrayList<>();
        }
    }

    private void saveNotes() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(notesList);
        editor.putString("notes", json);
        editor.apply();
    }

    private void saveNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();
        int backgroundColor = ((android.graphics.drawable.ColorDrawable) layoutEditNote.getBackground()).getColor();

        if (noteIndex != -1) {
            notesList.set(noteIndex, new Note(title, content, backgroundColor));
        } else {
            notesList.add(new Note(title, content, backgroundColor));
        }
        saveNotes();
        finish();
    }

    private void deleteNote() {
        if (noteIndex != -1) {
            notesList.remove(noteIndex);
            saveNotes();
            finish();
        }
    }
}