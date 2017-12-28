package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ImageButton bUp, bClear;
    private EditText etSearch;

    private String searchText;

    //Ids of the loaders used for the different search categories
    private static final int NO_SEARCH = 0;
    private static final int LID_BOOKS_TITLE = 1;

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bUp = (ImageButton) findViewById(R.id.Button_Up);
        bClear = (ImageButton) findViewById(R.id.Button_Clear);
        etSearch = (EditText) findViewById(R.id.editText_Search);
        //set button click listeners
        bUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpClicked();
            }
        });
        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClicked();
            }
        });
        //set listener for text changed
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onSearchTextChanged();
            }
        });
        displayListView();
        //Open keyboard
        //TODO: Make keyboard open and close appropriately
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void displayListView() {
        String[] columns = new String[] {
                BooksTable.KEY_TITLE,
                BooksTable.KEY_AUTHOR
        };

        int[] to = new int[] {
                R.id.itemName,
                R.id.itemInfo
        };

        dataAdapter = new SimpleCursorAdapter(this, R.layout.search_result_item, null, columns, to, 0);

        ListView listView = (ListView) findViewById(R.id.searchResultList);

        listView.setAdapter(dataAdapter);

        //Intitialise a loader with an empty cursor
        getLoaderManager().initLoader(NO_SEARCH, null, this);
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null;
        Uri uri = null;
        boolean errorId = false;

        switch (id) {
            case 0:
                return null;
            case LID_BOOKS_TITLE:
                uri = Uri.parse(BookProvider.URI_SEARCH_BOOKS_FILTER + BooksTable.KEY_TITLE + "/" + searchText);
                projection = new String[]{BooksTable.KEY_ID, BooksTable.KEY_TITLE, BooksTable.KEY_AUTHOR};
                break;
            default:
                errorId = true;
                Log.e("onCreateLoader()", "no matching id");
        }
        if (!errorId) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    uri, projection, null, null, null);
            return cursorLoader;
        } else return null;
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

    /**
     * Event: The up button was clicked
     */
    private void onUpClicked() {
        //TODO: Add appropriate up navigation
        finish();
    }

    /**
     * Event: The clear button was clicked
     */
    private void onClearClicked() {
        etSearch.setText("");
    }

    /**
     * Event: The search text changed
     */
    private void onSearchTextChanged() {
        searchText = etSearch.getText().toString();
        search(searchText, null);
    }

    /**
     * Searches all available data
     *
     * @param searchtext    the user input that filters the search
     * @param searchContext the context the search is in
     * @return              the result of the search
     * @see                 SearchResult
     * @see                 SearchContext
     */
    private SearchResult search(String searchtext, @Nullable SearchContext searchContext) {
        SearchResult searchResult = new SearchResult(searchtext, "Books", "Authors");

        //start search for matches in the title of the Books
        if(getLoaderManager().getLoader(LID_BOOKS_TITLE) != null){
            getLoaderManager().restartLoader(LID_BOOKS_TITLE, null, this);
        } else {
            getLoaderManager().initLoader(LID_BOOKS_TITLE, null, this);
        }

        return  searchResult;
    }
}
