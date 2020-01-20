package com.frozyice.callToQueue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String PHONENUMBER = "phoneNumber";
    private static final String DB_NAME = "CALLTOQUEUE.DB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "queuePhonenumbers";
    private static final String _ID = "_id";
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PHONENUMBER + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable();
        onCreate(db);
    }

    public void dropTable() {
        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_NAME
        );
    }

    public long insert(String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(PHONENUMBER, phoneNumber);
        long id = db.insert(TABLE_NAME, null, values);
        return id;
    }


    public List<String> read() {

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        List<String> PhonenumberList = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {
                PhonenumberList.add(cursor.getString((cursor.getColumnIndex(PHONENUMBER))));
            }
        }

        return PhonenumberList;
    }

    public void deleteFirst() {
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, _ID, "1");
        if (cursor != null)
            cursor.moveToFirst();
        db.delete(TABLE_NAME, _ID + "=?", new String[]{String.valueOf(cursor.getLong(cursor.getColumnIndex(_ID)))});
    }

    public void deleteAll() {
        db.delete(TABLE_NAME, null, null);
    }

    public void open() {
        db = this.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

}
