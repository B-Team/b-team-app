package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchStartFragment.OnSearchStartFragmentInteractionListener, SearchInContextFragment.OnSearchInContextFragmentInteractionListener {

    private ImageButton bUp, bClear;
    private EditText etSearch;

    private String searchText;

    //The searchContext we are currently searching in
    private SearchContext currentSearchContext;
    //The fragment that should receive the next search result
    private SearchListener currentReceiver;

    //Ids of the loaders used for the different search categories
    private static final int NO_SEARCH = 0;
    private static final int LID_BOOKS_TITLE = 1;
    private static final int LID_AUTHORS = 2;

    private SeparatedListAdapter dataAdapter;
    private SimpleCursorAdapter titlesAdapter;
    private SimpleCursorAdapter authorsAdapter;

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

        if (savedInstanceState == null) {
            //Add the SearchStartFragment if we are not restoring an old state
            SearchStartFragment searchStartFragment = new SearchStartFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, searchStartFragment, "SearchStart")
                    .commit();

            currentSearchContext = null;
        } else {
            currentSearchContext = (SearchContext) savedInstanceState.getSerializable("currentSearchContext");
            //searchText = savedInstanceState.getString("searchText");
            //etSearch.setText(searchText);
        }
        //displayListView();
        //Open keyboard
        //TODO: Make keyboard open and close appropriately
        //InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.RESULT_HIDDEN);
    }

    //This gets called before the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("currentSearchContext", currentSearchContext);
        outState.putString("searchText", searchText);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSearchStartFragmentInteraction(SearchCategory item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        SearchContext searchContext = new SearchContext(item, "", item.isAlwaysSingular());
        transaction.replace(R.id.fragmentContainer, SearchInContextFragment.newInstance(searchContext));
        transaction.addToBackStack(null);

        transaction.commit();

        currentSearchContext = searchContext;
    }

    @Override
    public void onBookSelected(int bookId) {
        //TODO: Open book info page
    }

    @Override
    public void onNewContextSelected(SearchContext searchContext) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainer, SearchInContextFragment.newInstance(searchContext));
        transaction.addToBackStack(null);

        transaction.commit();

        currentSearchContext = searchContext;
    }

    /*private void displayListView() {
        String[] columns = new String[] {
                "name",
                "info"
        };

        int[] to = new int[] {
                R.id.itemName,
                R.id.itemInfo
        };

        titlesAdapter = new SimpleCursorAdapter(this, R.layout.search_result_item, null, columns, to, 0);
        authorsAdapter = new SimpleCursorAdapter(this, R.layout.search_result_item, null, columns, to, 0);

        dataAdapter = new SeparatedListAdapter(this);
        dataAdapter.addSection(getString(R.string.search_category_header_titles), titlesAdapter);
        dataAdapter.addSection(getString(R.string.search_category_header_authors), authorsAdapter);

        ListView listView = (ListView) findViewById(R.id.searchResultList);

        listView.setAdapter(dataAdapter);

        //Intitialise a loader with an empty cursor
        getLoaderManager().initLoader(NO_SEARCH, null, this);
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null;
        Uri uri = null;
        boolean errorId = false;

        switch (id) {
            case NO_SEARCH:
                return null;
            case LID_BOOKS_TITLE:
                uri = Uri.parse(BookProvider.URI_SEARCH_BOOKS_FILTER + BooksTable.KEY_TITLE + "/" + searchText);
                projection = new String[]{BooksTable.KEY_ID, BooksTable.KEY_TITLE + " AS name", AuthorsTable.KEY_NAME + " AS info"};
                break;
            case LID_AUTHORS:
                //uri = Uri.parse(BookProvider.URI_SEARCH_BOOKS_GROUP_FILTER + BooksTable.KEY_AUTHOR + "/" + searchText);
                //projection = new String[]{BooksTable.KEY_ID, BooksTable.KEY_AUTHOR + " AS name", BooksTable.KEY_TITLE + " AS info"};
                uri = BookProvider.URI_AUTHORS;
                projection = new String[]{AuthorsTable.KEY_ID, AuthorsTable.KEY_NAME + " AS name", AuthorsTable.KEY_NAME + " AS info"};
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

        currentReceiver.onSearchComplete(data);
        /*
        SimpleCursorAdapter cursorAdapter;
        switch (loader.getId()) {
            case LID_BOOKS_TITLE:
                cursorAdapter = (SimpleCursorAdapter) dataAdapter.getSection(getString(R.string.search_category_header_titles));
                cursorAdapter.swapCursor(data);
                return;
            case LID_AUTHORS:
                cursorAdapter = (SimpleCursorAdapter) dataAdapter.getSection(getString(R.string.search_category_header_authors));
                cursorAdapter.swapCursor(data);
                return;
            default:
                return;
        }
        */
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        currentReceiver.onSearchComplete(null);
        /*
        SimpleCursorAdapter cursorAdapter;
        switch (loader.getId()) {
            case LID_BOOKS_TITLE:
                cursorAdapter = (SimpleCursorAdapter) dataAdapter.getSection(getString(R.string.search_category_header_titles));
                cursorAdapter.swapCursor(null);
                return;
            case LID_AUTHORS:
                cursorAdapter = (SimpleCursorAdapter) dataAdapter.getSection(getString(R.string.search_category_header_authors));
                cursorAdapter.swapCursor(null);
                return;
            default:
                return;
        }
        */
    }

    /**
     * Event: The up button was clicked
     */
    private void onUpClicked() {
        //TODO: Add appropriate up navigation
        onBackPressed();
        //finish();
    }

    /**
     * Event: The clear button was clicked
     */
    private void onClearClicked() {
        etSearch.setText("");

        //This opens the book info page for testing
        //This should be removed once the database is connected
        startActivity(new Intent(getBaseContext(), BookInfoActivity.class));
    }

    /**
     * Event: The search text changed
     */
    private void onSearchTextChanged() {
        searchText = etSearch.getText().toString();

        //Show SearchStartFragment if empty and SearchInAllFragment otherwise
        if (searchText.length() == 0) {
            //Try to get the existing SearchStartFragment
            Fragment searchStartFragment = getSupportFragmentManager().findFragmentByTag("SearchStart");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //Create a new Fragment if no old one is there
            if (searchStartFragment == null) {
                searchStartFragment = new SearchStartFragment();
            }
            transaction.replace(R.id.fragmentContainer, searchStartFragment, "SearchStart");
            transaction.commit();
        } else {
            //Try to get the existing SearchInAllFragment
            Fragment searchAllFragment = getSupportFragmentManager().findFragmentByTag("SearchAll");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //Create a new Fragment if no old one is there
            if (searchAllFragment == null) {
                searchAllFragment = new SearchInAllFragment();
            }
            transaction.replace(R.id.fragmentContainer, searchAllFragment, "SearchAll");
            transaction.commit();

            //Start a search with the current search text
            //search(searchText, currentSearchContext);
        }
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
    private SearchResult search(String searchtext, SearchContext searchContext) {
        SearchResult searchResult = new SearchResult(searchtext, "Books", "Authors");

        switch (currentSearchContext.getSearchCategory().getId()) {
            case SearchCategory.CATEGORY_TITLES:
                if(getLoaderManager().getLoader(LID_BOOKS_TITLE) != null){
                    getLoaderManager().restartLoader(LID_BOOKS_TITLE, null, this);
                } else {
                    getLoaderManager().initLoader(LID_BOOKS_TITLE, null, this);
                }
                break;
            case SearchCategory.CATEGORY_AUTHORS:
                break;
            case SearchCategory.CATEGORY_PUBLISHERS:
                break;
            case SearchCategory.CATEGORY_GENRES:
                break;
            case SearchCategory.CATEGORY_WISHLIST:
                break;
            case SearchCategory.CATEGORY_RATINGS:
                break;
            case SearchCategory.CATEGORY_ALL:
                break;
            default:
                break;
        }

        /*
        //start search for matches in the title of the books
        if(getLoaderManager().getLoader(LID_BOOKS_TITLE) != null){
            getLoaderManager().restartLoader(LID_BOOKS_TITLE, null, this);
        } else {
            getLoaderManager().initLoader(LID_BOOKS_TITLE, null, this);
        }
        //start search for matches in the authors
        if(getLoaderManager().getLoader(LID_BOOKS_TITLE) != null){
            getLoaderManager().restartLoader(LID_BOOKS_TITLE, null, this);
        } else {
            getLoaderManager().initLoader(LID_BOOKS_TITLE, null, this);
        }
        */

        return  searchResult;
    }

    //This function gets called in onCreateView() in search fragments to fill their result lists
    public void requestInitSearch() {
        search("", currentSearchContext);
    }

    //This function gets called in onAttach() in search fragments to set them as the target for searches
    public void requestSearchResults(SearchContext searchContext, SearchListener receiver) {
        currentSearchContext = searchContext;
        currentReceiver = receiver;
    }
}
