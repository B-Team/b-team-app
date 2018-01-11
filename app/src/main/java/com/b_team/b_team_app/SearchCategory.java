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
