package com.pens.afdolash.bytan.main.dashboard.focus;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PeltierTempFragment extends Fragment {

    private Croller croller;
    private RelativeLayout lnPower, lnCool, lnWarm, lnHot;

    private int ambLast;
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
        lnPower = (RelativeLayout) view.findViewById(R.id.ln_power);
        lnCool = (RelativeLayout) view.findViewById(R.id.ln_cool);
        lnWarm = (RelativeLayout) view.findViewById(R.id.ln_warm);
        lnHot = (RelativeLayout) view.findViewById(R.id.ln_hot);

        dataList = ((MainActivity)getActivity()).getDataList();

        if (dataList.size() > 0)
            ambLast = Math.round(Float.parseFloat(dataList.get(dataList.size() - 1).getAmbTemp()));

        croller.setProgress(ambLast);
        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            int progressValue = ambLast;

            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // Use the progress
                progressValue = progress;
                croller.setLabel(progressValue +"°C");
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
            }
        });

        lnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cooling...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##0");
                croller.setProgress(0);
            }
        });

        lnCool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Warming...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##20");
                croller.setProgress(20);
            }
        });

        lnWarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Warming...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("##40");
                croller.setProgress(40);
            }
        });

        lnHot.setOnClickListener(new View.OnClickListener() {
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
