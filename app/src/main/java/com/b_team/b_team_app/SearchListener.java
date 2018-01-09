package com.b_team.b_team_app;


import android.database.Cursor;

//Every search fragment implements this to receive the results of their search requests
public interface SearchListener {
    void onSearchComplete(Cursor data);
}
