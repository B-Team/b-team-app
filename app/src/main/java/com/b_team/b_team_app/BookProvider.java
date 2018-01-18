package com.b_team.b_team_app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

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
    private static final int BOOK_ID = 1;
    private static final int BOOKS_SEARCH_ALL = 2;
    private static final int BOOKS_SEARCH_FILTER = 3;
    private static final int BOOKS_SEARCH_FILTER_FIELD = 4;
    private static final int AUTHOR = 5;
    private static final int AUTHOR_ID = 6;
    private static final int AUTHORS_SEARCH_ALL = 7;
    private static final int AUTHORS_SEARCH_FILTER = 8;
    private static final int AUTHORS_SEARCH_FILTER_FIELD = 9;
    private static final int PUBLISHER = 10;
    private static final int PUBLISHER_ID = 11;
    private static final int PUBLISHERS_SEARCH_ALL = 12;
    private static final int PUBLISHERS_SEARCH_FILTER = 13;
    private static final int PUBLISHERS_SEARCH_FILTER_FIELD = 14;
    private static final int GENRE = 15;
    private static final int GENRE_ID = 16;
    private static final int GENRES_SEARCH_ALL = 17;
    private static final int GENRES_SEARCH_FILTER = 18;
    private static final int GENRES_SEARCH_FILTER_FIELD = 19;
    private static final int WISH = 20;
    private static final int WISH_ID = 21;
    private static final int WISHLIST_SEARCH_ALL = 22;
    private static final int WISHLIST_SEARCH_FILTER = 23;
    private static final int WISHLIST_SEARCH_FILTER_FIELD = 24;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, "books", BOOK);
        uriMatcher.addURI(AUTHORITY, "books/#", BOOK_ID);
        uriMatcher.addURI(AUTHORITY, "books/search", BOOKS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "books/search/*", BOOKS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "books/search/*/*", BOOKS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "authors", AUTHOR);
        uriMatcher.addURI(AUTHORITY, "authors/#", AUTHOR_ID);
        uriMatcher.addURI(AUTHORITY, "authors/search", AUTHORS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "authors/search/*", AUTHORS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "authors/search/*/*", AUTHORS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "publishers", PUBLISHER);
        uriMatcher.addURI(AUTHORITY, "publishers/#", PUBLISHER_ID);
        uriMatcher.addURI(AUTHORITY, "publishers/search", PUBLISHERS_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "publishers/search/*", PUBLISHERS_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "publishers/search/*/*", PUBLISHERS_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "genres", GENRE);
        uriMatcher.addURI(AUTHORITY, "genres/#", GENRE_ID);
        uriMatcher.addURI(AUTHORITY, "genres/search", GENRES_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "genres/search/*", GENRES_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "genres/search/*/*", GENRES_SEARCH_FILTER_FIELD);
        uriMatcher.addURI(AUTHORITY, "wishlist", WISH);
        uriMatcher.addURI(AUTHORITY, "wishlist/#", WISH_ID);
        uriMatcher.addURI(AUTHORITY, "wishlist/search", WISHLIST_SEARCH_ALL);
        uriMatcher.addURI(AUTHORITY, "wishlist/search/*", WISHLIST_SEARCH_FILTER);
        uriMatcher.addURI(AUTHORITY, "wishlist/search/*/*", WISHLIST_SEARCH_FILTER_FIELD);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());

        //This is not how it is supposed to be done.
        //The onCreate() method is normally called when the first instance of the db is created through getWritableDatabase().
        //For some reason only the books table gets created though, so this is just a temporary workaround 'til I have that fixed.
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        return false;
    }

    @Override
    public String getType(Uri uri) {
        //We don't need this function in our app as of yet and I don't want to waste time implementing it.
        //But normally it should be implemented as third party users of this provider might need it.
        throw new UnsupportedOperationException("getType is not implemented :(");
    }

    //TODO: Escape every text input

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int id;
        switch (uriMatcher.match(uri)) {
            case BOOK:
                //Insert or get id of author, publisher and genre
                int author_id = insertAuthorIfNotExists((String) values.get(BooksTable.VIEWKEY_AUTHOR));
                values.remove(BooksTable.VIEWKEY_AUTHOR);
                int publisher_id = insertPublisherIfNotExists((String) values.get(BooksTable.VIEWKEY_PUBLISHER));
                values.remove(BooksTable.VIEWKEY_PUBLISHER);
                int genre_id = insertGenreIfNotExists((String) values.get(BooksTable.VIEWKEY_GENRE));
                values.remove(BooksTable.VIEWKEY_GENRE);

                //Add id of publisher to book content values
                values.put(BooksTable.KEY_PUBLISHER_ID, publisher_id);

                //Insert book
                id = (int) db.insert(BooksTable.TABLE_NAME, null, values);

                //Connect book and author via insert in booksauthors table
                ContentValues values_booksauthors = new ContentValues();
                values_booksauthors.put(BooksAuthorsTable.KEY_BOOK_ID, id);
                values_booksauthors.put(BooksAuthorsTable.KEY_AUTHOR_ID, author_id);
                db.insert(BooksAuthorsTable.TABLE_NAME, null, values_booksauthors);

                //Connect book and genre via insert in booksgenres table
                ContentValues values_booksgenres = new ContentValues();
                values_booksgenres.put(BooksGenresTable.KEY_BOOK_ID, id);
                values_booksgenres.put(BooksGenresTable.KEY_GENRE_ID, genre_id);
                db.insert(BooksGenresTable.TABLE_NAME, null, values_booksgenres);
                break;
            case AUTHOR:
                id = (int) db.insert(AuthorsTable.TABLE_NAME, null, values);
                break;
            case PUBLISHER:
                id = (int) db.insert(PublishersTable.TABLE_NAME, null, values);
                break;
            case GENRE:
                id = (int) db.insert(GenresTable.TABLE_NAME, null, values);
                break;
            case WISH:
                id = (int) db.insert(WishlistTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri + "/" + id);
    }

    private int insertAuthorIfNotExists(String name) {
        Cursor cursor = query(Uri.withAppendedPath(URI_AUTHORS,PATH_SEARCH_FILTER + AuthorsTable.KEY_NAME + "/" + name), new String[]{AuthorsTable.KEY_ID, AuthorsTable.KEY_NBOOKS}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(AuthorsTable.KEY_NAME, name);
            //Set book count for this author to one
            values.put(AuthorsTable.KEY_NBOOKS, 1);
            return Integer.parseInt(insert(URI_AUTHORS, values).getLastPathSegment());
        } else {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(AuthorsTable.KEY_ID));
            ContentValues values = new ContentValues();
            //Increase book count for this author by one
            values.put(AuthorsTable.KEY_NBOOKS, cursor.getInt(cursor.getColumnIndexOrThrow(AuthorsTable.KEY_NBOOKS)) + 1);
            update(Uri.withAppendedPath(URI_AUTHORS, "/" + id), values, null, null);
            return id;
        }
    }

    private int insertGenreIfNotExists(String name) {
        Cursor cursor = query(Uri.withAppendedPath(URI_GENRES,PATH_SEARCH_FILTER + GenresTable.KEY_NAME + "/" + name), new String[]{GenresTable.KEY_ID, GenresTable.KEY_NBOOKS}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(GenresTable.KEY_NAME, name);
            //Set book count for this genre to one
            values.put(AuthorsTable.KEY_NBOOKS, 1);
            return Integer.parseInt(insert(URI_GENRES, values).getLastPathSegment());
        } else {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(GenresTable.KEY_ID));
            ContentValues values = new ContentValues();
            //Increase book count for this genre by one
            values.put(GenresTable.KEY_NBOOKS, cursor.getInt(cursor.getColumnIndexOrThrow(GenresTable.KEY_NBOOKS)) + 1);
            update(Uri.withAppendedPath(URI_GENRES, "/" + id), values, null, null);
            return id;
        }
    }

    private int insertPublisherIfNotExists(String name) {
        Cursor cursor = query(Uri.withAppendedPath(URI_PUBLISHERS,PATH_SEARCH_FILTER + PublishersTable.KEY_NAME + "/" + name), new String[]{PublishersTable.KEY_ID, PublishersTable.KEY_NBOOKS}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(PublishersTable.KEY_NAME, name);
            //Set book count for this publisher to one
            values.put(PublishersTable.KEY_NBOOKS, 1);
            return Integer.parseInt(insert(URI_PUBLISHERS, values).getLastPathSegment());
        } else {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(PublishersTable.KEY_ID));
            ContentValues values = new ContentValues();
            //Increase book count for this genre by one
            values.put(GenresTable.KEY_NBOOKS, cursor.getInt(cursor.getColumnIndexOrThrow(PublishersTable.KEY_NBOOKS)) + 1);
            update(Uri.withAppendedPath(URI_PUBLISHERS, "/" + id), values, null, null);
            return id;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case BOOK_ID:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(BooksTable.VIEWKEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case BOOKS_SEARCH_ALL:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                break;
            case BOOKS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case BOOKS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(BooksTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(2) + " LIKE '%" + uri.getPathSegments().get(3) + "%'");
                break;
            case AUTHOR_ID:
                queryBuilder.setTables(AuthorsTable.TABLE_NAME);
                queryBuilder.appendWhere(AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case AUTHORS_SEARCH_ALL:
                queryBuilder.setTables(AuthorsTable.TABLE_NAME);
                break;
            case AUTHORS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case AUTHORS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(AuthorsTable.TABLE_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(2) + " LIKE '%" + uri.getPathSegments().get(3) + "%'");
                break;
            case PUBLISHER_ID:
                queryBuilder.setTables(PublishersTable.TABLE_NAME);
                queryBuilder.appendWhere(PublishersTable.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case PUBLISHERS_SEARCH_ALL:
                queryBuilder.setTables(PublishersTable.TABLE_NAME);
                break;
            case PUBLISHERS_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case PUBLISHERS_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(PublishersTable.TABLE_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(2) + " LIKE '%" + uri.getPathSegments().get(3) + "%'");
                break;
            case GENRE_ID:
                queryBuilder.setTables(GenresTable.TABLE_NAME);
                queryBuilder.appendWhere(GenresTable.KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case GENRES_SEARCH_ALL:
                queryBuilder.setTables(GenresTable.TABLE_NAME);
                break;
            case GENRES_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case GENRES_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(GenresTable.TABLE_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(2) + " LIKE '%" + uri.getPathSegments().get(3) + "%'");
                break;
            case WISH_ID:
                queryBuilder.setTables(WishlistTable.VIEW_NAME);
                queryBuilder.appendWhere(WishlistTable.VIEWKEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case WISHLIST_SEARCH_ALL:
                queryBuilder.setTables(WishlistTable.VIEW_NAME);
                break;
            case WISHLIST_SEARCH_FILTER:
                throw new UnsupportedOperationException("filter in all fields is not implemented");
            case WISHLIST_SEARCH_FILTER_FIELD:
                queryBuilder.setTables(WishlistTable.VIEW_NAME);
                queryBuilder.appendWhere(uri.getPathSegments().get(2) + " LIKE '%" + uri.getPathSegments().get(3) + "%'");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (selection != null) {
            Log.d("Selection", "not null");
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case BOOK_ID:
                //TODO: Proper updating of connected tables
                //TODO: Open dialog for user to select: keep or delete old field if in connected table (e.g. author)
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(BooksTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AUTHOR_ID:
                selection = AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(AuthorsTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PUBLISHER_ID:
                selection = PublishersTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(PublishersTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GENRE_ID:
                selection = GenresTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(GenresTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WISH_ID:
                selection = WishlistTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.update(WishlistTable.TABLE_NAME, values, selection, selectionArgs);
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
            case BOOK_ID:
                selection = BooksTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(BooksTable.TABLE_NAME, selection, selectionArgs);
                break;
            case AUTHOR_ID:
                selection = AuthorsTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(AuthorsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case PUBLISHER_ID:
                selection = PublishersTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(PublishersTable.TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE_ID:
                selection = GenresTable.KEY_ID + "=" + uri.getPathSegments().get(1);
                db.delete(GenresTable.TABLE_NAME, selection, selectionArgs);
                break;
            case WISH_ID:
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
