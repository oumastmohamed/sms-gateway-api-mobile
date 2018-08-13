package pfe.stage.oumast.signupandlogintabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by poste05 on 04/04/2018.
 */

public class DBConnections extends SQLiteOpenHelper {
    private static final String DbName = "emailpass100.db";
    private static final int Version = 1;
    public DBConnections(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table IF NOT EXISTS login (id INTEGER primary key, email TEXT, pass TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("Drop table if EXISTS login");
    onCreate(db);
    }

    public void insertRowLogin(String email, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email",email);
        contentValues.put("pass",pass);
        db.insert("login", null, contentValues);
    }

    public HashMap<String, String> getAllrecord(){
        HashMap<String, String> hmap = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from login", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            hmap.put("email", res.getString(res.getColumnIndex("email")));
            hmap.put("pass", res.getString(res.getColumnIndex("pass")));
            res.moveToNext();
        }
        return hmap;
    }
}