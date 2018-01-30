package com.b_team.b_team_app;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WishlistTable {
    public static final String KEY_ID = "_id";
    public static final String KEY_BOOK_ID = "book_id";

    public static final String VIEWKEY_ID = KEY_ID;
    public static final String VIEWKEY_BOOK_TITLE = "book_title";
    public static final String VIEWKEY_BOOK_AUTHOR = "book_author";

    private static final String LOG_TAG = "WishlistTable";
    public static final String TABLE_NAME = "wishlist";
    public static final String VIEW_NAME = "view_wishlist";

    private static final String CREATE_WISHLIST_VIEW =
            "CREATE VIEW if not exists " + VIEW_NAME + " AS SELECT " +
                    TABLE_NAME + "." + KEY_ID + " AS " + VIEWKEY_ID + "," +
                    BooksTable.VIEW_NAME + "." + BooksTable.VIEWKEY_TITLE + " AS " + VIEWKEY_BOOK_TITLE + "," +
                    BooksTable.VIEW_NAME + "." + BooksTable.VIEWKEY_AUTHOR + " AS " + VIEWKEY_BOOK_AUTHOR +
                    " FROM " + TABLE_NAME +
                    " INNER JOIN " + BooksTable.VIEW_NAME + " ON " + TABLE_NAME + "." + KEY_BOOK_ID + " = " + BooksTable.VIEW_NAME + "." + BooksTable.VIEWKEY_ID +
                    ";";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    KEY_BOOK_ID + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
        db.execSQL(CREATE_WISHLIST_VIEW);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
