package com.example.android.inventoryapp.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.Contract.Entry;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public DbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + Entry.TABLE_NAME + " ("
                + Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Entry.COLUMN_NAME + " TEXT NOT NULL, "
                + Entry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + Entry.COLUMN_PRICE + " TEXT NOT NULL, "
                + Entry.COLUMN_IMAGE + " BLOB NOT NULL, "
                + Entry.COLUMN_SUP_NAME + " TEXT, "
                + Entry.COLUMN_SUP_EMAIL + " TEXT);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
