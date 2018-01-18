package com.b_team.b_team_app;

import android.content.Intent;
import android.database.Cursor;
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
