package com.pens.afdolash.bytan.main.dashboard.focus;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.view.LineChartView;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusTempFragment extends Fragment {

    private TextView tvTemperature;
    private LineChartView chartTemperature;
    private RelativeLayout rvStart;

    private int counter;

    private Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getBodyData();
        }
    };

    public FocusTempFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_temp, container, false);

        tvTemperature = (TextView) view.findViewById(R.id.tv_temperature);
        chartTemperature = (LineChartView) view.findViewById(R.id.chart_temperature);
        rvStart = (RelativeLayout) view.findViewById(R.id.rv_start);

        rvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvStart.setClickable(false);
                rvStart.setAlpha(0.5f);

                // Send state ACTIVE to Arduino
                final Handler handler = new Handler();
                ((MainActivity)getActivity()).changeState(7);

                updateBodyData();
            }
        });

        return view;
    }

    private void getBodyData() {
        List<BluetoothData> dataList = ((MainActivity)getActivity()).getDataList();

        if (dataList.size() != 0) {
            int bodyCode = dataList.get(dataList.size() - 1).getCode();
            BluetoothData lastUpdate = dataList.get(dataList.size() - 1);

            if (bodyCode == 99) {
                tvTemperature.setText(lastUpdate.getObjTemp());

                counter++;

                if (counter == 15) {
                    rvStart.setClickable(true);
                    rvStart.setAlpha(1f);
                    tvTemperature.setText(lastUpdate.getObjTemp());
                }
            }
        }

        if (counter == 15) {
            handler.removeCallbacks(runnable);
            counter = 0;
        } else {
            handler.postDelayed(runnable, 1000);
        }
    }

    private void updateBodyData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getBodyData();
            }
        });
    }
}
