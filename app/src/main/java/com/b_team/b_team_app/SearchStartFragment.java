package com.b_team.b_team_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class SearchStartFragment extends Fragment {

    private OnSearchStartFragmentInteractionListener mListener;
    private GridView gridView;

    public SearchStartFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_start, container, false);

        gridView = (GridView) view.findViewById(R.id.categoryGridView);
        gridView.setAdapter(new SearchCategoryAdapter(getContext(), R.layout.search_category_item, SearchCategory.CATEGORIES));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onSearchStartFragmentInteraction((SearchCategory) parent.getItemAtPosition(position));
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchStartFragmentInteractionListener) {
            mListener = (OnSearchStartFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchStartFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSearchStartFragmentInteractionListener {
        void onSearchStartFragmentInteraction(SearchCategory item);
    }
}
