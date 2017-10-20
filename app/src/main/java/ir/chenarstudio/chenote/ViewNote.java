package ir.chenarstudio.chenote;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ViewNote extends AppCompatActivity {

    EditText txtContent;
    String file;
    NoteDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);

        txtContent = (EditText)findViewById(R.id.txtContent);
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            file = extras.getString("id");
            db = new NoteDB(this, file);
            txtContent.setText(db.get());
        } else {
            db = new NoteDB(this, "");
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.save(txtContent.getText().toString());
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            db.save(txtContent.getText().toString());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
