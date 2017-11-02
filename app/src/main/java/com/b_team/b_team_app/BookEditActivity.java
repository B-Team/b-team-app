package com.b_team.b_team_app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Toast;

public class BookEditActivity extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Button save, delete, cancel;
    private String mode;
    private EditText title, author, isbn, publisher, nPages, nVolume, genre, ownership;
    private String id;
    private AutoCompleteTextView authorView;
    private SimpleCursorAdapter dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_edit);

        // get the values passed to the activity from the calling activity
        // determine the mode - add, update or delete
        if (this.getIntent().getExtras() != null){
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // get references to the buttons and attach listeners
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        title = (EditText) findViewById(R.id.editText_title);
        author = (EditText) findViewById(R.id.editText_author);
        isbn = (EditText) findViewById(R.id.editText_isbn);
        publisher = (EditText) findViewById(R.id.editText_publisher);
        nPages = (EditText) findViewById(R.id.editText_nPages);
        nVolume = (EditText) findViewById(R.id.editText_nVolume);
        genre = (EditText) findViewById(R.id.editText_genre);
        ownership = (EditText) findViewById(R.id.editText_ownership);

        // if in add mode disable the delete option
        if(mode.trim().equalsIgnoreCase("add")){
            delete.setEnabled(false);
        }
        // get the rowId for the specific book
        else{
            Bundle bundle = this.getIntent().getExtras();
            id = bundle.getString("rowId");
            loadBookInfo();
        }

        // Create a SimpleCursorAdapter for the author field.
        dataAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line,
                null, new String[]{BooksTable.KEY_AUTHOR}, new int[]{android.R.id.text1}, 0);
        authorView = (AutoCompleteTextView) findViewById(R.id.editText_author);
        authorView.setAdapter(dataAdapter);

        getLoaderManager().initLoader(0, null, this);

        // Set an OnItemClickListener, to update the field when
        // a choice is made in the AutoCompleteTextView.
        authorView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {

                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the author from this row in the database.
                String selAuthor = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_AUTHOR));

                // Update the TextView
                authorView.setText(selAuthor);
            }
        });

        // Set the CursorToStringConverter, to provide the labels for the
        // choices to be displayed in the AutoCompleteTextView.
        dataAdapter.setCursorToStringConverter(new CursorToStringConverter() {
            public String convertToString(android.database.Cursor cursor) {
                // Get the label for this row out of the BooksTable.KEY_AUTHOR column
                final int columnIndex = cursor.getColumnIndexOrThrow(BooksTable.KEY_AUTHOR);
                final String str = cursor.getString(columnIndex);
                return str;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String[] projection = {
                BooksTable.KEY_ID,
                BooksTable.KEY_AUTHOR,
        };

        CursorLoader cursorLoader = new CursorLoader(this,
                BookProvider.AUTHORS_URI, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        dataAdapter.swapCursor(null);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.save:
                // get values from the spinner and the input text fields
                String mytitle = title.getText().toString();
                String myauthor = author.getText().toString();
                String myisbn = isbn.getText().toString();
                String mypublisher = publisher.getText().toString();
                String mynPages = nPages.getText().toString();
                String mynVolume = nVolume.getText().toString();
                String mygenre = genre.getText().toString();
                String myownership = ownership.getText().toString();

                // check for blanks
                if(mytitle.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER title", Toast.LENGTH_LONG).show();
                    return;
                }
                if(myauthor.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER author", Toast.LENGTH_LONG).show();
                    return;
                }
                if(myisbn.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER isbn", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mypublisher.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER publisher", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mynPages.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER nPages", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mynVolume.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER nVolume", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mygenre.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER genre", Toast.LENGTH_LONG).show();
                    return;
                }
                if(myownership.trim().equalsIgnoreCase("")){
                    Toast.makeText(getBaseContext(), "Please ENTER ownership", Toast.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(BooksTable.KEY_TITLE, mytitle);
                values.put(BooksTable.KEY_AUTHOR, myauthor);
                values.put(BooksTable.KEY_ISBN, myisbn);
                values.put(BooksTable.KEY_PUBLISHER, mypublisher);
                values.put(BooksTable.KEY_NPAGES, mynPages);
                values.put(BooksTable.KEY_NVOLUME, mynVolume);
                values.put(BooksTable.KEY_GENRE, mygenre);
                values.put(BooksTable.KEY_OWNERSHIP, myownership);

                // insert a record
                if(mode.trim().equalsIgnoreCase("add")){
                    getContentResolver().insert(BookProvider.CONTENT_URI, values);
                }
                // update a record
                else {
                    Uri uri = Uri.parse(BookProvider.CONTENT_URI + "/" + id);
                    getContentResolver().update(uri, values, null, null);
                }
                finish();
                break;

            case R.id.delete:
                // delete a record
                Uri uri = Uri.parse(BookProvider.CONTENT_URI + "/" + id);
                getContentResolver().delete(uri, null, null);
                finish();
                break;

            case R.id.cancel:
                // return to main activity
                finish();
                break;
        }
    }

    // based on the rowId get all information from the Content Provider
    // about that country
    private void loadBookInfo(){

        String[] projection = {
                BooksTable.KEY_TITLE,
                BooksTable.KEY_AUTHOR,
                BooksTable.KEY_ISBN,
                BooksTable.KEY_PUBLISHER,
                BooksTable.KEY_NPAGES,
                BooksTable.KEY_NVOLUME,
                BooksTable.KEY_GENRE,
                BooksTable.KEY_OWNERSHIP};
        Uri uri = Uri.parse(BookProvider.CONTENT_URI + "/" + id);
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String myTitle = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_TITLE));
            String myAuthor = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_AUTHOR));
            String myIsbn = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_ISBN));
            String myPublisher = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_PUBLISHER));
            String myNPages = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_NPAGES));
            String myNVolume = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_NVOLUME));
            String myGenre = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_GENRE));
            String myOwnership = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_OWNERSHIP));
            title.setText(myTitle);
            author.setText(myAuthor);
            isbn.setText(myIsbn);
            publisher.setText(myPublisher);
            nPages.setText(myNPages);
            nVolume.setText(myNVolume);
            genre.setText(myGenre);
            ownership.setText(myOwnership);
        }


    }

}
