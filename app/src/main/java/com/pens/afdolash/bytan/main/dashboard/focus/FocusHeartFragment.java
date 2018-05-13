package com.pens.afdolash.bytan.main.dashboard.focus;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.view.LineChartView;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusHeartFragment extends Fragment {

    private TextView tvHeart;
    private LineChartView chartHeart;
    private RelativeLayout rvStart;

    private int counter = 0;

    private Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getBodyData();
        }
    };

    public FocusHeartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_heart, container, false);

        tvHeart = (TextView) view.findViewById(R.id.tv_heart);
        chartHeart = (LineChartView) view.findViewById(R.id.chart_heart);
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
                tvHeart.setText(lastUpdate.getHeartRate());

                Toast.makeText(getContext(), "Counter:" + counter, Toast.LENGTH_SHORT).show();
                counter++;

                if (counter == 15) {
                    rvStart.setClickable(true);
                    rvStart.setAlpha(1f);
                    tvHeart.setText(lastUpdate.getHeartRate());
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

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
