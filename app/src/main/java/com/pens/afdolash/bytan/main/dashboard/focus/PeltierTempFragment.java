package com.pens.afdolash.bytan.main.dashboard.focus;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import java.util.ArrayList;
import java.util.List;

import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_AUTO;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeltierTempFragment extends Fragment {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Croller croller;
    private RelativeLayout rvAuto, rvPower, rvHalf, rvMax;
    private ImageView imgAuto;

    private List<BluetoothData> dataList = new ArrayList<>();

    public PeltierTempFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peltier_temp, container, false);

        croller = (Croller) view.findViewById(R.id.croller);
        rvAuto = (RelativeLayout) view.findViewById(R.id.rv_auto);
        rvPower = (RelativeLayout) view.findViewById(R.id.rv_power);
        rvHalf = (RelativeLayout) view.findViewById(R.id.rv_half);
        rvMax = (RelativeLayout) view.findViewById(R.id.rv_max);
        imgAuto = (ImageView) view.findViewById(R.id.img_auto);

        preferences = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();

        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            int progressValue = 0;

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // Use the progress
                progressValue = progress;
                croller.setLabel(progressValue +"~");
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // Tracking started
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // Tracking stopped
                Toast.makeText(getContext(), "Warming...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##"+ progressValue);
                Log.i("Peltier Value", String.valueOf(progressValue));
            }
        });


        boolean b = preferences.getBoolean(EXTRAS_USER_AUTO, true);
        if (b) {
            imgAuto.setImageResource(R.drawable.ai_temp_auto_on);
        } else {
            imgAuto.setImageResource(R.drawable.ai_temp_auto);
        }

        rvAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = preferences.getBoolean(EXTRAS_USER_AUTO, true);

                if (b) {
                    Toast.makeText(getContext(), "Auto is off", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).changeState("@@0");
                    editor.putBoolean(EXTRAS_USER_AUTO, false);
                    imgAuto.setImageResource(R.drawable.ai_temp_auto);
                } else {
                    Toast.makeText(getContext(), "Auto is on", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).changeState("@@1");
                    editor.putBoolean(EXTRAS_USER_AUTO, true);
                    imgAuto.setImageResource(R.drawable.ai_temp_auto_on);
                }
                editor.apply();
            }
        });

        rvPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cooling...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##0");
                croller.setProgress(0);
            }
        });

        rvHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Warming...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##30");
                croller.setProgress(30);
            }
        });

        rvMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Warming...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##60");
                croller.setProgress(60);
            }
        });

        return view;
    }
}
