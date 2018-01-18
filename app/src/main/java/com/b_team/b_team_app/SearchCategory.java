package com.b_team.b_team_app;


import java.io.Serializable;

public class SearchCategory implements Serializable{

    private int id;

    public SearchCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getName() {
        return SearchCategories.NAMES[id];
    }

    public int getIcon() {
        return SearchCategories.ICONS[id];
    }

    public int getCountMessage() {
        return SearchCategories.COUNTMESSAGES[id];
    }

    public boolean isAlwaysSingular() {
        return SearchCategories.ALWAYSSINGULAR[id];
    }

}
