package com.pens.afdolash.bytan.main.group;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.listeners.ClientConnectedListener;
import com.abemart.wroup.common.listeners.ClientDisconnectedListener;
import com.abemart.wroup.common.listeners.DataReceivedListener;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.MainActivity;

import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_IS_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_NAME;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_PREF;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment implements DataReceivedListener, ClientConnectedListener, ClientDisconnectedListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String groupName;
    private String groupOwner;
    private boolean isGroupOwner = false;

    private WroupService wroupService;
    private WroupClient wroupClient;

    private Button btnSend;
    private EditText etMessage;
    private FrameLayout frBottomSheet;
    private CardView cardBottomSheet;
    private RelativeLayout rvLocation;
    private TextView tvNameGroup, tvNameMaster, tvLeave;
    private RecyclerView rcMember;
    private BottomSheetBehavior sheetBehavior;

    public MemberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        btnSend = (Button) view.findViewById(R.id.btn_send);
        etMessage = (EditText) view.findViewById(R.id.et_message);
        frBottomSheet = (FrameLayout) view.findViewById(R.id.fr_bottom_sheet);
        cardBottomSheet = (CardView) view.findViewById(R.id.card_bottom_sheet);
        rvLocation = (RelativeLayout) view.findViewById(R.id.rv_location);
        tvNameGroup = (TextView) view.findViewById(R.id.tv_name_group);
        tvNameMaster = (TextView) view.findViewById(R.id.tv_name_master);
        tvLeave = (TextView) view.findViewById(R.id.tv_leave);
        rcMember = (RecyclerView) view.findViewById(R.id.rc_member);

        sheetBehavior = BottomSheetBehavior.from(frBottomSheet);

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

        preferences = getActivity().getSharedPreferences(GROUP_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();

        groupName = preferences.getString(EXTRAS_GROUP_NAME, null);
        groupOwner = preferences.getString(EXTRAS_GROUP_OWNER, null);
        isGroupOwner = preferences.getBoolean(EXTRAS_GROUP_IS_OWNER, false);

        if (isGroupOwner) {
            wroupService = ((MainActivity) getActivity()).mWroupService;
            wroupService.setDataReceivedListener(this);
            wroupService.setClientDisconnectedListener(this);
            wroupService.setClientConnectedListener(this);
        } else {
            wroupClient = ((MainActivity) getActivity()).mWroupClient;
            wroupClient.setDataReceivedListener(this);
            wroupClient.setClientDisconnectedListener(this);
            wroupClient.setClientConnectedListener(this);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageStr = etMessage.getText().toString();
                if (messageStr != null && !messageStr.isEmpty()) {
                    MessageWrapper normalMessage = new MessageWrapper();
                    normalMessage.setMessage(etMessage.getText().toString());
                    normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

                    if (isGroupOwner) {
                        wroupService.sendMessageToAllClients(normalMessage);
                    } else {
                        wroupClient.sendMessageToAllClients(normalMessage);
                    }

                    etMessage.setText("");
                }
            }
        });

        tvLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wroupService != null) wroupService.disconnect();
                if (wroupClient != null) wroupClient.disconnect();

                editor.putString(EXTRAS_GROUP_NAME, null);
                editor.putString(EXTRAS_GROUP_OWNER, null);
                editor.putBoolean(EXTRAS_GROUP_IS_OWNER, false);
                editor.commit();

                ((MainActivity) getActivity()).destroyFragment(MemberFragment.this);
                ((MainActivity) getActivity()).loadFragment(new GroupFragment());
            }
        });

        return view;
    }

    @Override
    public void onClientConnected(final WroupDevice wroupDevice) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), wroupDevice.getDeviceName() +" is connected.", Toast.LENGTH_LONG).show();
                Log.i("Member", wroupDevice.getDeviceName() +" is connected.");
            }
        });
    }

    @Override
    public void onClientDisconnected(final WroupDevice wroupDevice) {
       getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), wroupDevice.getDeviceName() +" is disconnected.", Toast.LENGTH_LONG).show();
                Log.i("Member", wroupDevice.getDeviceName() +" is disconnected.");
            }
        });
    }

    @Override
    public void onDataReceived(final MessageWrapper messageWrapper) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) getActivity()).messageReceiver.setMessage(getActivity(), messageWrapper, MessageReceiver.TYPE_NORMAL);

                messageWrapper.getMessage().toString();
                Toast.makeText(getActivity(), messageWrapper.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
