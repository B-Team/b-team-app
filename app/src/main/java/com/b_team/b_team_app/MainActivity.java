package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ListView resultList;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String[] columns = new String[]{
                BooksTable.VIEWKEY_TITLE,
                BooksTable.VIEWKEY_AUTHOR,
                BooksTable.VIEWKEY_CURRENTPAGE,
                BooksTable.VIEWKEY_NPAGES
        };
        int[] to = new int[]{
                R.id.activeTitel
                R.id.activeAuthor,
                R.id.activeCurrentPage,
                R.id.activeMaxPage
        };
        dataAdapter = new SimpleCursorAdapter(this, R.layout.active_book_item, null, columns, to, 0);
        resultList.setAdapter(dataAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                BooksTable.VIEWKEY_ID,
                BooksTable.VIEWKEY_TITLE,
                BooksTable.VIEWKEY_AUTHOR,
                BooksTable.VIEWKEY_CURRENTPAGE,
                BooksTable.VIEWKEY_NPAGES

        };
        Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
        String selection =
                //hier einfügen,name des feldes BookTable.View_Currentape und edr wert der es sein soll
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Cursor resultCursor;

        switch (item.getItemId()) {
            case R.id.action_search:
                Intent search = new Intent(getBaseContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "full");
                search.putExtras(bundle);
                startActivity(search);
                return true;
            case R.id.action_add_book:
                Intent bookEdit = new Intent(getBaseContext(), BookEditActivity.class);
                Bundle bundle_add = new Bundle();
                bundle_add.putString("mode", "add");
                bookEdit.putExtras(bundle_add);
                startActivity(bookEdit);
            default:
                //Action not recognized
                //Let super handle it
                return super.onOptionsItemSelected(item);
        }
    }
}
