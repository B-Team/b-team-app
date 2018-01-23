package com.b_team.b_team_app;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BookInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    TextView tvBookTitle, tvAuthorName, tvPublisherName, tvNVolume, tvNMaxVolume, tvNPages, tvBookType, tvIsbn, tvGenreName, tvOwnershipStatus, tvNotes;
    Button bOpenDescription, progress;
    ImageView ivRatingStar1, ivRatingStar2, ivRatingStar3, ivRatingStar4, ivRatingStar5, ivBookCover;
    int rating;
    String description;
    int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
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

        tvBookTitle = (TextView) findViewById(R.id.textView_bookTitle);
        tvAuthorName = (TextView) findViewById(R.id.textView_authorName);
        tvPublisherName = (TextView) findViewById(R.id.textView_publisher);
        tvNVolume = (TextView) findViewById(R.id.textView_nVolume);
        tvNMaxVolume = (TextView) findViewById(R.id.textView_maxVolume);
        tvNPages = (TextView) findViewById(R.id.textView_nPages);
        tvBookType = (TextView) findViewById(R.id.textView_bookType);
        tvIsbn = (TextView) findViewById(R.id.textView_isbn);
        tvGenreName = (TextView) findViewById(R.id.textView_genre);
        tvOwnershipStatus = (TextView) findViewById(R.id.textView_ownershipStatus);
        tvNotes = (TextView) findViewById(R.id.textView_notes);

        bOpenDescription = (Button) findViewById(R.id.button_openDescription);
        bOpenDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = BookDescriptionFragment.createBookDescriptionFragment(description);
                dialog.show(getSupportFragmentManager(), "BookDescriptionFragment");
            }
        });


        ivBookCover = (ImageView) findViewById(R.id.imageView_bookCover);
        
        ivRatingStar1 = (ImageView) findViewById(R.id.imageView_ratingStar1);
        ivRatingStar2 = (ImageView) findViewById(R.id.imageView_ratingStar2);
        ivRatingStar3 = (ImageView) findViewById(R.id.imageView_ratingStar3);
        ivRatingStar4 = (ImageView) findViewById(R.id.imageView_ratingStar4);
        ivRatingStar5 = (ImageView) findViewById(R.id.imageView_ratingStar5);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(BookProvider.URI_BOOKS, "/" + bookId);
        //TODO: Add missing fields
        String[] projection = new String[] {
                BooksTable.VIEWKEY_ID,
                BooksTable.VIEWKEY_TITLE,
                BooksTable.VIEWKEY_AUTHOR,
                BooksTable.VIEWKEY_PUBLISHER,
                BooksTable.VIEWKEY_GENRE,
                BooksTable.VIEWKEY_ISBN,
                BooksTable.VIEWKEY_RATING,
                BooksTable.VIEWKEY_NPAGES,
                BooksTable.VIEWKEY_NVOLUME,
                BooksTable.VIEWKEY_OWNERSHIP,
        };
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        tvBookTitle.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_TITLE)));
        tvAuthorName.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_AUTHOR)));
        tvPublisherName.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_PUBLISHER)));
        tvGenreName.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_GENRE)));
        tvIsbn.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_ISBN)));
        tvOwnershipStatus.setText(data.getString(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_OWNERSHIP)));
        tvNPages.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_NPAGES))));
        tvNVolume.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_NVOLUME))));
        rating = data.getInt(data.getColumnIndexOrThrow(BooksTable.VIEWKEY_RATING));

        setRatingStars(rating);

        //Dummy values to be replaced
        tvNMaxVolume.setText("?");
        tvBookType.setText("Hardcover");
        tvNotes.setText("--\n---\n----\n-----\n------\n    Dummy notes\n------\n-----\n----\n---\n--");
        ivBookCover.setImageResource(R.drawable.moers_prinzessin_insomnia);
        description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" +
                "\n" +
                "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. ";
    }

    private void setRatingStars(int rating) {
        Log.d("Rating", String.valueOf(rating));
        switch (rating) {
            case 0:
                ivRatingStar1.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 1:
                ivRatingStar1.setImageResource(R.drawable.ic_star_half_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 2:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 3:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_half_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 4:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 5:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_half_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 6:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_border_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 7:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_half_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 8:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_border_black_24dp);
                break;
            case 9:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_half_black_24dp);
                break;
            case 10:
                ivRatingStar1.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar2.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar3.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar4.setImageResource(R.drawable.ic_star_black_24dp);
                ivRatingStar5.setImageResource(R.drawable.ic_star_black_24dp);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        Log.d("ActionBar", "Action bar present: " + getActionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                //TODO: Implement share functionality
                return true;
            case R.id.action_edit:
                //TODO: Open edit activity
                return true;
            default:
                //Action not recognized
                //Let super handle it
                return super.onOptionsItemSelected(item);
        }
    }
}
