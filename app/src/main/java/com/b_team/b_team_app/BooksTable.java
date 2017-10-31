package com.b_team.b_team_app;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * A static class that saves all SQL related strings that concern the library table.
 * It is used to get the correct names for rows. It also defines a SQL commands for
 * the {@link DatabaseHelper#onCreate(SQLiteDatabase) onCreate} and {@link DatabaseHelper#onUpgrade(SQLiteDatabase,int,int) onUpgrade}
 * methods of the {@link DatabaseHelper} class.
 */
public class BooksTable {

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_ISBN = "isbn";
    public static final String KEY_PUBLISHER = "publisher";
    public static final String KEY_PICTURES = "pictures";
    public static final String KEY_NPAGES = "nPages";
    public static final String KEY_NVOLUME = "nVolume";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_SHORTDESCRIPTION = "shortDescription";
    public static final String KEY_LONGDESCRIPTION = "longDescription";
    public static final String KEY_REVIEW = "review";
    public static final String KEY_PRICE = "price";
    public static final String KEY_OWNERSHIP = "ownership";
    public static final String KEY_LASTEDIT = "lastEdit";

    private static final String LOG_TAG = "BooksTable";
    public static final String TABLE_NAME = "books";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    KEY_TITLE + "," +
                    KEY_AUTHOR + "," +
                    KEY_ISBN + "," +
                    KEY_PUBLISHER + "," +
                    KEY_PICTURES + "," +
                    KEY_NPAGES + "," +
                    KEY_NVOLUME + "," +
                    KEY_GENRE + "," +
                    KEY_SHORTDESCRIPTION + "," +
                    KEY_LONGDESCRIPTION + "," +
                    KEY_REVIEW + "," +
                    KEY_PRICE + "," +
                    KEY_OWNERSHIP + "," +
                    KEY_LASTEDIT + ");";

    /**
     * Creates the books table by executing a sql command in the given database.
     *
     * @param db    the reference to the {@link android.database.sqlite.SQLiteDatabase SQLiteDatabase] in which the table is being created
     */
    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    /**
     * Drops the current data of the books table and calls {@link BooksTable#onCreate(SQLiteDatabase) onCreate} in this class.
     * This will destroy all old data. This should only be done during development and should later be replaced with
     * proper code for a safe upgrade.
     *
     * @param db            the reference to the {@link android.database.sqlite.SQLiteDatabase SQLiteDatabase] in which the table is being upgraded
     * @param oldVersion    the version of the databank prior to this upgrade
     * @param newVersion    the version of the databank after this upgrade
     */
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
