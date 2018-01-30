package com.b_team.b_team_app;


import java.io.Serializable;

public class SearchContext implements Serializable{


    private SearchCategory searchCategory;
    private String contextName;

    //single context means that all the list items are books and therefor do not lead to a new context search
    private boolean isSingleContext;

    public SearchContext(SearchCategory searchCategory, String contextName, boolean isSingleContext) {
        this.searchCategory = searchCategory;
        this.contextName = contextName;
        this.isSingleContext = isSingleContext;
    }

    public SearchCategory getSearchCategory() {
        return searchCategory;
    }

    public String getContextName() {
        return contextName;
    }

    public boolean isSingleContext() {
        return isSingleContext;
    }

}
