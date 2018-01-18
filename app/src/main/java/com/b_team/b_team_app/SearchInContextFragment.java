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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchInContextFragment extends Fragment implements SearchListener{
    private SearchInContextFragment.OnSearchInContextFragmentInteractionListener mListener;
    private ListView listView;
    private TextView tv_categoryName, tv_contextName, tv_contextInfo;
    private SimpleCursorAdapter dataAdapter;
    private SearchCategory searchCategory;
    private SearchContext searchContext;
    private SearchActivity searchActivity;

    public SearchInContextFragment() {

    }

    public static SearchInContextFragment newInstance(SearchContext searchContext) {
        SearchInContextFragment fragment = new SearchInContextFragment();
        Bundle args = new Bundle();
        args.putSerializable("SearchContext", searchContext);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("SearchInContextFragment", "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_search_in_context, container, false);

        listView = (ListView) view.findViewById(R.id.searchResultList);
        tv_categoryName = (TextView) view.findViewById(R.id.textView_categoryName);
        tv_contextName = (TextView) view.findViewById(R.id.textView_contextName);
        tv_contextInfo = (TextView) view.findViewById(R.id.textView_contextInfo);

        Bundle args = getArguments();
        searchContext = (SearchContext) args.getSerializable("SearchContext");
        Log.d("searchContext", "Category: "+searchContext.getSearchCategory().getId()+"; Name: "+searchContext.getContextName()+"; Single: "+searchContext.isSingleContext());
        searchCategory = searchContext.getSearchCategory();
        tv_categoryName.setText(searchCategory.getName());
        tv_contextName.setText(searchContext.getContextName());
        tv_contextInfo.setText(getString(R.string.status_message_loading));

        String[] columns = new String[] {
                "name",
                "info"
        };

        int[] to = new int[] {
                R.id.itemName,
                R.id.itemInfo
        };

        dataAdapter = new SimpleCursorAdapter(getContext(), R.layout.search_result_item, null, columns, to, 0);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                int rowId;
                SearchContext newContext;

                //Check if we are in a category or a single context (e.g. one specific author)
                if (searchContext.isSingleContext()) {
                    //signal the search activity that a book was selected
                    rowId = cursor.getInt(cursor.getColumnIndexOrThrow(BooksTable.KEY_ID));
                    mListener.onBookSelected(rowId);
                } else {
                    //signal the search activity that a new context was selected
                    switch (searchCategory.getId()) {
                        case SearchCategories.CATEGORY_AUTHORS:
                            rowId = cursor.getInt(cursor.getColumnIndexOrThrow(AuthorsTable.KEY_ID));
                            newContext = new SearchContext(searchCategory, cursor.getString(cursor.getColumnIndexOrThrow(AuthorsTable.KEY_NAME)), true);
                            mListener.onNewContextSelected(newContext);
                            break;
                        case SearchCategories.CATEGORY_PUBLISHERS:
                            rowId = cursor.getInt(cursor.getColumnIndexOrThrow(PublishersTable.KEY_ID));
                            newContext = new SearchContext(searchCategory, cursor.getString(cursor.getColumnIndexOrThrow(PublishersTable.KEY_NAME)), true);
                            mListener.onNewContextSelected(newContext);
                            break;
                        case SearchCategories.CATEGORY_GENRES:
                            rowId = cursor.getInt(cursor.getColumnIndexOrThrow(GenresTable.KEY_ID));
                            newContext = new SearchContext(searchCategory, cursor.getString(cursor.getColumnIndexOrThrow(GenresTable.KEY_NAME)), true);
                            mListener.onNewContextSelected(newContext);
                            break;
                        default:
                            Log.e("SearchInContext", "Invalid category-id: " + searchCategory.getId());
                    }
                }
            }
        });
        if (!searchActivity.requestEmptySearch()) {
            throw new IllegalStateException("Search text not empty");
        }
        return view;
    }

    @Override
    public void onSearchComplete(Cursor data) {
        Log.d("SearchInContextFragment", "onSearchComplete()");
        Log.d("data", DatabaseUtils.dumpCursorToString(data));
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onAttach(Context context) {
        Log.d("SearchInContextFragment", "onAttach()");
        super.onAttach(context);
        if (context instanceof SearchStartFragment.OnSearchStartFragmentInteractionListener) {
            mListener = (SearchInContextFragment.OnSearchInContextFragmentInteractionListener) context;
            searchActivity = (SearchActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchStartFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("SearchInContextFragment", "onDetach()");
        super.onDetach();
        mListener = null;
    }

    public interface OnSearchInContextFragmentInteractionListener {
        void onNewContextSelected(SearchContext searchContext);
        void onBookSelected(int bookId);
    }
}
