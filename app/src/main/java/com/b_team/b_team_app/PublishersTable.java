package com.b_team.b_team_app;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PublishersTable {
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_NBOOKS = "nbooks";

    private static final String LOG_TAG = "PublishersTable";
    public static final String TABLE_NAME = "publishers";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + "," +
                    KEY_NBOOKS + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
