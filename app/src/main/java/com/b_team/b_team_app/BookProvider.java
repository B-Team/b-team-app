package com.b_team.b_team_app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BookProvider extends ContentProvider{

    private DatabaseHelper dbHelper;

    public static final String AUTHORITY = "com.b_team.bookprovider";

    public static final Uri URI_BOOKS = Uri.parse("content://" + AUTHORITY + "/books");
    public static final Uri URI_AUTHORS = Uri.parse("content://" + AUTHORITY + "/authors");
    public static final Uri URI_PUBLISHERS = Uri.parse("content://" + AUTHORITY + "/publishers");
    public static final Uri URI_GENRES = Uri.parse("content://" + AUTHORITY + "/genres");
    public static final Uri URI_WISHLIST = Uri.parse("content://" + AUTHORITY + "/wishlist");

    public static String PATH_FIELD = "/";
    public static String PATH_SEARCH_ALL = "/search";
    public static String PATH_SEARCH_FILTER = "/search/";

    private static final int BOOK = 0;
    private static final int BOOKS_SEARCH_ALL = 1;
    private static final int BOOKS_SEARCH_FILTER = 3;
    private static final int BOOKS_SEARCH_FILTER_FIELD = 4;
    private static final int AUTHOR = 5;
    private static final int AUTHORS_SEARCH_ALL = 6;
    private static final int AUTHORS_SEARCH_FILTER = 7;
    private static final int AUTHORS_SEARCH_FILTER_FIELD = 8;
    private static final int PUBLISHER = 9;
    private static final int PUBLISHERS_SEARCH_ALL = 10;
    private static final int PUBLISHERS_SEARCH_FILTER = 11;
    private static final int PUBLISHERS_SEARCH_FILTER_FIELD = 12;
    private static final int GENRE = 13;
    private static final int GENRES_SEARCH_ALL = 14;
    private static final int GENRES_SEARCH_FILTER = 15;
    private static final int GENRES_SEARCH_FILTER_FIELD = 16;
    private static final int WISH = 17;
    private static final int WISHLIST_SEARCH_ALL = 18;
    private static final int WISHLIST_SEARCH_FILTER = 19;
    private static final int WISHLIST_SEARCH_FILTER_FIELD = 20;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, "books/#", BOOK);
        uriMatcher.addURI(AUTHORITY, "books/search", BOOKS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "books/search/*", BOOKS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "books/search/*/*", BOOKS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "books/#", AUTHOR);
        uriMatcher.addURI(AUTHORITY, "authors/search", AUTHORS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "authors/search/*", AUTHORS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "authors/search/*/*", AUTHORS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "publishers/#", PUBLISHER);
        uriMatcher.addURI(AUTHORITY, "publishers/search", PUBLISHERS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "publishers/search/*", PUBLISHERS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "publishers/search/*/*", PUBLISHERS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "genres/#", GENRE);
        uriMatcher.addURI(AUTHORITY, "genres/search", GENRES_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "genres/search/*", GENRES_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "genres/search/*/*", GENRES_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "wishlist/#", WISH);
        uriMatcher.addURI(AUTHORITY, "wishlist/search", WISHLIST_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "wishlist/search/*", WISHLIST_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "wishlist/search/*/*", WISHLIST_SEARCH_FILTER_FIELD);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        //We don't need this function in our app as of yet and I don't want to waste time implementing it.
        //But normally it should be implemented as third party users of this provider might need it.
        throw new UnsupportedOperationException("getType is not implemented :(");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id;

        switch (uriMatcher.match(uri)) {
            case BOOK:
                id = db.insert(BooksTable.TABLE_NAME, null, values);
                break;
            case AUTHOR:
                id = db.insert(AuthorsTable.TABLE_NAME, null, values);
                break;
            case PUBLISHER:
                id = db.insert(PublishersTable.TABLE_NAME, null, values);
                break;
            case GENRE:
                id = db.insert(GenresTable.TABLE_NAME, null, values);
                break;
            case WISH:
                id = db.insert(WishlistTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case BOOK:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                break;
            case BOOKS_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
            case BOOKS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case BOOKS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            case AUTHOR:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                break;
            case AUTHORS_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                break;
            case AUTHORS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case AUTHORS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            case PUBLISHER:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                break;
            case PUBLISHERS_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                break;
            case PUBLISHERS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case PUBLISHERS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            case GENRE:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                break;
            case GENRES_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                break;
            case GENRES_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case GENRES_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            case WISH:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                break;
            case WISHLIST_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                break;
            case WISHLIST_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case WISHLIST_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(1) + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOK:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AUTHOR:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PUBLISHER:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GENRE:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WISH:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //We ever only update one row at a time
        return 1;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO: Check for usage of deleted data and update all now invalid fields
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOK:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(BooksTable.TABLE_NAME, selection, selectionArgs);
                break;
            case AUTHOR:
                selection = AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(AuthorsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case PUBLISHER:
                selection = PublishersTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(PublishersTable.TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE:
                selection = GenresTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(GenresTable.TABLE_NAME, selection, selectionArgs);
                break;
            case WISH:
                selection = WishlistTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(WishlistTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //We only ever delete one row at a time
        return 1;
    }
}
