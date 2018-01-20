package com.b_team.b_team_app;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BookDescriptionFragment extends DialogFragment {

    public BookDescriptionFragment() {
    }

    public static BookDescriptionFragment createBookDescriptionFragment(String description) {
        BookDescriptionFragment fragment = new BookDescriptionFragment();
        Bundle args = new Bundle();
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_book_description, null);
        TextView tvDescription = (TextView) view.findViewById(R.id.textView_description);
        tvDescription.setText(getArguments().getString("description"));
        builder.setView(view);
        builder.setPositiveButton(R.string.bookinfo_descriptionclose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
