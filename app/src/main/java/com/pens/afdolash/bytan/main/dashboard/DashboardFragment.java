package com.pens.afdolash.bytan.main.dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;

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

    private TextView tvName, tvAddress, tvMessage, tvTemp, tvHeart, tvSpO2;
    private ImageView imgStatus;
    private int nCounter;

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
        tvSpO2 = (TextView) view.findViewById(R.id.tv_spO2);
        imgStatus = (ImageView) view.findViewById(R.id.img_status);

        // Get user data from shared preference
        prefUser = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        String name = prefUser.getString(EXTRAS_USER_NAME, "");

        // Get device data from shared preference
        prefDevice = getContext().getSharedPreferences(DEVICE_PREF, Context.MODE_PRIVATE);
        String address = prefDevice.getString(EXTRAS_DEVICE_ADDRESS, "");

        tvName.setText("Welcome back "+ name +"!");
        tvAddress.setText(address);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).changeState(8);
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
        List<BluetoothData> dataList = ((MainActivity)getActivity()).getDataList();

        if (dataList.size() != 0) {
            BluetoothData lastUpdate = dataList.get(dataList.size() - 1);

            tvTemp.setText(lastUpdate.getObjTemp());
            tvHeart.setText(lastUpdate.getHeartRate());
            tvSpO2.setText(lastUpdate.getSpO2());
        }

        nCounter++;
        Toast.makeText(getActivity(), "Count : "+ nCounter, Toast.LENGTH_SHORT).show();
        handler.postDelayed(runnable, 5000);
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
