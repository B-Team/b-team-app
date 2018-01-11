package com.b_team.b_team_app;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * A static class that saves all SQL related strings that concern the bookstable.
 * It is used to get the correct names for rows. It also defines SQL commands for
 * the {@link DatabaseHelper#onCreate(SQLiteDatabase) onCreate} and {@link DatabaseHelper#onUpgrade(SQLiteDatabase,int,int) onUpgrade}
 * methods of the {@link DatabaseHelper} class.
 */
public class BooksTable {

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ISBN = "isbn";
    public static final String KEY_PUBLISHER_ID = "publisher";
    public static final String KEY_PICTURES = "pictures";
    public static final String KEY_NPAGES = "nPages";
    public static final String KEY_NVOLUME = "nVolume";
    public static final String KEY_SHORTDESCRIPTION = "shortDescription";
    public static final String KEY_LONGDESCRIPTION = "longDescription";
    public static final String KEY_REVIEW = "review";
    public static final String KEY_PRICE = "price";
    public static final String KEY_OWNERSHIP = "ownership";
    public static final String KEY_LASTEDIT = "lastEdit";

    public static final String VIEWKEY_ID = KEY_ID;
    public static final String VIEWKEY_TITLE = KEY_TITLE;
    public static final String VIEWKEY_AUTHOR = "author";
    public static final String VIEWKEY_ISBN = KEY_ISBN;
    public static final String VIEWKEY_PUBLISHER = "publisher";
    public static final String VIEWKEY_PICTURES = KEY_PICTURES;
    public static final String VIEWKEY_NPAGES = KEY_NPAGES;
    public static final String VIEWKEY_NVOLUME = KEY_NVOLUME;
    public static final String VIEWKEY_GENRE = "genre";
    public static final String VIEWKEY_SHORTDESCRIPTION = KEY_SHORTDESCRIPTION;
    public static final String VIEWKEY_LONGDESCRIPTION = KEY_LONGDESCRIPTION;
    public static final String VIEWKEY_REVIEW = KEY_REVIEW;
    public static final String VIEWKEY_PRICE = KEY_PRICE;
    public static final String VIEWKEY_OWNERSHIP = KEY_OWNERSHIP;
    public static final String VIEWKEY_LASTEDIT = KEY_LASTEDIT;

    private static final String LOG_TAG = "BooksTable";
    public static final String TABLE_NAME = "books";
    public static final String VIEW_NAME = "view_book";

    private static final String CREATE_BOOK_VIEW =
            "CREATE VIEW if not exists " + VIEW_NAME + " AS SELECT " +
                    TABLE_NAME + "." + KEY_ID + "," +
                    TABLE_NAME + "." + KEY_TITLE + "," +
                    AuthorsTable.TABLE_NAME + "." + AuthorsTable.KEY_NAME + " AS " + VIEWKEY_AUTHOR + "," +
                    TABLE_NAME + "." + KEY_ISBN + "," +
                    PublishersTable.TABLE_NAME + "." + PublishersTable.KEY_NAME + " AS " + VIEWKEY_PUBLISHER + "," +
                    TABLE_NAME + "." + KEY_PICTURES + "," +
                    TABLE_NAME + "." + KEY_NPAGES + "," +
                    TABLE_NAME + "." + KEY_NVOLUME + "," +
                    GenresTable.TABLE_NAME + "." + GenresTable.KEY_NAME + " AS " + VIEWKEY_GENRE + "," +
                    TABLE_NAME + "." + KEY_SHORTDESCRIPTION + "," +
                    TABLE_NAME + "." + KEY_LONGDESCRIPTION + "," +
                    TABLE_NAME + "." + KEY_REVIEW + "," +
                    TABLE_NAME + "." + KEY_PRICE + "," +
                    TABLE_NAME + "." + KEY_OWNERSHIP + "," +
                    TABLE_NAME + "." + KEY_LASTEDIT +
                    " FROM " + TABLE_NAME +
                    " INNER JOIN " + BooksAuthorsTable.TABLE_NAME + " ON " + TABLE_NAME + "." + KEY_ID + " = " + BooksAuthorsTable.TABLE_NAME + "." + BooksAuthorsTable.KEY_BOOK_ID +
                    " INNER JOIN " + AuthorsTable.TABLE_NAME + " ON " + BooksAuthorsTable.TABLE_NAME + "." + BooksAuthorsTable.KEY_AUTHOR_ID + " = " + AuthorsTable.TABLE_NAME + "." + AuthorsTable.KEY_ID +
                    " INNER JOIN " + BooksGenresTable.TABLE_NAME + " ON " + TABLE_NAME + "." + KEY_ID + " = " + BooksGenresTable.TABLE_NAME + "." + BooksGenresTable.KEY_BOOK_ID +
                    " INNER JOIN " + GenresTable.TABLE_NAME + " ON " + BooksGenresTable.TABLE_NAME + "." + BooksGenresTable.KEY_GENRE_ID + " = " + GenresTable.TABLE_NAME + "." + GenresTable.KEY_ID +
                    " INNER JOIN " + PublishersTable.TABLE_NAME + " ON " + TABLE_NAME + "." + KEY_PUBLISHER_ID + " = " + PublishersTable.TABLE_NAME + "." + PublishersTable.KEY_ID +
                    ";";


    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                    KEY_ID + " integer PRIMARY KEY autoincrement," +
                    KEY_TITLE + "," +
                    KEY_ISBN + "," +
                    KEY_PUBLISHER_ID + "," +
                    KEY_PICTURES + "," +
                    KEY_NPAGES + "," +
                    KEY_NVOLUME + "," +
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
        Log.w(LOG_TAG, CREATE_BOOK_VIEW);
        db.execSQL(CREATE_BOOK_VIEW);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
