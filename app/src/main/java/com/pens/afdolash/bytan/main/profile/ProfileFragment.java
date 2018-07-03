package com.pens.afdolash.bytan.main.profile;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.profile.adapter.HistoryAdapter;
import com.pens.afdolash.bytan.other.RecyclerTouchListener;
import com.pens.afdolash.bytan.main.profile.model.History;
import com.pens.afdolash.bytan.other.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_NAME;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private SharedPreferences preferences;

    private List<History> histories = new ArrayList<>();
    private DatabaseHelper db;

    private TextView tvTitle;
    private RecyclerView rcHistory;
    private LinearLayout lnEmpty;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        rcHistory = (RecyclerView) view.findViewById(R.id.rc_history);
        lnEmpty = (LinearLayout) view.findViewById(R.id.ln_empty);

        db = new DatabaseHelper(getContext());
        histories.addAll(db.getAllHistory());

        preferences = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String name = preferences.getString(EXTRAS_USER_NAME, "Guest");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcHistory.setLayoutManager(layoutManager);
        rcHistory.setItemAnimator(new DefaultItemAnimator());
        rcHistory.setAdapter(new HistoryAdapter(getContext(), histories));

        rcHistory.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rcHistory, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        tvTitle.setText(name +"'s\nHistory");

        toggleEmptyHistory();

        return view;
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteHistory(int position) {
        // deleting the note from db
        db.deleteHistory(histories.get(position));

        // removing the note from the list
        histories.remove(position);
        rcHistory.getAdapter().notifyItemRemoved(position);

        toggleEmptyHistory();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteHistory(position);
            }
        });
        builder.show();
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyHistory() {
        // You can check notesList.size() > 0

        if (db.getHistoryCount() > 0) {
            lnEmpty.setVisibility(View.GONE);
        }
        else {
            lnEmpty.setVisibility(View.VISIBLE);
        }
    }
}
