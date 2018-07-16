package com.pens.afdolash.bytan.main;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiDirectBroadcastReceiver;
import com.abemart.wroup.common.WiFiP2PInstance;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.listeners.ClientConnectedListener;
import com.abemart.wroup.common.listeners.ClientDisconnectedListener;
import com.abemart.wroup.common.listeners.DataReceivedListener;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothActivity;
import com.pens.afdolash.bytan.bluetooth.BluetoothData;
import com.pens.afdolash.bytan.bluetooth.BluetoothLeAttributes;
import com.pens.afdolash.bytan.bluetooth.BluetoothLeService;
import com.pens.afdolash.bytan.main.dashboard.DashboardFragment;
import com.pens.afdolash.bytan.main.group.GroupFragment;
import com.pens.afdolash.bytan.main.group.model.MemberData;
import com.pens.afdolash.bytan.main.group.MemberFragment;
import com.pens.afdolash.bytan.main.profile.ProfileFragment;
import com.pens.afdolash.bytan.other.AlertActivity;
import com.pens.afdolash.bytan.other.DatabaseHelper;
import com.pens.afdolash.bytan.other.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.DEVICE_PREF;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_ADDRESS;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_NAME;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.REQUEST_ENABLE_BT;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_IS_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.EXTRAS_GROUP_NAME;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_PREF;

public class MainActivity extends AppCompatActivity implements DataReceivedListener, ClientConnectedListener, ClientDisconnectedListener, GPSTracker.LocationUpdateListener {
    public final static String MAIN_TAG = MainActivity.class.getSimpleName();

    public final static UUID HM_RX_TX = UUID.fromString(BluetoothLeAttributes.HM_RX_TX);
    public final static String EXTRAS_BODY_CODE = "EXTRAS_BODY_CODE";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public DisplayMetrics metrics = new DisplayMetrics();

    private DatabaseHelper db;

    public GPSTracker tracker;

    private SharedPreferences prefGroup, prefDevice;
    private String groupName;
    private boolean isGroupOwner;

    public String mDeviceName;
    public String mDeviceAddress;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private List<BluetoothData> dataList = new ArrayList<>();

    public WiFiDirectBroadcastReceiver mWifiDirectReceiver;
    public WroupService mWroupService;
    public WroupClient mWroupClient;
    private List<MemberData> memberList = new ArrayList<>();

    private Handler handler = new Handler();
    private boolean isConnected = false;
    private boolean isSerial = false;
    private boolean isAlert = false;
    public Fragment focusFragment;

    private Dialog dialogLoading;
    private BottomNavigationViewEx navigation;
    private int selectedNavId = R.id.nav_dashboard;

    public String lastUpdate;
    private boolean doubleBackToExit = false;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                prefDevice.edit().clear().commit();

                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
                finish();
                Log.e(MAIN_TAG, "Unable to initialize Bluetooth.");
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            mBluetoothLeService.disconnect();
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                isConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();

                // Load dashboard fragment by default
                loadFragment(new DashboardFragment());

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                isConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
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
                    if (item.getItemId() != selectedNavId) {
                        selectedNavId = item.getItemId();
                        focusFragment = null;
                        fragment = new DashboardFragment();
                        loadFragment(fragment);

                        // Send state ACTIVE to Arduino
                        changeState("!!8");
                    }
                    return true;
                case R.id.nav_group:
                    if (item.getItemId() != selectedNavId) {
                        selectedNavId = item.getItemId();
                        focusFragment = null;
                        groupName = prefGroup.getString(EXTRAS_GROUP_NAME, null);
                        isGroupOwner = prefGroup.getBoolean(EXTRAS_GROUP_IS_OWNER, false);
                        fragment = groupName != null ? new MemberFragment() : new GroupFragment();
                        loadFragment(fragment);

                        // Send state ACTIVE to Arduino
                        changeState("!!8");
                    }
                    return true;
                case R.id.nav_profile:
                    if (item.getItemId() != selectedNavId) {
                        selectedNavId = item.getItemId();
                        focusFragment = null;
                        fragment = new ProfileFragment();
                        loadFragment(fragment);

                        // Send state ACTIVE to Arduino
                        changeState("!!8");
                    }
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setTextVisibility(false);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Database history
        db = new DatabaseHelper(this);

        // Location tracker
        tracker = new GPSTracker(this);
        tracker.setLocationUpdateListener(this);

        // Get address and device name bluetooth
        prefDevice = getSharedPreferences(DEVICE_PREF, MODE_PRIVATE);
        prefGroup = getSharedPreferences(GROUP_PREF, MODE_PRIVATE);
        if (prefDevice.getString(EXTRAS_DEVICE_ADDRESS, null) == null) {
            final Intent intent = getIntent();
            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        } else {
            mDeviceName = prefDevice.getString(EXTRAS_DEVICE_NAME, null);
            mDeviceAddress = prefDevice.getString(EXTRAS_DEVICE_ADDRESS, null);
        }

        // Wifi direct p2p
        mWroupService = WroupService.getInstance(getApplicationContext());
        mWroupClient = WroupClient.getInstance(getApplicationContext());
        mWifiDirectReceiver = WiFiP2PInstance.getInstance(this).getBroadcastReceiver();

        // Bluetooth Le Service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Display loading animate
        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        dialogLoading = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialogLoading.setContentView(view);
        dialogLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialogLoading.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlert = false;

        // Re-registering receiver
        try {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            registerReceiver(mWifiDirectReceiver, makeWifiDirectIntentFilter());
        } catch (Exception e) {
            Log.d(MAIN_TAG, "Receiver already registered");
            unregisterReceiver(mGattUpdateReceiver);
            return;
        }

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(MAIN_TAG, "Connect request result: " + result);
            }
        }

        // Send state ACTIVE to Arduino
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeState("!!8");
            }
        }, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAlert = true;

        // Send state IDLE to Arduino
        changeState("!!9");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unreggistering receiver
        try {
            unregisterReceiver(mGattUpdateReceiver);
            unregisterReceiver(mWifiDirectReceiver);
        } catch (Exception e) {
            Log.d(MAIN_TAG, "Receiver already registered");
        }

        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        // Disconnecting wifi direct
        if (mWroupService != null) mWroupService.disconnect();
        if (mWroupClient != null) mWroupClient.disconnect();

        SharedPreferences.Editor editor = prefGroup.edit();
        editor.clear().apply();
    }

    @Override
    public void onBackPressed() {
        if (focusFragment == null) {
            if (navigation.getSelectedItemId() != R.id.nav_dashboard) {
                navigation.setSelectedItemId(R.id.nav_dashboard);
                selectedNavId = R.id.nav_dashboard;
                loadFragment(new DashboardFragment());
            } else {
                if (doubleBackToExit) {
                    super.onBackPressed();
                }

                this.doubleBackToExit = true;
                Toast.makeText(this, "Please click back again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExit = false;
                    }
                }, 2000);
            }
        } else {
            loadFragment(new DashboardFragment());
            destroyFragment(focusFragment);
            focusFragment = null;

            // Send state ACTIVE to Arduino
            changeState("!!8");
        }
    }

    /**
     * Send code change state to arduino
     *
     * @param code
     */
    public void changeState(String code) {
        if (isSerial) {
            String activeParams = String.valueOf(code);
            final byte[] tx = activeParams.getBytes(Charset.forName("UTF-8"));
            if (isConnected && characteristicTX != null && characteristicRX != null) {
                characteristicTX.setValue(tx);
                try {
                    mBluetoothLeService.writeCharacteristic(characteristicTX);
                    mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
                } catch (Exception e) {
                    Log.e(MAIN_TAG, e.getMessage().toString());
                }
            }
        }
        return;
    }

    /**
     * Load spesific fragment
     *
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    /**
     * Destroy spesific fragment
     *
     * @param fragment
     */
    public void destroyFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * Notification bluetooth state
     */
    private void clearUI() {
        Toast.makeText(mBluetoothLeService, "Disconnected.", Toast.LENGTH_SHORT).show();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resourceId == R.string.connected) {
                    dialogLoading.dismiss();
                } else {
                    dialogLoading.show();
                }
            }
        });
    }

    /**
     * Receive data from bluetooth arduino
     */
    private String jsonString;
    private int lastBodyCode;
    private void displayData(String data) {
        if (data != null) {
            if (data.startsWith("{")) {
                jsonString = "";
            }

            if (!data.equals("Failed!") || !data.equals("Success.")) {
                jsonString += data;
            }

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                BluetoothData bluetoothData = new BluetoothData(
                        jsonObject.getString("amb"),
                        jsonObject.getString("obj"),
                        jsonObject.getString("heart"),
                        jsonObject.getInt("code")
                );

                dataList.add(bluetoothData);

                lastUpdate = new SimpleDateFormat("HHmmss").format(new Date());

                int bodyCode = bluetoothData.getCode();
                if (bodyCode > 1 && bodyCode != lastBodyCode && bodyCode != 99) {
                    double latitude = tracker.getLatitude();
                    double longitude = tracker.getLongitude();

                    lastBodyCode = bodyCode;
                    db.insertHistory(
                            bluetoothData.getHeartRate(),
                            bluetoothData.getObjTemp(),
                            bluetoothData.getAmbTemp(),
                            String.valueOf(bluetoothData.getCode()),
                            String.valueOf(latitude),
                            String.valueOf(longitude));

                    groupName = prefGroup.getString(EXTRAS_GROUP_NAME, null);
                    isGroupOwner = prefGroup.getBoolean(EXTRAS_GROUP_IS_OWNER, false);

                    if (groupName != null) {
                        String messageStr = "{"
                                + "heart : "+ bluetoothData.getHeartRate() +", "
                                + "obj : "+ bluetoothData.getObjTemp() +", "
                                + "amb : "+ bluetoothData.getAmbTemp() +", "
                                + "code : "+ bluetoothData.getCode() +", "
                                + "latitude : "+ latitude +", "
                                + "longitude : "+ longitude +", "
                                + "timestamp : "+ new SimpleDateFormat("HHmmss").format(new Date())
                                + "}";

                        if (!messageStr.isEmpty()) {
                            MessageWrapper normalMessage = new MessageWrapper();
                            normalMessage.setMessage(messageStr);
                            normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

                            if (isGroupOwner) {
                                mWroupService.sendMessageToAllClients(normalMessage);
                            } else {
                                mWroupClient.sendMessageToAllClients(normalMessage);
                            }
                        }
                    }
                }

                if (isAlert && bodyCode != 0 && bodyCode != 99) {
                    isAlert = false;

                    Intent intent = new Intent(mBluetoothLeService, AlertActivity.class);
                    intent.putExtra(EXTRAS_BODY_CODE, bodyCode);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all data arduino
     *
     * @return datalist
     */
    public List<BluetoothData> getDataList() {
        return dataList;
    }

    /**
     * Get all member group wifi direct
     *
     * @return memberlist
     */
    public List<MemberData> getMemberList() {
        return memberList;
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
                prefDevice.edit()
                        .putString(EXTRAS_DEVICE_NAME, mDeviceName)
                        .putString(EXTRAS_DEVICE_ADDRESS, mDeviceAddress)
                        .commit();
                isSerial = true;
            } else {
                isSerial = false;
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // Get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    /**
     * Bluetooth intent filter
     *
     * @return Bluetooth intent filter
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * Wifi direct intent filter
     *
     * @return Wifi direct intent filter
     */
    private static IntentFilter makeWifiDirectIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    /*
        Wifi Direct Data Receiver
     */
    @Override
    public void onClientConnected(final WroupDevice wroupDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Check if location is available
                double latitude = tracker.getLatitude();
                double longitude = tracker.getLongitude();

                String messageStr = null;

                if (dataList.size() > 0) {
                    BluetoothData lastUpdate = dataList.get(dataList.size() - 1);
                    messageStr = "{"
                            + "heart : "+ lastUpdate.getHeartRate() +", "
                            + "obj : "+ lastUpdate.getObjTemp() +", "
                            + "amb : "+ lastUpdate.getAmbTemp() +", "
                            + "code : "+ lastUpdate.getCode() +", "
                            + "latitude : "+ latitude +", "
                            + "longitude : "+ longitude +", "
                            + "timestamp : "+ new SimpleDateFormat("HHmmss").format(new Date())
                            + "}";
                } else {
                    messageStr = "{"
                            + "heart : "+ 0.0 +", "
                            + "obj : "+ 0.0 +", "
                            + "amb : "+ 0.0 +", "
                            + "spo2 : "+ 0 +", "
                            + "code : "+ 0 +", "
                            + "latitude : "+ latitude +", "
                            + "longitude : "+ longitude +", "
                            + "timestamp : "+ new SimpleDateFormat("HHmmss").format(new Date())
                            + "}";
                }

                if (!messageStr.isEmpty()) {
                    MessageWrapper normalMessage = new MessageWrapper();
                    normalMessage.setMessage(messageStr);
                    normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

                    if (isGroupOwner) {
                        mWroupService.sendMessage(wroupDevice, normalMessage);
                    } else {
                        mWroupClient.sendMessage(wroupDevice, normalMessage);
                    }
                }
            }
        });
    }

    @Override
    public void onClientDisconnected(final WroupDevice wroupDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MemberData member : memberList) {
                    if (member.getDevice().equals(wroupDevice)) {
                        memberList.remove(member);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onDataReceived(final MessageWrapper messageWrapper) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(messageWrapper.getMessage());

                    MemberData memberData = new MemberData(
                            messageWrapper.getWroupDevice(),
                            jsonObject.getString("heart"),
                            jsonObject.getString("obj"),
                            jsonObject.getString("amb"),
                            jsonObject.getString("code"),
                            jsonObject.getString("latitude"),
                            jsonObject.getString("longitude"),
                            jsonObject.getString("timestamp")
                    );

                    boolean isExist = false;
                    for (MemberData member : memberList) {
                        if (member.getDevice().equals(memberData.getDevice())) {
                            isExist = true;
                            memberList.remove(member);
                            memberList.add(memberData);
                            break;
                        }
                    }

                    if (!isExist) {
                        memberList.add(memberData);
                    }

                    Toast.makeText(MainActivity.this, "Data receive.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLocationUpdate(Location location) {
        groupName = prefGroup.getString(EXTRAS_GROUP_NAME, null);
        isGroupOwner = prefGroup.getBoolean(EXTRAS_GROUP_IS_OWNER, false);

        if (groupName != null) {
            // Check if location is available
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String messageStr = null;

            if (dataList.size() > 0) {
                BluetoothData lastUpdate = dataList.get(dataList.size() - 1);
                messageStr = "{"
                        + "heart : "+ lastUpdate.getHeartRate() +", "
                        + "obj : "+ lastUpdate.getObjTemp() +", "
                        + "amb : "+ lastUpdate.getAmbTemp() +", "
                        + "code : "+ lastUpdate.getCode() +", "
                        + "latitude : "+ latitude +", "
                        + "longitude : "+ longitude +", "
                        + "timestamp : "+ new SimpleDateFormat("HHmmss").format(new Date())
                        + "}";
            } else {
                messageStr = "{"
                        + "heart : "+ 0.0 +", "
                        + "obj : "+ 0.0 +", "
                        + "amb : "+ 0.0 +", "
                        + "spo2 : "+ 0 +", "
                        + "code : "+ 0 +", "
                        + "latitude : "+ latitude +", "
                        + "longitude : "+ longitude +", "
                        + "timestamp : "+ new SimpleDateFormat("HHmmss").format(new Date())
                        + "}";
            }

            if (!messageStr.isEmpty()) {
                MessageWrapper normalMessage = new MessageWrapper();
                normalMessage.setMessage(messageStr);
                normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

                if (isGroupOwner) {
                    mWroupService.sendMessageToAllClients(normalMessage);
                } else {
                    mWroupClient.sendMessageToAllClients(normalMessage);
                }
            }

        }
    }
}
