package com.b_team.b_team_app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookEditActivity extends Activity implements OnClickListener {

    private Button save, delete, cancel;
    private Switch switch_read, switch_currentlyReading, switch_wantToRead;
    private String mode;
    private EditText title, author, isbn, publisher, nPages, nVolume, genre, ownership, rating, currentPage;
    private String id;
    private TextView maxpage;
    private SimpleCursorAdapter dataAdapter;
    private int readingstatus = -1;
    private Toast curToastMessage;
    private ConstraintLayout layout_currentlyReadingBox;
    private ProgressBar pb_readingstatus;


    //Ignore this. Dynamic scrolling would be nice but it doesn't work right now and we don't have time for it
    //The ids of all the edit text fields that should affect the scroll when focused
    /*
    private static ArrayList<Integer> scrollFocusableFields = new ArrayList<>();
    static {
        scrollFocusableFields.add(R.id.editText_title);
        scrollFocusableFields.add(R.id.editText_author);
        scrollFocusableFields.add(R.id.editText_isbn);
        scrollFocusableFields.add(R.id.editText_publisher);
        scrollFocusableFields.add(R.id.editText_nPages);
        scrollFocusableFields.add(R.id.editText_nVolume);
        scrollFocusableFields.add(R.id.editText_genre);
        scrollFocusableFields.add(R.id.editText_ownership);
        scrollFocusableFields.add(R.id.editText_rating);
    }
    private int scrollBaseHeight;
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);

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
        rating = (EditText) findViewById(R.id.editText_rating);

        //Dynamic scrolling. Not done. Please ignore.
        /*
        scrollBaseHeight = findViewById(R.id.scrollContainer).getHeight();

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("onFocusChange()", "hasFocus?: " + hasFocus);
                final View currentView = v;
                int currentId = v.getId();
                if (scrollFocusableFields.contains(currentId)) {
                    Log.d("onFocusChange()", currentId + " is inside array.");
                    int spacing = 0;
                    int currentIndex = scrollFocusableFields.indexOf(currentId);
                    if (currentIndex < scrollFocusableFields.size() - 1) {
                        Log.d("onFocusChange()", currentIndex + " is not last element.");
                        for (int i=currentIndex; i>=0; i--) {
                            spacing += findViewById(scrollFocusableFields.get(i)).getHeight();
                        }
                        Log.d("onFocusChange()", "spacing: " + spacing);
                    }
                    RelativeLayout scrollContainer = (RelativeLayout) findViewById(R.id.scrollContainer);
                    scrollContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, scrollBaseHeight + spacing));
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewEdit);
                    scrollView.post(new Runnable() {
                        public void run() {
                            Log.d("onFocusChange()", "Post(): scroll to " + currentView.getBottom());
                            scrollView.smoothScrollTo(0, currentView.getBottom());
                        }
                    });
                }
            }
        };

        for (int i=0; i<scrollFocusableFields.size(); i++) {
            findViewById(scrollFocusableFields.get(i)).setOnFocusChangeListener(listener);
        }
        */

        maxpage = (TextView) findViewById(R.id.textView_maxPage);
        nPages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                maxpage.setText(String.format(getString(R.string.bookedit_maxpage),nPages.getText().toString()));
            }
        });

        pb_readingstatus = (ProgressBar)findViewById(R.id.progressBar);
        currentPage = (EditText) findViewById(R.id.editText_currentPage);
        currentPage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (nPages.getText().toString().length() != 0) {
                    pb_readingstatus.setMax(Integer.parseInt(nPages.getText().toString()));
                    pb_readingstatus.setProgress(Integer.parseInt(currentPage.getText().toString()));
                }
            }
        });

        switch_read = (Switch) findViewById(R.id.switch_read);
        switch_wantToRead = (Switch) findViewById(R.id.switch_wantToRead);
        switch_currentlyReading = (Switch) findViewById(R.id.switch_currentlyReading);
        layout_currentlyReadingBox = (ConstraintLayout) findViewById(R.id.layout_currentlyReadinBox);

        //Button function, set false for not chosen ones
        switch_read.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switch_wantToRead.setChecked(false);
                    switch_currentlyReading.setChecked(false);
                    readingstatus = 2;
                    layout_currentlyReadingBox.setVisibility(View.GONE);
                }
                else {
                    readingstatus = -1;
                }
            }
        });
        switch_wantToRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switch_read.setChecked(false);
                    switch_currentlyReading.setChecked(false);
                    readingstatus = 0;
                    layout_currentlyReadingBox.setVisibility(View.GONE);
                }
                else {
                    readingstatus = -1;
                }
            }
        });
        switch_currentlyReading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switch_read.setChecked(false);
                    switch_wantToRead.setChecked(false);
                    readingstatus = 1;
                    layout_currentlyReadingBox.setVisibility(View.VISIBLE);
                }
                else{
                    layout_currentlyReadingBox.setVisibility(View.GONE);
                    readingstatus =-1;
                }
            }
        });

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

        //Initialise a Toast so curToastMessage is never null
        curToastMessage = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT);
        curToastMessage.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean wasFocusMovementDown(View lastView, View currentView) {
        if (lastView.getNextFocusDownId() == currentView.getId()) return true;
        return false;
    }

    @Override
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
                String myRating = rating.getText().toString();
                String mycurrentpage = null;
                String myreadingstatus = String.valueOf(readingstatus);

                switch (readingstatus) {
                    case 1:
                         mycurrentpage = currentPage.getText().toString();
                        break;
                    case 0:
                        mycurrentpage = String.valueOf(0);
                        break;
                    case 2:
                        mycurrentpage = mynPages;
                        break;
                    case -1:
                        curToastMessage.cancel();
                        curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_readingstatus)), Toast.LENGTH_LONG);
                        curToastMessage.show();
                        return;

                }



                //TODO: Default values instead of toast
                // check for blanks and invalid characters
                if(mycurrentpage.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_currentpage)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }
                else if (checkForInvalidChar(mycurrentpage)) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_currentpage)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(mytitle.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_title)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(mytitle)) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_title)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(myauthor.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_author)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(myauthor.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_author)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(myisbn.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_isbn)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(myisbn)) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_isbn)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(mypublisher.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_publisher)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(mypublisher.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_publisher)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(mynPages.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_nPages)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(mynPages.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_nPages)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(mynVolume.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_nVolume)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(mynVolume.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_nVolume)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(mygenre.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_genre)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(mygenre.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_genre)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(myownership.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_ownership)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(myownership.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_ownership)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                }

                if(myRating.trim().equalsIgnoreCase("")){
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_emptyRequiredField), getString(R.string.key_rating)), Toast.LENGTH_LONG);
                    curToastMessage.show();
                    return;
                } else if (checkForInvalidChar(myRating.trim())) {
                    curToastMessage.cancel();
                    curToastMessage = Toast.makeText(getBaseContext(), String.format(getString(R.string.editError_invalidChar), getString(R.string.key_rating)), Toast.LENGTH_LONG);
                    curToastMessage.show();
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
                    values_book.put(BooksTable.KEY_RATING, myRating);
                    values_book.put(BooksTable.KEY_READINGSTATUS, myreadingstatus );
                    values_book.put(BooksTable.KEY_CURRENTPAGE, mycurrentpage);


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
                curToastMessage.cancel();
                finish();
                break;
        }
    }

    private boolean checkForInvalidChar(String string) {
        Pattern regex = Pattern.compile("[A-Za-z0-9]");
        Matcher matcher = regex.matcher(string);
        if(matcher.find()) {
            return false;
        }
        return true;
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
                BooksTable.VIEWKEY_OWNERSHIP,
                BooksTable.VIEWKEY_RATING};
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
            String myRating = cursor.getString(cursor.getColumnIndexOrThrow(BooksTable.VIEWKEY_RATING));
            title.setText(myTitle);
            author.setText(myAuthor);
            isbn.setText(myIsbn);
            publisher.setText(myPublisher);
            nPages.setText(myNPages);
            nVolume.setText(myNVolume);
            genre.setText(myGenre);
            ownership.setText(myOwnership);
            rating.setText(myRating);

            cursor.close();
        }


    }

}
