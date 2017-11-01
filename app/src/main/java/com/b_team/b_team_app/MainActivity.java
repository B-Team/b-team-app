package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayListView();

        /*Contextual action bar allows for multiple selections by touching and holding.
        Not bugged anymore, but still very poorly integrated. The tapping of books should not
        open the BookEditActivity but instead fold out all information about the book.
        -Moritz
         */
        initializeContextualActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookEdit = new Intent(getBaseContext(), BookEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                bookEdit.putExtras(bundle);
                startActivity(bookEdit);
            }
        });

        //Setting up autocomplete for authors already used once
        String[] existingAuthors;
    }

    @Override
    protected void onResume() {
        super.onResume();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void displayListView() {


        // The desired columns to be bound
        String[] columns = new String[] {
                BooksTable.KEY_TITLE,
                BooksTable.KEY_AUTHOR,
              //BooksTable.KEY_ISBN,
              //BooksTable.KEY_PUBLISHER,
              //BooksTable.KEY_PICTURES,
              //BooksTable.KEY_NPAGES,
              //BooksTable.KEY_NVOLUME,
              //BooksTable.KEY_GENRE,
              //BooksTable.KEY_SHORTDESCRIPTION,
              //BooksTable.KEY_LONGDESCRIPTION,
              //BooksTable.KEY_REVIEW,
              //BooksTable.KEY_PRICE,
              //BooksTable.KEY_OWNERSHIP,
              //BooksTable.KEY_LASTEDIT
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.textViewTitleValue,
                R.id.textViewAuthorValue
        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(this, R.layout.book_info, null, columns, to, 0);

        // get reference to the ListView
        ListView listView = (ListView) findViewById(R.id.bookList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                /*// display the selected country
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow(CountriesDb.KEY_CODE));
                Toast.makeText(getApplicationContext(),
                        countryCode, Toast.LENGTH_SHORT).show();
                */
                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_ID));

                // starts a new Intent to update/delete a Country
                // pass in row Id to create the Content URI for a single row
                Intent bookEdit = new Intent(getBaseContext(), BookEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                bookEdit.putExtras(bundle);
                startActivity(bookEdit);

            }
        });

    }

    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BooksTable.KEY_ID,
                BooksTable.KEY_TITLE,
                BooksTable.KEY_AUTHOR,
                //BooksTable.KEY_ISBN,
                //BooksTable.KEY_PUBLISHER,
                //BooksTable.KEY_PICTURES,
                //BooksTable.KEY_NPAGES,
                //BooksTable.KEY_NVOLUME,
                //BooksTable.KEY_GENRE,
                //BooksTable.KEY_SHORTDESCRIPTION,
                //BooksTable.KEY_LONGDESCRIPTION,
                //BooksTable.KEY_REVIEW,
                //BooksTable.KEY_PRICE,
                //BooksTable.KEY_OWNERSHIP,
                //BooksTable.KEY_LASTEDIT
                };
        CursorLoader cursorLoader = new CursorLoader(this,
                BookProvider.CONTENT_URI, projection, null, null, null);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initializeContextualActionBar() {

        final ListView bookListView = (ListView) findViewById(R.id.bookList);
        bookListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        bookListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;

            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }

            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedBooksPositions = bookListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedBooksPositions.size(); i++) {
                            boolean isChecked = touchedBooksPositions.valueAt(i);
                            if (isChecked) {
                                int position = touchedBooksPositions.keyAt(i);
                                Cursor cursor = (Cursor) bookListView.getItemAtPosition(position);
                                String id = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_ID));

                                Uri uri = Uri.parse(BookProvider.CONTENT_URI + "/" + id);
                                getContentResolver().delete(uri, null, null);
                            }
                        }
                        restartLoader();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        for (int i = 0; i < touchedBooksPositions.size(); i++) {
                            boolean isChecked = touchedBooksPositions.valueAt(i);
                            if (isChecked) {
                                int position = touchedBooksPositions.keyAt(i);
                                Cursor cursor = (Cursor) bookListView.getItemAtPosition(position);
                                String rowId = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.KEY_ID));

                                // starts a new Intent to update the book
                                // pass in row Id to create the Content URI for a single row
                                Intent bookEdit = new Intent(getBaseContext(), BookEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("mode", "update");
                                bundle.putString("rowId", rowId);
                                bookEdit.putExtras(bundle);
                                startActivity(bookEdit);
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }
}
