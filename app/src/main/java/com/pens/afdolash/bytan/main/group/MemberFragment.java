package com.pens.afdolash.bytan.main.group;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.main.MainActivity;
import com.pens.afdolash.bytan.main.group.adapter.MemberAdapter;
import com.pens.afdolash.bytan.main.group.model.MemberData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_IS_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_NAME;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    private SharedPreferences prefGroup, prefUser;
    private SharedPreferences.Editor editGroup;
    private String groupName;
    private String groupOwner;
    private boolean isGroupOwner = false;

    private WroupService wroupService;
    private WroupClient wroupClient;

    private FrameLayout frBottomSheet;
    private CardView cardBottomSheet;
    private RelativeLayout rvLocation, rvBroadcast;
    private LinearLayout lnEmpty;
    private TextView tvNameGroup, tvNameMaster;
    private ImageView imgLeave;
    private RecyclerView rcMember;
    private BottomSheetBehavior sheetBehavior;

    private Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getMemberData();
        }
    };

    private DisplayMetrics metrics;

    private double myLongitude, myLatitude; //My longitude and latitude
    private double mapLongitude = 180 - myLongitude;  //Set maximum longitude
    private double mapLatitude = myLatitude - 90; //Set maximum latitude

    public MemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        frBottomSheet = (FrameLayout) view.findViewById(R.id.fr_bottom_sheet);
        cardBottomSheet = (CardView) view.findViewById(R.id.card_bottom_sheet);
        rvLocation = (RelativeLayout) view.findViewById(R.id.rv_location);
        rvBroadcast = (RelativeLayout) view.findViewById(R.id.rv_broadcast);
        lnEmpty = (LinearLayout) view.findViewById(R.id.ln_empty);
        tvNameGroup = (TextView) view.findViewById(R.id.tv_name_group);
        tvNameMaster = (TextView) view.findViewById(R.id.tv_name_master);
        imgLeave = (ImageView) view.findViewById(R.id.img_leave);
        rcMember = (RecyclerView) view.findViewById(R.id.rc_member);

        sheetBehavior = BottomSheetBehavior.from(frBottomSheet);

        metrics = ((MainActivity) getActivity()).metrics;

        // Check if location is available
        myLatitude = ((MainActivity) getActivity()).tracker.getLatitude();
        myLongitude = ((MainActivity) getActivity()).tracker.getLongitude();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcMember.setLayoutManager(layoutManager);
        rcMember.setItemAnimator(new DefaultItemAnimator());
        rcMember.setAdapter(new MemberAdapter(getContext(), ((MainActivity) getActivity()).getMemberList()));

        cardBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        prefUser = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        prefGroup = getActivity().getSharedPreferences(GROUP_PREF, Context.MODE_PRIVATE);
        editGroup = prefGroup.edit();

        groupName = prefGroup.getString(EXTRAS_GROUP_NAME, "Anonymous");
        groupOwner = prefGroup.getString(EXTRAS_GROUP_OWNER, "Anonymous");
        isGroupOwner = prefGroup.getBoolean(EXTRAS_GROUP_IS_OWNER, false);

        tvNameGroup.setText(groupName +"'s Group");
        tvNameMaster.setText(groupOwner);

        if (isGroupOwner) {
            wroupService = ((MainActivity) getActivity()).mWroupService;
            wroupService.setDataReceivedListener((MainActivity) getActivity());
            wroupService.setClientDisconnectedListener((MainActivity) getActivity());
            wroupService.setClientConnectedListener((MainActivity) getActivity());
        } else {
            wroupClient = ((MainActivity) getActivity()).mWroupClient;
            wroupClient.setDataReceivedListener((MainActivity) getActivity());
            wroupClient.setClientDisconnectedListener((MainActivity) getActivity());
            wroupClient.setClientConnectedListener((MainActivity) getActivity());
        }

        rvBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataFirstTime();
            }
        });

        imgLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).unregisterReceiver(((MainActivity) getActivity()).mWifiDirectReceiver);
                if (wroupService != null) wroupService.disconnect();
                if (wroupClient != null) wroupClient.disconnect();

                editGroup.putString(EXTRAS_GROUP_NAME, null);
                editGroup.putString(EXTRAS_GROUP_OWNER, null);
                editGroup.putBoolean(EXTRAS_GROUP_IS_OWNER, false);
                editGroup.commit();

                ((MainActivity) getActivity()).destroyFragment(MemberFragment.this);
                ((MainActivity) getActivity()).loadFragment(new GroupFragment());
            }
        });

        // Create my view on location relative view
        View child = getLayoutInflater().inflate(R.layout.item_user_man, null);
        child.setX(posX(myLongitude) - 85f);
        child.setY(posY(myLatitude) - 125f);

        ImageView imgStatus = (ImageView) child.findViewById(R.id.img_status);
        TextView tvName = (TextView) child.findViewById(R.id.tv_name);

        imgStatus.setVisibility(View.GONE);
        tvName.setText("Me");

        rvLocation.addView(child);

        // Send data first time when connected
        sendDataFirstTime();

        // Update data repeatly
        updateMemberData();

        toggleEmptyMember();

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void getMemberData() {
//        rcMember.setAdapter(new MemberAdapter(getContext(), ((MainActivity) getActivity()).getMemberList()));
        rcMember.getAdapter().notifyDataSetChanged();

        for (int i = rvLocation.getChildCount(); i > 2; i--) {
            rvLocation.removeViewAt(i - 1);
        }

        List<MemberData> memberList = ((MainActivity) getActivity()).getMemberList();
        for (MemberData member : memberList) {
            View child = getLayoutInflater().inflate(R.layout.item_user_man, null);
            child.setX(posX(Double.parseDouble(member.getLongitude())) - 85f);
            child.setY(posY(Double.parseDouble(member.getLatitude())) - 135f);

            ImageView imgStatus = (ImageView) child.findViewById(R.id.img_status);
            TextView tvName = (TextView) child.findViewById(R.id.tv_name);

            tvName.setText(member.getDevice().getDeviceName());

            Location memberLoc = new Location("");
            memberLoc.setLatitude(Double.parseDouble(member.getLatitude()));
            memberLoc.setLongitude(Double.parseDouble(member.getLongitude()));

            Location myLoc = new Location("");
            myLoc.setLatitude(myLatitude);
            myLoc.setLongitude(myLongitude);

            switch (Integer.parseInt(member.getCode())) {
                case 0:
                    imgStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusHealthy), android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 1:
                    imgStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusRest), android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 2:
                    imgStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusHyphothermia), android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 3:
                    imgStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusEmergency), android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                default:
                    imgStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusIdentified), android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
            }

            rvLocation.addView(child);
        }
        toggleEmptyMember();

        handler.postDelayed(runnable, 1000);
    }

    private void updateMemberData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMemberData();
            }
        });
    }

    private void sendDataFirstTime() {
        String messageStr = null;
        List<BluetoothData> dataList = ((MainActivity) getActivity()).getDataList();

        String date = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        if (dataList.size() > 0) {
            BluetoothData lastUpdate = dataList.get(dataList.size() - 1);
            messageStr = "{"
                    + "heart : "+ lastUpdate.getHeartRate() +", "
                    + "obj : "+ lastUpdate.getObjTemp() +", "
                    + "amb : "+ lastUpdate.getAmbTemp() +", "
                    + "code : "+ lastUpdate.getCode() +", "
                    + "latitude : "+ myLatitude +", "
                    + "longitude : "+ myLongitude +", "
                    + "timestamp : "+ date
                    + "}";
        } else {
            messageStr = "{"
                    + "heart : "+ 0.0 +", "
                    + "obj : "+ 0.0 +", "
                    + "amb : "+ 0.0 +", "
                    + "code : "+ 0 +", "
                    + "latitude : "+ myLatitude +", "
                    + "longitude : "+ myLongitude +", "
                    + "timestamp : "+ date
                    + "}";
        }

        if (!messageStr.isEmpty()) {
            MessageWrapper normalMessage = new MessageWrapper();
            normalMessage.setMessage(messageStr);
            normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

            if (isGroupOwner) {
                ((MainActivity) getActivity()).mWroupService.sendMessageToAllClients(normalMessage);
            } else {
                ((MainActivity) getActivity()).mWroupClient.sendMessageToAllClients(normalMessage);
            }

            Toast.makeText(getContext(), "Broadcasting...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyMember() {
        // You can check notesList.size() > 0

        if (((MainActivity) getActivity()).getMemberList().size() > 0) {
            lnEmpty.setVisibility(View.GONE);
        }
        else {
            lnEmpty.setVisibility(View.VISIBLE);
        }
    }

    private float posX(Double longitude) {
        longitude = longitude - myLongitude;
        float x = (float) ((metrics.widthPixels / 2) * ((longitude / mapLongitude) * 500000)) + (metrics.widthPixels / 2);
        return  x > metrics.widthPixels ? metrics.widthPixels : x;
    }

    private float posY(Double latitude) {
        latitude = myLatitude - latitude;
        float y = (float) (((metrics.heightPixels / 2) * ((latitude / mapLatitude) * 500000)) + (metrics.heightPixels / 2));
        return  y > metrics.heightPixels ? metrics.heightPixels : y;
    }
}
