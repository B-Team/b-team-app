package com.b_team.b_team_app;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.LinkedHashMap;
import java.util.Random;

public class StatisticsActivity extends AppCompatActivity {

    //TODO: Add graphs in activity_statistics.xml
    PieGraph pg_genre;
    LinearLayout legendBox;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Reference to the piegraph in the layout
        //TODO: Add references to graphs
        pg_genre = (PieGraph) findViewById(R.id.piegraph_genre);
        //Reference to a linear layout that will be filled with the labels and their colors (legend)
        legendBox = (LinearLayout) findViewById(R.id.labelsBox_genres);

        //Initialize random stream for random colors
        random = new Random();

        //TODO: Add graph functions here
        setupGenreGraph();

    }
    //TODO: Create a function like this for every Graph neede
    private void setupGenreGraph() {
        //A table with the genre name as key and the number of books that have this genre as value
        LinkedHashMap<String, Integer> genresMap = new LinkedHashMap<>();

        //Tell the BookProvider to give us all genres
        Uri uri = Uri.withAppendedPath(BookProvider.URI_GENRES, BookProvider.PATH_SEARCH_ALL);
        //Tell the BookProvider to give us all three columns of the genre table (the id is always required for the cursor to work)
        String[] projection = new String[] {
                GenresTable.KEY_ID,
                GenresTable.KEY_NAME,
                GenresTable.KEY_NBOOKS
        };
        //Get a cursor (pointer) to the requested genres
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        //Move the cursor to the start of the returned table of genres
        cursor.moveToFirst();
        //As long as there are genres in the table...
        while (!cursor.isAfterLast()) {
            //...get the genre name and number of books from the cursor and add them as a new pair in our map
            genresMap.put(cursor.getString(cursor.getColumnIndexOrThrow(GenresTable.KEY_NAME)), cursor.getInt(cursor.getColumnIndexOrThrow(GenresTable.KEY_NBOOKS)));
            cursor.moveToNext();
        }

        //Sort the genres in descending order by their number of books to make the graph neater
        genresMap = sortMapOfInt(genresMap);

        //Fuse the genres with the fewest books into one 'other' category to make the graph even neater
        //maxCategories should be changed to desired value
        genresMap = fuseSmallCategories(genresMap, getString(R.string.stat_categorylabel_other), 4);

        //Iterate over all keys in the genres map
        for (String s : genresMap.keySet()) {
            PieSlice ps = new PieSlice();
            ps.setValue(genresMap.get(s));
            //Assign a random color. This could be changed to pick from a pre made list if more consistent colors are desired
            String color = randomHexColor();
            ps.setColor(Color.parseColor(color));
            ps.setTitle(s);

            pg_genre.addSlice(ps);

            //Add this genre to the legend as an instance of stats_piegraph_legend_item.xml
            //Inflate the stats_piegraph_legend_item.xml to get a view
            LinearLayout legendItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.stats_piegraph_legend_item, null, false);
            //Set the color by adding it to the white image view
            ImageView legendColorImage = (ImageView) legendItem.findViewById(R.id.imageView_legendColor);
            legendColorImage.setBackgroundColor(Color.rgb(Integer.parseInt(color.substring(1,3), 16),Integer.parseInt(color.substring(3,5), 16),Integer.parseInt(color.substring(5,7), 16)));
            //Set the label
            TextView legendTextView = (TextView) legendItem.findViewById(R.id.textView_legendLabel);
            legendTextView.setText(s);
            //Add the item to the box
            legendBox.addView(legendItem);
        }
    }

    private LinkedHashMap<String,Integer> sortMapOfInt(LinkedHashMap<String,Integer> map) {
        LinkedHashMap<String,Integer> orderedMap = new LinkedHashMap<>();
        int mapSize = map.size();
        for (int i=0; i<mapSize; i++) {
            int biggestValue = -1;
            String biggestKey = null;
            for (String s : map.keySet()) {
                if (!orderedMap.keySet().contains(s)) {
                    int value = map.get(s);
                    if (value > biggestValue) {
                        biggestValue = value;
                        biggestKey = s;
                    }
                }
            }
            if (biggestKey != null) {
                orderedMap.put(biggestKey, biggestValue);
            } else {
                throw new IllegalStateException("No biggest key set");
            }
        }
        return orderedMap;
    }

    //This function always puts the last category under 'other' even if the maxCaterories would allow it to be shown.
    //TODO: Redo this function to fix above-mentioned error
    private LinkedHashMap<String,Integer> fuseSmallCategories(LinkedHashMap<String,Integer> map, String key_fused, int maxCategories) {
        LinkedHashMap<String,Integer> fusedMap = new LinkedHashMap<>();
        boolean noOther = false;
        if (maxCategories > 1) {
            maxCategories = maxCategories - 1;
            if (maxCategories > map.size()) {
                maxCategories = map.size();
            }
            if (maxCategories == map.size()) {
                noOther = true;
            }
        } else {
            maxCategories = 1;
        }
        int fusedValue = 0;
        for (int i=maxCategories; i<map.size(); i++) {
            fusedValue += map.get(map.keySet().toArray()[i]);
        }
        for (int i=0; i<maxCategories; i++) {
            fusedMap.put((String)map.keySet().toArray()[i], map.get(map.keySet().toArray()[i]));
        }
        if (!noOther) {
            fusedMap.put(key_fused, fusedValue);
        }
        return fusedMap;
    }

    private String randomHexColor() {
        //Highest int representing a color: 256*256*256 = 16777216
        return String.format("#%06x", random.nextInt(16777216));
    }
}
