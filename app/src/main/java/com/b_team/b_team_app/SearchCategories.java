package com.b_team.b_team_app;


import java.util.ArrayList;

public class SearchCategories {
    //Category id's
    public static final int CATEGORY_TITLES = 0;
    public static final int CATEGORY_AUTHORS = 1;
    public static final int CATEGORY_PUBLISHERS = 2;
    public static final int CATEGORY_GENRES = 3;
    public static final int CATEGORY_WISHLIST = 4;
    public static final int CATEGORY_RATINGS = 5;

    //Array of categories
    public static final ArrayList<SearchCategory> CATEGORIES = new ArrayList<>();
    static{
        //TODO: Add missing categorie icons
        CATEGORIES.add(new SearchCategory(CATEGORY_TITLES));
        CATEGORIES.add(new SearchCategory(CATEGORY_AUTHORS));
        CATEGORIES.add(new SearchCategory(CATEGORY_PUBLISHERS));
        CATEGORIES.add(new SearchCategory(CATEGORY_GENRES));
        CATEGORIES.add(new SearchCategory(CATEGORY_WISHLIST));
        CATEGORIES.add(new SearchCategory(CATEGORY_RATINGS));
    }

    //Array of Names
    public static final int[] NAMES = new int[] {
            R.string.search_category_header_titles,
            R.string.search_category_header_authors,
            R.string.search_category_header_publishers,
            R.string.search_category_header_genres,
            R.string.search_category_header_wishlist,
            R.string.search_category_header_ratings
    };

    //Array of Icons
    public static final int[] ICONS = new int[] {
            R.drawable.ic_book_black_24dp,
            R.drawable.ic_person_black_24dp,
            R.drawable.ic_clear_black_24dp,
            R.drawable.ic_clear_black_24dp,
            R.drawable.ic_format_list_bulleted_black_24dp,
            R.drawable.ic_star_black_24dp
    };

    //Array of count messages
    public static final int[] COUNTMESSAGES = new int[] {
            R.string.search_count_messages_titles,
            R.string.search_count_messages_authors,
            R.string.search_count_messages_publishers,
            R.string.search_count_messages_genres,
            R.string.search_count_messages_wishlist,
            R.string.search_count_messages_ratings
    };

    //Array of alwaysSingular
    public static final boolean[] ALWAYSSINGULAR = new boolean[] {
            true,
            false,
            false,
            false,
            true,
            true
    };
}
