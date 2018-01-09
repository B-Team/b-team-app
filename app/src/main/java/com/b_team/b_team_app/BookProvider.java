package com.b_team.b_team_app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BookProvider extends ContentProvider{

    private DatabaseHelper dbHelper;

    private static final int ALL_BOOKS = 1;
    private static final int SINGLE_BOOK = 2;
    private static final int ALL_AUTHORS = 3;
    private static final int SINGLE_AUTHOR = 4;
    private static final int SEARCH_BOOKS_GROUP = 5;
    private static final int SEARCH_BOOKS_GROUP_FILTER = 6;
    private static final int SEARCH_BOOKS_FILTER = 7;
    private static final int SEARCH_BOOKS_FILTER_EMPTY= 8;

    private static final int BOOKS_AUTHORS = 9;
    //Add "id" of aliases here

    private static final String AUTHORITY = "com.b_team.bookprovider";

    public static final Uri URI_BOOKS =
            Uri.parse("content://" + AUTHORITY + "/books");

    public static final Uri URI_AUTHORS =
            Uri.parse("content://" + AUTHORITY + "/authors");

    public static final Uri URI_BOOKS_AUTHORS =
            Uri.parse("content://" + AUTHORITY + "/booksauthors");

    public static final Uri URI_SEARCH_BOOKS_FILTER =
            Uri.parse("content://" + AUTHORITY + "/booksearch/");

    public static final Uri URI_SEARCH_BOOKS_GROUP_FILTER =
            Uri.parse("content://" + AUTHORITY + "/booksearchgroup/");

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "books", ALL_BOOKS);
        uriMatcher.addURI(AUTHORITY, "books/#", SINGLE_BOOK);
        uriMatcher.addURI(AUTHORITY, "authors", ALL_AUTHORS);
        uriMatcher.addURI(AUTHORITY, "authors/#", SINGLE_AUTHOR);
        uriMatcher.addURI(AUTHORITY, "booksauthors", BOOKS_AUTHORS);
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
        dbHelper.onCreate(dbHelper.getWritableDatabase());
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

        long id;
        Uri returnUri;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                id = db.insert(BooksTable.TABLE_NAME, null, values);
                returnUri = URI_BOOKS;
                Log.d("Insert", returnUri.toString() + id);
                break;
            case ALL_AUTHORS:
                id = db.insert(AuthorsTable.TABLE_NAME, null, values);
                returnUri = URI_AUTHORS;
                Log.d("Insert", returnUri.toString() + id);
                break;
            case BOOKS_AUTHORS:
                id = db.insert(BooksAuthorsTable.TABLE_NAME, null, values);
                returnUri = URI_BOOKS_AUTHORS;
                Log.d("Insert", returnUri.toString() + id);
                break;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(returnUri + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String groupBy = null;
        //selection is used if matched uri doesnt provide a different one
        String WHERE = selection;

        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                queryBuilder.setTables(
                        BooksAuthorsTable.TABLE_NAME +
                                " INNER JOIN " +
                                BooksTable.TABLE_NAME +
                                " ON " +
                                BooksAuthorsTable.KEY_BOOK_ID + "=" + BooksTable.KEY_ID +
                                " INNER JOIN " +
                                AuthorsTable.TABLE_NAME +
                                " ON " +
                                BooksAuthorsTable.KEY_AUTHOR_ID + "=" + AuthorsTable.KEY_ID
                );
                break;
            case SINGLE_BOOK:
                queryBuilder.setTables(BooksTable.TABLE_NAME);
                queryBuilder.appendWhere(BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case ALL_AUTHORS:
                queryBuilder.setTables(AuthorsTable.TABLE_NAME);
                break;
            case SINGLE_AUTHOR:
                queryBuilder.setTables(AuthorsTable.TABLE_NAME);
                queryBuilder.appendWhere(AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case SEARCH_BOOKS_GROUP:
                queryBuilder.setTables(BooksTable.TABLE_NAME);
                groupBy = uri.getPathSegments().get(1);
                break;
            case SEARCH_BOOKS_GROUP_FILTER:
                queryBuilder.setTables(BooksTable.TABLE_NAME);
                WHERE = uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'";
                groupBy = uri.getPathSegments().get(1);
                break;
            case SEARCH_BOOKS_FILTER:
                queryBuilder.setTables(BooksTable.TABLE_NAME);
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
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            case ALL_AUTHORS:
                break;
            case SINGLE_AUTHOR:
                selection = AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1)
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
        //TODO: Add conditional checks for usage of deleted data and update all now invalid fields
        switch (uriMatcher.match(uri)) {
            case ALL_BOOKS:
                //do nothing
                break;
            case SINGLE_BOOK:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            case ALL_AUTHORS:
                //do nothing
                break;
            case SINGLE_AUTHOR:
                selection = AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
                break;
            //Additional alias code here
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

}
