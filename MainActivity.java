// MainActivity.java

package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnAddNote;
    private ListView listViewNotes;
    private List<Note> notesList;
    private NoteAdapter adapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        btnAddNote = findViewById(R.id.btnAddNote);
        listViewNotes = findViewById(R.id.listViewNotes);
        sharedPreferences = getSharedPreferences("MyNotesData", MODE_PRIVATE);
        gson = new Gson();

        loadNotesInBackground(true);

        btnAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            startActivity(intent);
        });

        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("note_index", position);
            startActivity(intent);
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etSearch.setText("");
            }
        });

        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
                String query = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                filterNotesInBackground(query);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotesInBackground(false);
    }

    private void loadNotesInBackground(boolean initialLoad) {
        new Thread(() -> {
            List<Note> loadedNotes = loadNotesInternal();
            mainHandler.post(() -> {
                notesList = loadedNotes;
                adapter = new NoteAdapter(MainActivity.this, R.layout.item_note, notesList);
                listViewNotes.setAdapter(adapter);
            });
        }).start();
    }

    private List<Note> loadNotesInternal() {
        String json = sharedPreferences.getString("notes", null);
        Type type = new TypeToken<List<Note>>() {}.getType();
        List<Note> loadedNotes = gson.fromJson(json, type);
        return loadedNotes == null ? new ArrayList<>() : loadedNotes;
    }

    private void saveNotes() {
        new Thread(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = gson.toJson(notesList);
            editor.putString("notes", json);
            editor.apply();
        }).start();
    }

    private void filterNotesInBackground(String query) {
        new Thread(() -> {
            List<Note> filteredNotes = filterNotesInternal(query);
            mainHandler.post(() -> {
                adapter = new NoteAdapter(MainActivity.this, R.layout.item_note, filteredNotes);
                listViewNotes.setAdapter(adapter);
            });
        }).start();
    }

    private List<Note> filterNotesInternal(String query) {
        if (notesList == null) {
            return new ArrayList<>();
        }
        return notesList.stream()
                .filter(note -> note.getTitle().toLowerCase(Locale.getDefault()).contains(query) ||
                        note.getContent().toLowerCase(Locale.getDefault()).contains(query))
                .collect(Collectors.toList());
    }

    private static class NoteAdapter extends ArrayAdapter<Note> {

        private final LayoutInflater inflater;
        private final int listItemLayout;

        public NoteAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
            listItemLayout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(listItemLayout, parent, false);
                holder = new ViewHolder();
                holder.titleTextView = convertView.findViewById(R.id.note_title_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Note currentNote = getItem(position);
            if (currentNote != null) {
                holder.titleTextView.setText(currentNote.getTitle().isEmpty() ? "Untitled Note" : currentNote.getTitle());
                holder.titleTextView.setBackgroundColor(currentNote.getBackgroundColor());
            }

            return convertView;
        }

        private static class ViewHolder {
            TextView titleTextView;
        }
    }
}