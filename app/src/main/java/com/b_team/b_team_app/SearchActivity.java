package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchStartFragment.OnSearchStartFragmentInteractionListener, SearchInContextFragment.OnSearchInContextFragmentInteractionListener {

    private ImageButton bUp, bClear;
    private EditText etSearch;

    private String searchText;

    //The searchContext we are currently searching in
    private SearchContext currentSearchContext;
    //The fragment that should receive the next search result
    private SearchListener currentSearchReceiver;

    //Loader Id's
    //Used to switch the search arguments
    private static final int LID_BOOKS = 0;
    private static final int LID_AUTHORS = 1;
    private static final int LID_PUBLISHERS = 2;
    private static final int LID_GENRES = 3;
    private static final int LID_RATINGS = 4;
    private static final int LID_WISHLIST = 5;
    private static final int LID_SUBCATEGORY = 6;

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
            Log.d("SearchAct.onCreate()", "savedInstanceState = null");
            //Add the SearchStartFragment if we are not restoring an old state
            SearchStartFragment searchStartFragment = new SearchStartFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, searchStartFragment, "SearchStart")
                    .commit();

            currentSearchContext = new SearchContext(null, "", false);
            Log.d("curSearchContext", "Category: null; Name: empty; Single: false");
            currentSearchReceiver = null;
            Log.d("curSearchReciever", "null");
            searchText = "";
            Log.d("searchText", "null");
        } else {
            Log.d("SearchAct.onCreate()", "savedInstanceState != null");
            currentSearchContext = (SearchContext) savedInstanceState.getSerializable("currentSearchContext");
            Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
            searchText = savedInstanceState.getString("searchText");
            Log.d("searchText", searchText);
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
        Log.d("SearchActivity", "onSaveInstanceState()");
        outState.putSerializable("currentSearchContext", currentSearchContext);
        Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
        outState.putString("searchText", searchText);
        Log.d("searchText", searchText);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSearchStartFragmentInteraction(SearchCategory item) {
        Log.d("SearchActivity", "onSearchStartFragmentInteraction()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        SearchContext searchContext = new SearchContext(item, "", item.isAlwaysSingular());
        Fragment newFragment = SearchInContextFragment.newInstance(searchContext);
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

        currentSearchContext = searchContext;
        Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
        searchText = "";
        Log.d("searchText", searchText);
        etSearch.setText(searchText);
        currentSearchReceiver = (SearchListener) newFragment;
        Log.d("curSearchReciever", currentSearchReceiver.toString());
    }

    @Override
    public void onBookSelected(int bookId) {
        Log.d("SearchActivity", "onBookSelected(bookId="+bookId+")");
        Intent bookInfo = new Intent(getBaseContext(), BookInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", bookId);
        bookInfo.putExtras(bundle);
        startActivity(bookInfo);
    }

    @Override
    public void onNewContextSelected(SearchContext searchContext) {
        Log.d("SearchActivity", "onNewContextSelected()");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment newFragment = SearchInContextFragment.newInstance(searchContext);

        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

        currentSearchContext = searchContext;
        Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
        searchText = "";
        Log.d("searchText", searchText);
        etSearch.setText(searchText);
        currentSearchReceiver = (SearchListener) newFragment;
        Log.d("curSearchReciever", currentSearchReceiver.toString());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("SearchActivity", "onCreateLoader(id="+id+")");
        String[] projection;
        Uri uri;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        //TODO: Add cases for ratings and wishlist
        switch (id) {
            case LID_BOOKS:
                Log.d("Case", "LID_BOOKS");
                projection = new String[] {
                        BooksTable.VIEWKEY_ID,
                        BooksTable.VIEWKEY_TITLE + " AS name",
                        BooksTable.VIEWKEY_AUTHOR + " AS info"
                };
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + BooksTable.VIEWKEY_TITLE + "/" + searchText);
                }
                break;
            case LID_AUTHORS:
                Log.d("Case", "LID_AUTHORS");
                projection = new String[] {
                        AuthorsTable.KEY_ID,
                        AuthorsTable.KEY_NAME + " AS name",
                        AuthorsTable.KEY_NBOOKS + " AS info"
                };
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_AUTHORS, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_AUTHORS, BookProvider.PATH_SEARCH_FILTER + AuthorsTable.KEY_NAME + "/" + searchText);
                }
                break;
            case LID_PUBLISHERS:
                Log.d("Case", "LID_PUBLISHERS");
                projection = new String[] {
                        PublishersTable.KEY_ID,
                        PublishersTable.KEY_NAME + " AS name",
                        PublishersTable.KEY_NBOOKS + " AS info"
                };
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_PUBLISHERS, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_PUBLISHERS, BookProvider.PATH_SEARCH_FILTER + PublishersTable.KEY_NAME + "/" + searchText);
                }
                break;
            case LID_GENRES:
                Log.d("Case", "LID_GENRES");
                projection = new String[] {
                        GenresTable.KEY_ID,
                        GenresTable.KEY_NAME + " AS name",
                        GenresTable.KEY_NBOOKS + " AS info"
                };
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_GENRES, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_GENRES, BookProvider.PATH_SEARCH_FILTER + GenresTable.KEY_NAME + "/" + searchText);
                }
                break;
            case LID_RATINGS:
                Log.d("Case", "LID_RATINGS");
                projection = new String[] {
                        BooksTable.VIEWKEY_ID,
                        BooksTable.VIEWKEY_TITLE + " AS name",
                        BooksTable.VIEWKEY_RATING + " AS info"
                };
                sortOrder = BooksTable.VIEWKEY_RATING + " DESC";
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + BooksTable.VIEWKEY_TITLE + "/" + searchText);
                }
                break;
            case LID_WISHLIST:
                Log.d("Case", "LID_WISHLIST");
                projection = new String[] {
                        WishlistTable.VIEWKEY_ID,
                        WishlistTable.VIEWKEY_BOOK_TITLE + " AS name",
                        WishlistTable.VIEWKEY_BOOK_AUTHOR + " AS info"
                };
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                if (searchText.length() == 0) {
                    uri = Uri.withAppendedPath(BookProvider.URI_WISHLIST, BookProvider.PATH_SEARCH_ALL);
                } else {
                    uri = Uri.withAppendedPath(BookProvider.URI_WISHLIST, BookProvider.PATH_SEARCH_FILTER + WishlistTable.VIEWKEY_BOOK_TITLE + "/" + searchText);
                }
                break;
            case LID_SUBCATEGORY:
                Log.d("Case", "LID_SUBCATEGORY");
                projection = new String[] {
                        BooksTable.VIEWKEY_ID,
                        BooksTable.VIEWKEY_TITLE + " AS name",
                        BooksTable.VIEWKEY_AUTHOR + " As info"
                };
                Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
                switch (currentSearchContext.getSearchCategory().getId()) {
                    case SearchCategories.CATEGORY_AUTHORS:
                        selection = BooksTable.VIEWKEY_AUTHOR + " = ?";
                        selectionArgs = new String[] {currentSearchContext.getContextName()};
                        if (searchText.length() == 0) {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
                        } else {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + BooksTable.VIEWKEY_TITLE + "/" + searchText);
                        }
                        break;
                    case SearchCategories.CATEGORY_PUBLISHERS:
                        selection = BooksTable.VIEWKEY_PUBLISHER + " = ?";
                        selectionArgs = new String[] {currentSearchContext.getContextName()};
                        if (searchText.length() == 0) {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
                        } else {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + BooksTable.VIEWKEY_TITLE + "/" + searchText);
                        }
                        break;
                    case SearchCategories.CATEGORY_GENRES:
                        selection = BooksTable.VIEWKEY_GENRE + " = ?";
                        selectionArgs = new String[] {currentSearchContext.getContextName()};
                        if (searchText.length() == 0) {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
                        } else {
                            uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + BooksTable.VIEWKEY_TITLE + "/" + searchText);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("This category is singular");
                }
                break;
            default:
                throw new UnsupportedOperationException("No matching id");
        }
        if (selection != null) {
            Log.d("selection", selection);
        } else {
            Log.d("selection", "null");
        }
        if (selectionArgs != null && selectionArgs.length>0) {
            Log.d("selectionArgs", selectionArgs.length + ": " + selectionArgs[0]);
        }
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("SearchActivity", "onLoadFinished(loader.id="+loader.getId()+")");
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        Log.d("curSearchReciever", currentSearchReceiver.toString());
        Log.d("data", DatabaseUtils.dumpCursorToString(data));
        currentSearchReceiver.onSearchComplete(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("SearchActivity", "onLoadFinished(loader.id="+loader.getId()+")");
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        currentSearchReceiver.onSearchComplete(null);
    }

    /**
     * Event: The up button was clicked
     */
    //TODO: Custom back stack behavior to restore search context and search receiver
    private void onUpClicked() {
        onBackPressed();
        //finish();
    }

    /**
     * Event: The clear button was clicked
     */
    private void onClearClicked() {
        etSearch.setText("");
    }

    //This gets called by SearchInContextFragment to fill the result list
    public boolean requestEmptySearch() {
        Log.d("SearchActivity", "requestEmptySearch()");
        Log.d("searchText", searchText);
        if (searchText.length() == 0) {
            onSearchTextChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Event: The search text changed
     */
    private void onSearchTextChanged() {
        Log.d("SearchActivity", "onSearchTextChanged()");
        searchText = etSearch.getText().toString();
        Log.d("searchText", searchText);

        //TODO: Rethink this condition as it is never properly fulfilled
        if (currentSearchContext.getSearchCategory() == null) {
            Log.d("curSearchContext", "Category: null"+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
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
            }
        } else {
            Log.d("curSearchContext", "Category: "+currentSearchContext.getSearchCategory().getId()+"; Name: "+currentSearchContext.getContextName()+"; Single: "+currentSearchContext.isSingleContext());
            if (currentSearchContext.getContextName() != null && currentSearchContext.getContextName().length() != 0) {
                if (getLoaderManager().getLoader(LID_SUBCATEGORY) == null) {
                    getLoaderManager().initLoader(LID_SUBCATEGORY, null, this);
                } else {
                    getLoaderManager().restartLoader(LID_SUBCATEGORY, null, this);
                }
            }
            switch (currentSearchContext.getSearchCategory().getId()) {
                case SearchCategories.CATEGORY_TITLES:
                    if (getLoaderManager().getLoader(LID_BOOKS) == null) {
                        getLoaderManager().initLoader(LID_BOOKS, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_BOOKS, null, this);
                    }
                    break;
                case SearchCategories.CATEGORY_AUTHORS:
                    if (getLoaderManager().getLoader(LID_AUTHORS) == null) {
                        getLoaderManager().initLoader(LID_AUTHORS, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_AUTHORS, null, this);
                    }
                    break;
                case SearchCategories.CATEGORY_PUBLISHERS:
                    if (getLoaderManager().getLoader(LID_PUBLISHERS) == null) {
                        getLoaderManager().initLoader(LID_PUBLISHERS, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_PUBLISHERS, null, this);
                    }
                    break;case SearchCategories.CATEGORY_GENRES:
                    if (getLoaderManager().getLoader(LID_GENRES) == null) {
                        getLoaderManager().initLoader(LID_GENRES, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_GENRES, null, this);
                    }
                    break;
                case SearchCategories.CATEGORY_RATINGS:
                    if (getLoaderManager().getLoader(LID_RATINGS) == null) {
                        getLoaderManager().initLoader(LID_RATINGS, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_RATINGS, null, this);
                    }
                    break;
                case SearchCategories.CATEGORY_WISHLIST:
                    if (getLoaderManager().getLoader(LID_WISHLIST) == null) {
                        getLoaderManager().initLoader(LID_WISHLIST, null, this);
                    } else {
                        getLoaderManager().restartLoader(LID_WISHLIST, null, this);
                    }
                    break;
            }
        }
    }


    private void search() {

        return;
    }
}
