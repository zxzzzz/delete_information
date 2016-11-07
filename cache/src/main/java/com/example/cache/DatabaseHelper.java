package com.example.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zx on 16-9-19.
 */
//将sharedPreference文件保存在数据库里
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name,int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
