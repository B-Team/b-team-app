package com.b_team.b_team_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PageCountEdit extends AppCompatActivity implements OnClickListener {

    private int bookId;
    private TextView textViewMaxPageCount;
    private EditText editTextCurrentPageCount;
    private String maxPageCount, currentPageCount;
    private ProgressBar pb_readingstatus;
    private Button buttonSave, buttonCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagecount_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        bookId = bundle.getInt("bookId");

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

        textViewMaxPageCount = (TextView) findViewById(R.id.textViewMaxPageCount);
        editTextCurrentPageCount = (EditText) findViewById(R.id.editTextCurrentPagecount);

        Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, "/" + bookId);
        String[] projection = new String[]{
                BooksTable.VIEWKEY_ID,
                BooksTable.VIEWKEY_CURRENTPAGE,
                BooksTable.KEY_NPAGES
        };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        currentPageCount = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_CURRENTPAGE));
        maxPageCount = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_NPAGES));

        textViewMaxPageCount.setText(maxPageCount);
        editTextCurrentPageCount.setText(currentPageCount);

        pb_readingstatus = (ProgressBar) findViewById(R.id.progressBar3);
        pb_readingstatus.setProgress(Integer.parseInt(editTextCurrentPageCount.getText().toString()));
        pb_readingstatus.setMax(Integer.parseInt(textViewMaxPageCount.getText().toString()));

        editTextCurrentPageCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextCurrentPageCount.getText().toString().length() != 0 && pb_readingstatus != null) {
                    pb_readingstatus.setMax(Integer.parseInt(textViewMaxPageCount.getText().toString()));
                    pb_readingstatus.setProgress(Integer.parseInt(editTextCurrentPageCount.getText().toString()));
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSave:
                ContentValues values_book = new ContentValues();
                values_book.put(BooksTable.KEY_CURRENTPAGE,Integer.parseInt(editTextCurrentPageCount.getText().toString()));
                Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, "/" + bookId);
                getContentResolver().update(uri, values_book, null, null);
                finish();
                break;
            case R.id.buttonCancel:
                finish();
                break;
        }
    }
}





