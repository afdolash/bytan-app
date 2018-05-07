package com.pens.afdolash.bytan.main.dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.intro.IntroductionActivity;
import com.pens.afdolash.bytan.main.MainActivity;

import static com.pens.afdolash.bytan.SplashActivity.USER_NAME;
import static com.pens.afdolash.bytan.SplashActivity.USER_PREF;
import static com.pens.afdolash.bytan.main.MainActivity.EXTRAS_DEVICE_ADDRESS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private SharedPreferences preferences;

    private TextView tvName, tvAddress, tvMessage;
    private ImageView imgStatus;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
        imgStatus = (ImageView) view.findViewById(R.id.img_status);

        preferences = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String name = preferences.getString(USER_NAME, "");

        final Intent intent = getActivity().getIntent();
        String address = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        tvName.setText("Welcome back "+ name +"!");
        tvAddress.setText(address);

        return view;
    }

}
