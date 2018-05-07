package com.pens.afdolash.bytan.main.profile;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;

import static com.pens.afdolash.bytan.SplashActivity.USER_NAME;
import static com.pens.afdolash.bytan.SplashActivity.USER_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private SharedPreferences preferences;

    private TextView tvTitle;
    private RecyclerView rcHistory;

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

        preferences = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String name = preferences.getString(USER_NAME, "Guest");

        tvTitle.setText(name +"'s\nHistory");

        return view;
    }

}
