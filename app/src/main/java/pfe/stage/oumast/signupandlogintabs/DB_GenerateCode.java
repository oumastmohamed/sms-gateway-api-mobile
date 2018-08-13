package pfe.stage.oumast.signupandlogintabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by poste05 on 04/04/2018.
 */

public class DB_GenerateCode extends SQLiteOpenHelper {
    private static final String DbName = "codeAA.db";
    private static final int Version = 1;
    public DB_GenerateCode(Context context) {
        super(context, DbName, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table IF NOT EXISTS Code_ (id INTEGER primary key, code TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("Drop table if EXISTS Code_");
    onCreate(db);
    }

    public void insertCode(String code){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code",code);
        db.insert("Code_", null, contentValues);
    }

    public String getCode(){
        String code = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Code_", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            code = res.getString(res.getColumnIndex("code"));
            res.moveToNext();
        }
        return code;
    }
}
