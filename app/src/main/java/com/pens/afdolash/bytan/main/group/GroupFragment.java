package com.pens.afdolash.bytan.main.group;


import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiP2PError;
import com.abemart.wroup.common.WroupServiceDevice;
import com.abemart.wroup.common.listeners.ServiceDiscoveredListener;
import com.abemart.wroup.common.listeners.ServiceRegisteredListener;
import com.abemart.wroup.service.WroupService;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements GroupCreationDialog.GroupCreationListener, GroupDiscoveryDialog.GroupDiscoveryListener {
    public static final String GROUP_TAG = GroupFragment.class.getSimpleName();

    public static final String GROUP_PREF = "group-pref";
    public static final String GROUP_NAME = "group-name";
    public static final String GROUP_OWNER = "group-owner";
    public static final String GROUP_IS_OWNER = "group-is-owner";

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

        wroupClient = ((MainActivity) getActivity()).mWroupClient;
        wroupService = ((MainActivity) getActivity()).mWroupService;

        rvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroupDialog = new GroupCreationDialog();
                createGroupDialog.setGroupCreationListener(GroupFragment.this);
                createGroupDialog.show(getChildFragmentManager(), GroupCreationDialog.class.getSimpleName());
            }
        });

        rvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverGroupDialog = new GroupDiscoveryDialog();
                discoverGroupDialog.setGroupDiscoveryListener(GroupFragment.this);
                discoverGroupDialog.show(getChildFragmentManager(), GroupDiscoveryDialog.class.getSimpleName());
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

                    editor.putString(GROUP_NAME, groupName);
                    editor.putBoolean(GROUP_IS_OWNER, true);
                    editor.commit();

                    Log.i(GROUP_TAG, "Group created. Launching GroupChatActivity...");
                    Toast.makeText(getContext(), "Group created.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onErrorServiceRegistered(WiFiP2PError wiFiP2PError) {
                    Toast.makeText(getContext(), "Error creating group. "+ wiFiP2PError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please, insert a group name.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupDiscovered(final RecyclerView rcWifi) {
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
                        Toast.makeText(getContext(), "Sorry, there aren't any group nearby",Toast.LENGTH_LONG).show();
                    } else {
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        rcWifi.setLayoutManager(layoutManager);
                        rcWifi.setItemAnimator(new DefaultItemAnimator());
                        rcWifi.setAdapter(new WifiDirectAdapter(getContext(), serviceDevices, wroupClient));
                    }
                }

                @Override
                public void onError(WiFiP2PError wiFiP2PError) {
                    Toast.makeText(getContext(), "Error searching groups: " + wiFiP2PError, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
