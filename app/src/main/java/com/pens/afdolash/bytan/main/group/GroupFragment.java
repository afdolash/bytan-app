package com.pens.afdolash.bytan.main.group;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiP2PError;
import com.abemart.wroup.common.WroupServiceDevice;
import com.abemart.wroup.common.listeners.ServiceDiscoveredListener;
import com.abemart.wroup.common.listeners.ServiceRegisteredListener;
import com.abemart.wroup.service.WroupService;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.MainActivity;
import com.pens.afdolash.bytan.main.group.adapter.WifiDirectAdapter;
import com.pens.afdolash.bytan.main.group.dialog.GroupCreationDialog;
import com.pens.afdolash.bytan.main.group.dialog.GroupDiscoveryDialog;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements GroupCreationDialog.GroupCreationListener, GroupDiscoveryDialog.GroupDiscoveryListener {
    public static final String GROUP_TAG = GroupFragment.class.getSimpleName();

    public static final String GROUP_PREF = "GROUP_PREF";
    public static final String EXTRAS_GROUP_NAME = "GROUP_NAME";
    public static final String EXTRAS_GROUP_OWNER = "GROUP_OWNER";
    public static final String EXTRAS_GROUP_IS_OWNER = "GROUP_IS_OWNER";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private GroupCreationDialog createGroupDialog;
    private GroupDiscoveryDialog discoverGroupDialog;
    private WroupClient wroupClient;
    private WroupService wroupService;

    private RelativeLayout rvCreate, rvDiscover;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        rvCreate = (RelativeLayout) view.findViewById(R.id.rv_create);
        rvDiscover = (RelativeLayout) view.findViewById(R.id.rv_discover);

        preferences = getActivity().getSharedPreferences(GROUP_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();

        final WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wroupClient = ((MainActivity) getActivity()).mWroupClient;
        wroupService = ((MainActivity) getActivity()).mWroupService;

        rvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupDialog = new GroupCreationDialog();
                createGroupDialog.setGroupCreationListener(GroupFragment.this);
                createGroupDialog.show(getChildFragmentManager(), GroupCreationDialog.class.getSimpleName());

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }

                if (!wifiManager.isP2pSupported()) {
                    Toast.makeText(getContext(), "Sorry, you can\'t use this feature.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverGroupDialog = new GroupDiscoveryDialog();
                discoverGroupDialog.setGroupDiscoveryListener(GroupFragment.this);
                discoverGroupDialog.show(getChildFragmentManager(), GroupDiscoveryDialog.class.getSimpleName());

                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }

                if (!wifiManager.isP2pSupported()) {
                    Toast.makeText(getContext(), "Sorry, you can\'t use this feature.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onGroupCreated(final String groupName) {
        if (!groupName.isEmpty()) {
            wroupService.registerService(groupName, new ServiceRegisteredListener() {
                @Override
                public void onSuccessServiceRegistered() {
                    createGroupDialog.dismiss();
                    ((MainActivity) getActivity()).loadFragment(new MemberFragment());

                    editor.putString(EXTRAS_GROUP_NAME, groupName);
                    editor.putString(EXTRAS_GROUP_OWNER, "You are the owner! ");
                    editor.putBoolean(EXTRAS_GROUP_IS_OWNER, true);
                    editor.commit();

                    Log.i(GROUP_TAG, "Group created. Launching GroupChatActivity...");
                    Toast.makeText(getContext(), "Group created.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onErrorServiceRegistered(WiFiP2PError wiFiP2PError) {
                    Toast.makeText(getContext(), "Please make sure your Wi-Fi in on!"+ wiFiP2PError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please, insert a group name.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupDiscovered(final RecyclerView rcWifi, final TextView tvStatus) {
        if (rcWifi != null) {
            wroupClient.discoverServices(5000L, new ServiceDiscoveredListener() {
                @Override
                public void onNewServiceDeviceDiscovered(WroupServiceDevice serviceDevice) {
                    Log.i(GROUP_TAG, "New group found:");
                    Log.i(GROUP_TAG, "\tName: " + serviceDevice.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME));
                }

                @Override
                public void onFinishServiceDeviceDiscovered(List<WroupServiceDevice> serviceDevices) {
                    Log.i(GROUP_TAG, "Found '" + serviceDevices.size() + "' groups");

                    if (serviceDevices.isEmpty()) {
                        tvStatus.setText("Sorry, there aren't any group nearby.");
                    } else {
                        tvStatus.setVisibility(View.GONE);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        rcWifi.setLayoutManager(layoutManager);
                        rcWifi.setItemAnimator(new DefaultItemAnimator());
                        rcWifi.setAdapter(new WifiDirectAdapter(getContext(), serviceDevices, wroupClient));
                    }
                }

                @Override
                public void onError(WiFiP2PError wiFiP2PError) {
                    tvStatus.setText("Please restart your Wi-Fi!");
                }
            });
        }
    }
}
