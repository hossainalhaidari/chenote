package ir.chenarstudio.chenote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class NoteDB {

    Context context;
    SQLiteDatabase db;
    String id;

    public NoteDB(Context context, String id) {
        this.context = context;
        db = context.openOrCreateDatabase("NoteDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes(id VARCHAR, content TEXT);");
        this.id = id;
    }

    public String get() {
        Cursor c = db.rawQuery("SELECT * FROM notes WHERE `id` = '" + id + "'", null);
        if(c.getCount() == 0)
            return "";

        c.moveToFirst();

        String[] a = new String[] {"1", "2"};
        String b = a[1];


        return c.getString(1);


    }

    public static class SingleNote {
        String text;
        String id;
    }

    public List<SingleNote> getAll() {
        Cursor c = db.rawQuery("SELECT * FROM notes", null);
        if(c.getCount() == 0)
            return null;

        ArrayList<SingleNote> inFiles = new ArrayList<SingleNote>();

        while(c.moveToNext())
        {
            SingleNote note = new SingleNote();
            note.id = c.getString(0);
            note.text = c.getString(1).replace("\n", " ");

            if(note.text.length() > 35) note.text = note.text.substring(0, 30) + "...";

            inFiles.add(note);
        }

        return inFiles;
    }

    public void save(String content) {
        if(id == "") {
            long unixTime = System.currentTimeMillis() / 1000L;
            id = String.valueOf(unixTime);
            db.execSQL("INSERT INTO notes VALUES('" + id + "', '" + content +"')");
        }
        else
            db.execSQL("UPDATE notes SET `content` = '" + content + "' WHERE `id` = '" + id + "'");
    }

    public void removeAll(String ids) {
        if(ids != "")
            db.execSQL("DELETE FROM notes WHERE `id` IN (" + ids + ")");
    }
}
