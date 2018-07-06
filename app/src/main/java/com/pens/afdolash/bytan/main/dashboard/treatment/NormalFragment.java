package com.pens.afdolash.bytan.main.dashboard.treatment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pens.afdolash.bytan.R;

import static android.content.Context.MODE_PRIVATE;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_GENDER;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;

public class NormalFragment extends Fragment {

    private final String GENDER_MALE = "Male";
    private final String GENDER_FEMALE = "Female";

    private SharedPreferences preferences;

    private ImageView imgAlert;

    public NormalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_normal, container, false);

        imgAlert = (ImageView) view.findViewById(R.id.img_alert);

        preferences = getContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
        String gender = preferences.getString(EXTRAS_USER_GENDER, null);

        if (gender.equals(GENDER_FEMALE))
            imgAlert.setImageResource(R.drawable.ai_woman_rest);
        else
            imgAlert.setImageResource(R.drawable.ai_man_rest);

        return view;
    }
}
