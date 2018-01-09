package com.b_team.b_team_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    final static String DATABASE_NAME = "BTeamAppDb.db";
    final static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        BooksTable.onCreate(db);
        AuthorsTable.onCreate(db);
        BooksAuthorsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        BooksTable.onUpgrade(db, oldVersion, newVersion);
        AuthorsTable.onUpgrade(db, oldVersion, newVersion);
        BooksAuthorsTable.onUpgrade(db, oldVersion, newVersion);
    }
}
