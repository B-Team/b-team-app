package com.b_team.b_team_app;


import java.io.Serializable;
import java.util.ArrayList;

public class SearchCategory implements Serializable{

    /*
    This whole structure is a little bit redundant. Instead of saving all data in every instance
    a SearchCategory should only contain it's id and getter-functions that return data from constants in a second class.
    But this class only gets instantiated a couple of times during a search and it doesn't contain much data (mostly resource id's)
    so it's ok for now.
    Note to distant future self: make this class better
     */

    //Category id's
    public static final int CATEGORY_TITLES = 0;
    public static final int CATEGORY_AUTHORS = 1;
    public static final int CATEGORY_PUBLISHERS = 2;
    public static final int CATEGORY_GENRES = 3;
    public static final int CATEGORY_WISHLIST = 4;
    public static final int CATEGORY_RATINGS = 5;
    public static final int CATEGORY_ALL = 6;

    //Array of categories
    public static final ArrayList<SearchCategory> CATEGORIES = new ArrayList<>();
    static{
        //TODO: Add missing categorie icons
        CATEGORIES.add(new SearchCategory(CATEGORY_TITLES, R.string.search_category_header_titles, R.drawable.ic_book_black_24dp, R.string.search_count_messages_titles, true));
        CATEGORIES.add(new SearchCategory(CATEGORY_AUTHORS, R.string.search_category_header_authors, R.drawable.ic_person_black_24dp, R.string.search_count_messages_authors, false));
        CATEGORIES.add(new SearchCategory(CATEGORY_PUBLISHERS, R.string.search_category_header_publishers, R.drawable.ic_clear_black_24dp, R.string.search_count_messages_publishers, false));
        CATEGORIES.add(new SearchCategory(CATEGORY_GENRES, R.string.search_category_header_genres, R.drawable.ic_clear_black_24dp, R.string.search_count_messages_genres, false));
        CATEGORIES.add(new SearchCategory(CATEGORY_WISHLIST, R.string.search_category_header_wishlist, R.drawable.ic_format_list_bulleted_black_24dp, R.string.search_count_messages_wishlist, true));
        CATEGORIES.add(new SearchCategory(CATEGORY_RATINGS, R.string.search_category_header_ratings, R.drawable.ic_star_black_24dp, R.string.search_count_messages_ratings, true));
    }

    private int id;
    private int name;
    private int icon;
    private int countMessage;

    //always singular means this category always shows a list of books
    private boolean alwaysSingular;

    public SearchCategory(int categoryName, int categoryIcon) {
        name = categoryName;
        icon = categoryIcon;
    }

    public SearchCategory(int id, int categoryName, int categoryIcon, int countMessage, boolean alwaysSingular) {
        this.id = id;
        name = categoryName;
        icon = categoryIcon;
        this.countMessage = countMessage;
        this.alwaysSingular = alwaysSingular;
    }

    public int getId() {
        return id;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getCountMessage() {
        return countMessage;
    }

    public boolean isAlwaysSingular() {
        return alwaysSingular;
    }

}
