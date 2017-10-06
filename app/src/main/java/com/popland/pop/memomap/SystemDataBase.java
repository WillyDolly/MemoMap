package com.popland.pop.memomap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by hai on 23/05/2017.
 */

public class SystemDataBase extends SQLiteOpenHelper {

    SystemDataBase(Context c, String name, SQLiteDatabase.CursorFactory cursor, int version){
        super(c,name,cursor,version);
    }

    public void queryData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public Cursor getData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery(sql,null);
    }

    public void Insert_Data(String so,String word){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO MaSy VALUES(null,?,?,null)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,so);
        statement.bindString(2,word);
        statement.executeInsert();
    }

    public void Update_Data(String so, String newword,byte[] anh){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE MaSy SET keyword =? , pic =? WHERE so =?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,newword);
        statement.bindBlob(2,anh);
        statement.bindString(3,so);
        statement.executeUpdateDelete();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
