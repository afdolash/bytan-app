package com.pens.afdolash.bytan.main;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiDirectBroadcastReceiver;
import com.abemart.wroup.common.WiFiP2PInstance;
import com.abemart.wroup.service.WroupService;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothLeAttributes;
import com.pens.afdolash.bytan.bluetooth.BluetoothLeService;
import com.pens.afdolash.bytan.main.dashboard.DashboardFragment;
import com.pens.afdolash.bytan.main.group.GroupFragment;
import com.pens.afdolash.bytan.main.group.MemberFragment;
import com.pens.afdolash.bytan.main.group.MessageReceiver;
import com.pens.afdolash.bytan.main.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_NAME;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_PREF;

public class MainActivity extends AppCompatActivity {
    public final static String MAIN_TAG = MainActivity.class.getSimpleName();

    public final static UUID HM_RX_TX = UUID.fromString(BluetoothLeAttributes.HM_RX_TX);

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private SharedPreferences preferences;
    private String groupName;

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private boolean mConnected = false;
    public String mDeviceName;
    public String mDeviceAddress;

    private BottomNavigationViewEx navigation;
    private WiFiDirectBroadcastReceiver mWifiDirectReceiver;
    public WroupService mWroupService;
    public WroupClient mWroupClient;

    public MessageReceiver messageReceiver;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(MAIN_TAG, "Unable to initialize Bluetooth.");
                finish();
            }

            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    fragment = new DashboardFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_group:
                    fragment = groupName != null ? new MemberFragment() : new GroupFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.nav_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setTextVisibility(false);

        // Get address and device name bluetooth
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        preferences = getSharedPreferences(GROUP_PREF, MODE_PRIVATE);
        groupName = preferences.getString(GROUP_NAME, null);

        messageReceiver = new MessageReceiver();

        // Broadcast receiver
        mWifiDirectReceiver = WiFiP2PInstance.getInstance(this).getBroadcastReceiver();

        // Load dashboard fragment by default
        loadFragment(new DashboardFragment());

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(mWifiDirectReceiver, makeWifiDirectIntentFilter());

        mWroupService = WroupService.getInstance(getApplicationContext());
        mWroupClient = WroupClient.getInstance(getApplicationContext());

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(MAIN_TAG, "Connect request result: " + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unregisterReceiver(mWifiDirectReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        if (mWroupService != null) mWroupService.disconnect();
        if (mWroupClient != null) mWroupClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() != R.id.nav_dashboard) {
            loadFragment(new DashboardFragment());
            navigation.setSelectedItemId(R.id.nav_dashboard);
        } else {
            super.onBackPressed();
        }
    }


    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    public void destroyFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    private void clearUI() {
        Toast.makeText(mBluetoothLeService, "No data.", Toast.LENGTH_SHORT).show();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mBluetoothLeService, String.valueOf(resourceId), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            Toast.makeText(mBluetoothLeService, data, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Demonstrates how to iterate through the supported GATT Services/Characteristics.
     * In this sample, we populate the data structure that is bound to the ExpandableListView
     * on the UI.
     *
     * @param gattServices
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "Unknown Service";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, BluetoothLeAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            if(BluetoothLeAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                Toast.makeText(mBluetoothLeService, "Yes, serial :-)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mBluetoothLeService, "No, serial :-(", Toast.LENGTH_SHORT).show();
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // Get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private static IntentFilter makeWifiDirectIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }
}
