package com.b_team.b_team_app;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class SearchInAllFragment extends Fragment implements SearchListener{

    private SearchInContextFragment.OnSearchInContextFragmentInteractionListener mListener;
    private LinearLayout listContainer;

    public SearchInAllFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_in_all, container, false);
        listContainer = (LinearLayout) getActivity().findViewById(R.id.listContainer);

        return view;
    }

    @Override
    public void onSearchComplete(Cursor data) {
        Log.d("SearchInContextFragment", "onSearchComplete()");
        Log.d("data", DatabaseUtils.dumpCursorToString(data));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchInContextFragment.OnSearchInContextFragmentInteractionListener) {
            mListener = (SearchInContextFragment.OnSearchInContextFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchInContextFragment.OnSearchInContextFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
