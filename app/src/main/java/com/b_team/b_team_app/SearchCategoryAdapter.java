package com.b_team.b_team_app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchCategoryAdapter extends ArrayAdapter<SearchCategory> {

    private ArrayList<SearchCategory> categories;

    public SearchCategoryAdapter(Context context, int textViewResourceId, ArrayList<SearchCategory> searchCategories) {
        super(context, textViewResourceId, searchCategories);
        categories = searchCategories;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_category_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView_categoryName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_categoryIcon);

        textView.setText(getContext().getString(categories.get(position).getName()));
        imageView.setImageResource(categories.get(position).getIcon());

        return convertView;
    }


}
