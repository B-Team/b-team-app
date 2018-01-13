package com.b_team.b_team_app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class BookEditActivity extends Activity implements OnClickListener{

    private Button save, delete, cancel;
    private String mode;
    private EditText title, author, isbn, publisher, nPages, nVolume, genre, ownership;
    private String id;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
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

                // insert a record
                if(mode.trim().equalsIgnoreCase("add")){
                    ContentValues values_book = new ContentValues();
                    values_book.put(BooksTable.KEY_TITLE, mytitle);
                    values_book.put(BooksTable.VIEWKEY_AUTHOR, myauthor);
                    values_book.put(BooksTable.KEY_ISBN, myisbn);
                    values_book.put(BooksTable.VIEWKEY_PUBLISHER, mypublisher);
                    values_book.put(BooksTable.VIEWKEY_GENRE, mygenre);
                    values_book.put(BooksTable.KEY_NPAGES, mynPages);
                    values_book.put(BooksTable.KEY_NVOLUME, mynVolume);
                    values_book.put(BooksTable.KEY_OWNERSHIP, myownership);

                    getContentResolver().insert(BookProvider.URI_BOOKS, values_book);
                }
                // update a record
                else {
                    finish();
                }
                finish();
                break;

            case R.id.delete:
                // delete a record
                Uri uri = Uri.parse(BookProvider.URI_BOOKS + "/" + id);
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
                BooksTable.VIEWKEY_TITLE,
                BooksTable.VIEWKEY_AUTHOR,
                BooksTable.VIEWKEY_ISBN,
                BooksTable.VIEWKEY_PUBLISHER,
                BooksTable.VIEWKEY_NPAGES,
                BooksTable.VIEWKEY_NVOLUME,
                BooksTable.VIEWKEY_GENRE,
                BooksTable.VIEWKEY_OWNERSHIP};
        Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, "/" + id);
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String myTitle = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_TITLE));
            String myAuthor = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_AUTHOR));
            String myIsbn = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_ISBN));
            String myPublisher = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_PUBLISHER));
            String myNPages = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_NPAGES));
            String myNVolume = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_NVOLUME));
            String myGenre = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_GENRE));
            String myOwnership = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_OWNERSHIP));
            title.setText(myTitle);
            author.setText(myAuthor);
            isbn.setText(myIsbn);
            publisher.setText(myPublisher);
            nPages.setText(myNPages);
            nVolume.setText(myNVolume);
            genre.setText(myGenre);
            ownership.setText(myOwnership);

            cursor.close();
        }


    }

}
