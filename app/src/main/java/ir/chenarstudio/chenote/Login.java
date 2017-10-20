package ir.chenarstudio.chenote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    EditText txtPassword;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Settings settings = new Settings(this);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        password = settings.get("password", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Login.this, NotesList.class);
        startActivity(intent);
        Login.this.finish();
    }

    public void doLogin(View v) {
        if (BCrypt.checkpw(txtPassword.getText().toString(), password)) {
            NotesList.needPassword = false;
            finish();
        }
        else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_pass), Toast.LENGTH_SHORT).show();
    }
}
