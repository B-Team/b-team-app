package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ListView resultList;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        resultList = (ListView) findViewById(R.id.resultList);
        String[] columns = new String[]{
                BooksTable.VIEWKEY_TITLE,
                BooksTable.VIEWKEY_AUTHOR,
                BooksTable.VIEWKEY_CURRENTPAGE,
                BooksTable.VIEWKEY_NPAGES,
                BooksTable.VIEWKEY_ID
        };
        int[] to = new int[]{
                R.id.activeTitel,
                R.id.activeAuthor,
                R.id.activeCurrentPage,
                R.id.activeMaxPage,
                R.id.TextView_bookId
        };
        dataAdapter = new SimpleCursorAdapter(this, R.layout.active_book_item, null, columns, to, 0);
        resultList.setAdapter(dataAdapter);

        getLoaderManager().initLoader(0, null, this);
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
                BooksTable.VIEWKEY_NPAGES,


        };
        Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, BookProvider.PATH_SEARCH_ALL);
        String selection = BooksTable.VIEWKEY_READINGSTATUS + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(1)};
        CursorLoader cursorLoader = new CursorLoader(this, uri,projection, selection, selectionArgs, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataAdapter.swapCursor(data);
        Log.d("onLoadfinished", "data:"+ DatabaseUtils.dumpCursorToString(data));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dataAdapter.swapCursor(null);

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

    public void infoButtonClicked(View v){
        ConstraintLayout parent = (ConstraintLayout) v.getParent();
        TextView idTextView = (TextView) parent.findViewById(R.id.TextView_bookId);
        int id = Integer.parseInt(idTextView.getText().toString());
        Log.d("editButtonClicked", "ID:"+ id);
        Intent bookInfo = new Intent(getBaseContext(), BookInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", id);
        bookInfo.putExtras(bundle);
        startActivity(bookInfo);
    }
    public void editButtonClicked(View v){
        ConstraintLayout parent = (ConstraintLayout) v.getParent();
        TextView idTextView = (TextView) parent.findViewById(R.id.TextView_bookId);
        int id = Integer.parseInt(idTextView.getText().toString());
        Log.d("editButtonClicked", "ID:"+ id);
        Intent pageCountEdit = new Intent(getBaseContext(), PageCountEdit.class);//würde BookInfoAcitivity ändern
        Bundle bundle = new Bundle();
        bundle.putInt("bookId", id);
        pageCountEdit.putExtras(bundle);
        startActivity(pageCountEdit);
    }
}
