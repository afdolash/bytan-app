package com.pens.afdolash.bytan.main.dashboard.focus;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusHeartFragment extends Fragment {

    private TextView tvHeart, tvNote;
    private LineChartView chartHeart;
    private RelativeLayout rvStart;

    private boolean isStop = true;

    private List<BluetoothData> dataTemp = new ArrayList<>();
    private LineSet dataset;
    private final String[]  mLabels = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private float[] mValue = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

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
        tvNote = (TextView) view.findViewById(R.id.tv_note);
        chartHeart = (LineChartView) view.findViewById(R.id.chart_heart);
        rvStart = (RelativeLayout) view.findViewById(R.id.rv_start);

        rvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNote.setVisibility(View.VISIBLE);
                rvStart.setClickable(false);
                rvStart.setAlpha(0.5f);
                dataTemp.clear();
                isStop = false;

                // Send state ACTIVE to Arduino
                final Handler handler = new Handler();
                ((MainActivity)getActivity()).changeState(7);

                updateBodyData();
            }
        });

        dataset = new LineSet(mLabels, mValue);
        dataset.setColor(Color.parseColor("#0D87FF"))
                .setThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .beginAt(0)
                .endAt(15);

        for (int i = 0; i < mValue.length; i++) {
            Point point = (Point) dataset.getEntry(i);
            point.setColor(Color.parseColor("#0CE8A2"));
        }

        chartHeart.addData(dataset);

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#0079AE"));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        chartHeart.setXLabels(AxisRenderer.LabelPosition.NONE)
                .setYLabels(AxisRenderer.LabelPosition.NONE)
                .setValueThreshold(60f, 100f, thresPaint)
                .setAxisBorderValues(0, 200)
                .show();

        return view;
    }

    private void getBodyData() {
        List<BluetoothData> dataList = ((MainActivity)getActivity()).getDataList();

        if (dataList.size() != 0) {
            BluetoothData lastUpdate = dataList.get(dataList.size() - 1);
            int bodyCode = lastUpdate.getCode();

            if (bodyCode == 99 && !dataTemp.contains(lastUpdate)) {
                dataTemp.add(lastUpdate);
                tvHeart.setText(lastUpdate.getHeartRate());

                for (int i = 0; i < mValue.length - 1; i++) {
                    mValue[i] = mValue[i + 1];
                }
                mValue[mValue.length - 1] = Float.parseFloat(lastUpdate.getHeartRate());
                chartHeart.updateValues(0, mValue);
                chartHeart.notifyDataUpdate();
            }

            if (dataTemp.size() >= 15){
                float sumDataHeart = 0;

                // Checkpoint
                for (BluetoothData data : dataTemp) {
                    sumDataHeart += Float.parseFloat(data.getHeartRate());
                }

                tvNote.setVisibility(View.GONE);
                rvStart.setClickable(true);
                rvStart.setAlpha(1f);
                tvHeart.setText(String.format("%.2f", (sumDataHeart / dataTemp.size())));
                isStop = true;
            }
        }

        if (isStop) {
            handler.removeCallbacks(runnable);
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
