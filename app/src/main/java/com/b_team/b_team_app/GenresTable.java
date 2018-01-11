package com.b_team.b_team_app;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GenresTable {
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    private static final String LOG_TAG = "GenresTable";
    public static final String TABLE_NAME = "genres";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
