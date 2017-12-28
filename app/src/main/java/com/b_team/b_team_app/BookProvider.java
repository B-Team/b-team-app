package com.b_team.b_team_app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BookProvider extends ContentProvider{

    private DatabaseHelper dbHelper;

    private static final int ALL_BOOKS = 1;
    private static final int SINGLE_BOOK = 2;
    private static final int ALL_AUTHORS = 3;
    private static final int SEARCH_BOOKS_GROUP = 4;
    private static final int SEARCH_BOOKS_GROUP_FILTER = 5;
    private static final int SEARCH_BOOKS_FILTER = 6;
    private static final int SEARCH_BOOKS_FILTER_EMPTY= 7;
    //Add "id" of aliases here

    private static final String AUTHORITY = "com.b_team.bookprovider";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/books");

    public static final Uri URI_SEARCH_BOOKS_FILTER =
            Uri.parse("content://" + AUTHORITY + "/booksearch/");

    public static final Uri AUTHORS_URI =
            Uri.parse("content://" + AUTHORITY + "/authors");

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "books", ALL_BOOKS);
        uriMatcher.addURI(AUTHORITY, "books/#", SINGLE_BOOK);
        uriMatcher.addURI(AUTHORITY, "authors", ALL_AUTHORS);
        //Search for all Books grouped by field given in *
        //* must be the correct KEY from BooksTable
        uriMatcher.addURI(AUTHORITY, "booksearchgroup/*", SEARCH_BOOKS_GROUP);
        //Like booksearch but with an additional filter for the value of the field
        uriMatcher.addURI(AUTHORITY, "booksearchgroup/*/*", SEARCH_BOOKS_GROUP_FILTER);
        //Search for all Books that match the given string */* in the given field *
        //e.g booksearch/BooksTable.KEY_AUTHOR/searchtext
        uriMatcher.addURI(AUTHORITY, "booksearch/*/*", SEARCH_BOOKS_FILTER);
        //same as above but for an empty search text
        uriMatcher.addURI(AUTHORITY, "booksearch/*", SEARCH_BOOKS_FILTER_EMPTY);
        //Add URI aliases here
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                return "vnd.android.cursor.dir/vnd.com.b_team.contentprovider.books";
            case SINGLE_BOOK:
                return "vnd.android.cursor.item/vnd.com.b_team.contentprovider.books";
            case SEARCH_BOOKS_FILTER:
                return "vnd.android.cursor.dir/vnd.com.b_team.contentprovider.books";
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                //do nothing
                break;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long id = db.insert(BooksTable.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" + id);
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(BooksTable.TABLE_NAME);
        String groupBy = null;
        //selection is used if matched uri doesnt provide a different one
        String WHERE = selection;

        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                //do nothing
                break;
            case SINGLE_BOOK:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(BooksTable.KEY_ID + "=" + id);
                break;
            case ALL_AUTHORS:
                groupBy = BooksTable.KEY_AUTHOR;
                break;
            case SEARCH_BOOKS_GROUP:
                groupBy = uri.getPathSegments().get(1);
                break;
            case SEARCH_BOOKS_GROUP_FILTER:
                WHERE = uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'";
                groupBy = uri.getPathSegments().get(1);
                break;
            case SEARCH_BOOKS_FILTER:
                WHERE = uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'";
                break;
            case SEARCH_BOOKS_FILTER_EMPTY:
                return null;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, WHERE,
                selectionArgs, groupBy, null, sortOrder);
        return cursor;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                //do nothing
                break;
            case SINGLE_BOOK:
                String id = uri.getPathSegments().get(1);
                selection = BooksTable.KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Handle deletion of pictures here by getting the path from the table
        int deleteCount = db.delete(BooksTable.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                //do nothing
                break;
            case SINGLE_BOOK:
                String id = uri.getPathSegments().get(1);
                selection = BooksTable.KEY_ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Handle update and potential deletion of pictures here by getting the path from the table and comparing for a change
        int updateCount = db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

}
