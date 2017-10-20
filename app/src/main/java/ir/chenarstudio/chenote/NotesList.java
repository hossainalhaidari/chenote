package ir.chenarstudio.chenote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class NotesList extends AppCompatActivity {

    ArrayAdapter<String> notesArrayAdapter;
    ListView lstNotes;
    MenuItem mnuAbout;
    MenuItem mnuSelect;
    MenuItem mnuShare;
    MenuItem mnuDelete;
    FloatingActionButton btnAdd;
    NoteDB db;
    Settings settings;
    String[] ids;
    String[] texts;
    boolean isSelect = false;
    public static boolean needPassword = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        init();
    }

    private void init() {
        settings = new Settings(this);
        db = new NoteDB(this, "");
        lstNotes = (ListView)findViewById(R.id.lstNotes);
        btnAdd = (FloatingActionButton) findViewById(R.id.fab);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesList.this, ViewNote.class);
                startActivity(intent);
            }
        });
        Toast.makeText(getApplicationContext(), "create", Toast.LENGTH_SHORT).show();
    }

    public void prepareNotes() {
        List<NoteDB.SingleNote> files =  db.getAll();
        if(files == null) {
            ids = null;
            texts = null;
        } else {
            texts = new String[files.size()];
            ids = new String[files.size()];
            for(int i = 0; i < files.size(); i++) {
                ids[i] = files.get(i).id;
                texts[i] = files.get(i).text;
            }
        }
    }

    public void loadNotes(boolean multi) {
        if(ids == null) {
            lstNotes.setAdapter(null);
            return;
        }

        if(multi) {
            lstNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            notesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, texts);
            lstNotes.setAdapter(notesArrayAdapter);
            lstNotes.setOnItemClickListener(null);
        }
        else {
            lstNotes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            notesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, texts);
            lstNotes.setAdapter(notesArrayAdapter);
            lstNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(NotesList.this, ViewNote.class);
                    intent.putExtra("id", ids[position]);
                    startActivity(intent);
                }
            });

            lstNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                    if(!isSelect) {
                        loadNotes(true);
                        toggleMenus(true);
                        lstNotes.setItemChecked(pos, true);
                    }
                    return true;
                }
            });
        }
    }

    private void toggleMenus(boolean multi)
    {
        if(multi) {
            isSelect = true;
            mnuSelect.setVisible(false);
            mnuAbout.setVisible(false);
            mnuShare.setVisible(true);
            mnuDelete.setVisible(true);
            btnAdd.setVisibility(View.INVISIBLE);
        } else {
            isSelect = false;
            mnuSelect.setVisible(true);
            mnuAbout.setVisible(true);
            mnuShare.setVisible(false);
            mnuDelete.setVisible(false);
            btnAdd.setVisibility(View.VISIBLE);
        }
    }

    private void checkPassword() {
        if(needPassword == false) return;
        //String hashed = BCrypt.hashpw("123", BCrypt.gensalt());
        //settings.set("password", hashed);

        String curpass = settings.get("password", "");
        if(curpass != "") {
            Intent intent = new Intent(NotesList.this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareNotes();
        loadNotes(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPassword();
    }

    @Override
    public void onStop() {
        super.onStop();
        needPassword = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        mnuSelect = menu.findItem(R.id.action_select);
        mnuShare = menu.findItem(R.id.action_share);
        mnuDelete = menu.findItem(R.id.action_delete);
        mnuAbout = menu.findItem(R.id.action_about);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(NotesList.this, About.class);
            startActivity(intent);
        } else if (id == R.id.action_select) {
            loadNotes(true);
            toggleMenus(true);
        } else if (id == R.id.action_delete) {
            if(lstNotes.getCheckedItemCount() > 0) {
                String selected = "";
                SparseBooleanArray checked = lstNotes.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    selected += "'" + ids[position] + "',";
                }

                if (selected != "") {
                    selected = selected.substring(0, selected.length() - 1);
                    db.removeAll(selected);
                    prepareNotes();
                }
            }
            loadNotes(false);
            toggleMenus(false);
        } else if (id == R.id.action_share) {
            if(lstNotes.getCheckedItemCount() > 0) {
                SparseBooleanArray checked = lstNotes.getCheckedItemPositions();
                String text = texts[checked.keyAt(0)];
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
            }
            loadNotes(false);
            toggleMenus(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (isSelect) {
            loadNotes(false);
            toggleMenus(false);
        } else {
            super.onBackPressed();
        }
    }
}
