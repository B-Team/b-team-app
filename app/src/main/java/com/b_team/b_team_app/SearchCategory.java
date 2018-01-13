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
        return 0;
    }

    public int getIcon() {
        return 0;
    }

    public int getCountMessage() {
        return 0;
    }

    public boolean isAlwaysSingular() {
        return false;
    }

}
