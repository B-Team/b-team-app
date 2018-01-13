package com.b_team.b_team_app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity{

    private ListView resultList;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultList = (ListView) findViewById(R.id.resultList);
        String[] columns = new String[] {
                "name",
                "info"
        };
        int[] to = new int[] {
                R.id.itemName,
                R.id.itemInfo
        };
        dataAdapter = new SimpleCursorAdapter(this, R.layout.search_result_item, null, columns, to, 0);
        resultList.setAdapter(dataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                /*
                Intent search = new Intent(getBaseContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "full");
                search.putExtras(bundle);
                startActivity(search);
                */
                Intent bookEdit = new Intent(getBaseContext(), BookEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                bookEdit.putExtras(bundle);
                startActivity(bookEdit);
                return true;
            case R.id.action_search_books:
                resultCursor = getContentResolver().query(Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL),
                        new String[]{BooksTable.VIEWKEY_ID, BooksTable.VIEWKEY_TITLE + " AS name", BooksTable.VIEWKEY_AUTHOR + " AS info"},
                        null, null, null);
                dataAdapter.swapCursor(resultCursor);
                return true;
            case R.id.action_search_authors:
                resultCursor = getContentResolver().query(Uri.withAppendedPath(BookProvider.URI_AUTHORS, BookProvider.PATH_SEARCH_ALL),
                        new String[]{AuthorsTable.KEY_ID, AuthorsTable.KEY_NAME + " AS name", AuthorsTable.KEY_NAME + " AS info"},
                        null, null, null);
                dataAdapter.swapCursor(resultCursor);
                return true;
            case R.id.action_search_test:
                resultCursor = getContentResolver().query(Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_FILTER + "/" + BooksTable.VIEWKEY_TITLE + "/w"),
                        new String[]{BooksTable.VIEWKEY_ID, BooksTable.VIEWKEY_TITLE + " AS name", BooksTable.VIEWKEY_AUTHOR + " AS info"},
                        null, null, null);
                dataAdapter.swapCursor(resultCursor);
                return true;
            default:
                //Action not recognized
                //Let super handle it
                return super.onOptionsItemSelected(item);
        }
    }
}
