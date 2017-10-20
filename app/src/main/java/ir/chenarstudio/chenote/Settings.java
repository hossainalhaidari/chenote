package ir.chenarstudio.chenote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Settings {

    Context context;
    SQLiteDatabase db;
    String id;

    public Settings(Context context) {
        this.context = context;
        db = context.openOrCreateDatabase("NoteDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS settings(key VARCHAR, value TEXT);");
    }

    public String get(String key, String def) {
        Cursor c = db.rawQuery("SELECT `value` FROM `settings` WHERE `key` = '" + key+ "'", null);
        if(c.getCount() == 0)
            return def;

        c.moveToFirst();
        return c.getString(0);
    }

    public boolean exists(String key) {
        Cursor c = db.rawQuery("SELECT `value` FROM `settings` WHERE `key` = '" + key+ "'", null);
        if(c.getCount() == 0)
            return false;

        return true;
    }

    public void set(String key, String value) {
        if(exists(key))
            db.execSQL("UPDATE `settings` SET `value` = '" + value + "' WHERE `key` = '" + key + "'");
        else
            db.execSQL("INSERT INTO `settings` (`key`,`value`) VALUES ('" + key + "', '" + key + "')");
    }
}
