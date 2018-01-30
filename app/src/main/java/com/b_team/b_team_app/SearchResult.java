package com.b_team.b_team_app;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A container for a search result
 */
public class SearchResult {

    private class Category {
        public ArrayList<Data> data;

        public Category() {
            data = new ArrayList<>();
        }
    }

    private class Data {
        public Cursor cursor;
        public String matchedField;

        public Data(Cursor cursor, String matchedField) {
            this.cursor = cursor;
            this.matchedField = matchedField;
        }
    }

    private final String searchText;
    private Map<String, Category> categories;

    public SearchResult(String searchText, String ... categories) {
        this.searchText = searchText;
        this.categories = new HashMap<>();
        addCategory("default");
        for (String category : categories) {
            addCategory(category);
        }
    }

    /**
     * Adds a new category to store data in
     * @param name the name of the new category
     */
    public void addCategory(String name) {
        categories.put(name, new Category());
    }

    /**
     * Adds a new piece of search result
     * @param category      the category that holds this data
     * @param data          the data to add
     * @param matchedField  the field of the data that was found to match the search text
     */
    public void addData(String category, Cursor data, String matchedField) {
        categories.get(category).data.add(new Data(data, matchedField));
    }

    /**
     * Returns all results in the given category
     * @param category  the category to return the results of
     * @return          map of cursors and the name of the field that matched the search text
     */
    public Cursor[] getResultsInCategory(String category) {
        Category c = categories.get(category);
        Cursor[] cursors = new Cursor[c.data.size()];
        for (int i=0; i<c.data.size(); i++) {
            cursors[i] = c.data.get(i).cursor;
        }
        return cursors;
    }
}
