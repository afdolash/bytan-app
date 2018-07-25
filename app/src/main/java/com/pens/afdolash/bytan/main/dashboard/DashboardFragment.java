package com.pens.afdolash.bytan.main.dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.util.Tools;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;
import com.pens.afdolash.bytan.main.dashboard.focus.FocusHeartFragment;
import com.pens.afdolash.bytan.main.dashboard.focus.FocusTempFragment;
import com.pens.afdolash.bytan.main.dashboard.focus.PeltierTempFragment;
import com.pens.afdolash.bytan.main.dashboard.treatment.EmergencyFragment;
import com.pens.afdolash.bytan.main.dashboard.treatment.HyphoFragment;
import com.pens.afdolash.bytan.main.dashboard.treatment.NormalFragment;
import com.pens.afdolash.bytan.main.dashboard.treatment.RestFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.DEVICE_PREF;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_ADDRESS;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_NAME;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private SharedPreferences prefUser, prefDevice;

    private TextView tvName, tvAddress, tvMessage, tvTemp, tvHeart, tvHeater, tvStatus, tvLabelTreatment, tvLastUpdate;
    private ImageView imgRefresh;
    private LinearLayout lnTemperature, lnHeart, lnTreatment, lnHeater;

    private Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getBodyData();
        }
    };


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
        tvTemp = (TextView) view.findViewById(R.id.tv_temperature);
        tvHeart = (TextView) view.findViewById(R.id.tv_heart);
        tvHeater = (TextView) view.findViewById(R.id.tv_heater);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        tvLabelTreatment = (TextView) view.findViewById(R.id.tv_label_treatment);
        tvLastUpdate = (TextView) view.findViewById(R.id.tv_lastupdate);
        imgRefresh = (ImageView) view.findViewById(R.id.img_refresh);
        lnHeart = (LinearLayout) view.findViewById(R.id.ln_heart);
        lnTemperature = (LinearLayout) view.findViewById(R.id.ln_temperature);
        lnTreatment = (LinearLayout) view.findViewById(R.id.ln_treatment);
        lnHeater = (LinearLayout) view.findViewById(R.id.ln_heater);

        // Get user data from shared preference
        prefUser = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String name = prefUser.getString(EXTRAS_USER_NAME, "");

        // Get device data from shared preference
        prefDevice = getContext().getSharedPreferences(DEVICE_PREF, Context.MODE_PRIVATE);
        String address = prefDevice.getString(EXTRAS_DEVICE_ADDRESS, "");

        tvName.setText("Welcome back "+ name +"!");
        tvAddress.setText(address);

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).changeState("!!8");
            }
        });

        lnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).focusFragment = new FocusHeartFragment();
                ((MainActivity) getActivity()).loadFragment(((MainActivity) getActivity()).focusFragment);
            }
        });

        lnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).focusFragment = new FocusTempFragment();
                ((MainActivity) getActivity()).loadFragment(((MainActivity) getActivity()).focusFragment);
            }
        });

        lnHeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).focusFragment = new PeltierTempFragment();
                ((MainActivity) getActivity()).loadFragment(((MainActivity) getActivity()).focusFragment);
            }
        });

        updateBodyData();

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }


    private void getBodyData() {
        List<BluetoothData> dataList = ((MainActivity) getActivity()).getDataList();

        if (dataList.size() != 0) {
            BluetoothData lastUpdate = dataList.get(dataList.size() - 1);
            int bodyCode = lastUpdate.getCode();

            if (bodyCode != 99) {
                tvTemp.setText(lastUpdate.getObjTemp());
                tvHeart.setText(lastUpdate.getHeartRate());
                tvHeater.setText(lastUpdate.getAmbTemp());

                if (lastUpdate.getCode() == 0) {
                    tvLabelTreatment.setVisibility(View.VISIBLE);
                    tvStatus.setText("HEALTY");
                    tvMessage.setText(R.string.message_healthy);
                    showTreatment(new NormalFragment());
                } else if (lastUpdate.getCode() == 1) {
                    tvLabelTreatment.setVisibility(View.VISIBLE);
                    tvStatus.setText("REST");
                    tvMessage.setText(R.string.message_rest);
                    showTreatment(new RestFragment());
                } else if (lastUpdate.getCode() == 2) {
                    tvLabelTreatment.setVisibility(View.VISIBLE);
                    tvStatus.setText("HYPOTHERMIA");
                    tvMessage.setText(R.string.message_hipotermia);
                    showTreatment(new HyphoFragment());
                } else if (lastUpdate.getCode() == 3) {
                    tvLabelTreatment.setVisibility(View.VISIBLE);
                    tvStatus.setText("EMERGENCY");
                    tvMessage.setText(R.string.message_emergency);
                    showTreatment(new EmergencyFragment());
                }
            }
        }

        String s = ((MainActivity) getActivity()).lastUpdate;
        if (s != null) {
            String date = new SimpleDateFormat("HHmmss").format(new Date());
            float timestamp = Math.abs(Float.parseFloat(date) - Float.parseFloat(s));

            if (timestamp / 60 > 59) {
                tvLastUpdate.setText(String.format("%.0f", timestamp / 3600) +" hours ago");
            }else if (timestamp > 59) {
                tvLastUpdate.setText(String.format("%.0f", timestamp / 60) +" minutes ago");
            } else {
                tvLastUpdate.setText(String.format("%.0f", timestamp) +" seconds ago");
            }
        }

        handler.postDelayed(runnable, 5000);
    }

    private void showTreatment(final Fragment fragment) {
        lnTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).focusFragment = fragment;
                ((MainActivity) getActivity()).loadFragment(((MainActivity) getActivity()).focusFragment);
            }
        });
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
