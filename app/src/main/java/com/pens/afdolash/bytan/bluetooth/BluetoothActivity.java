package com.pens.afdolash.bytan.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pens.afdolash.bytan.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    public static final String DEVICE_PREF = "DEVICE_PREF";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final String BLE_TAG = BluetoothActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();
    private boolean mScanning;
    private Handler mHandler;

    private RecyclerView rcDevices;
    private TextView tvDiscover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported.", Toast.LENGTH_SHORT).show();
            finish();
        }

        mHandler = new Handler();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDiscover = (TextView) findViewById(R.id.tv_discover);
        rcDevices = (RecyclerView) findViewById(R.id.rc_devices);
        rcDevices.setLayoutManager(new LinearLayoutManager(this));

        tvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mScanning) {
                    scanLeDevice(true);
                    Toast.makeText(BluetoothActivity.this, "Discovering...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BluetoothActivity.this, "Discovering...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Permission on Marsmellow
        checkBTPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        rcDevices.setAdapter(new BluetoothLeAdapter(this, mBluetoothDevices));
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        scanLeDevice(false);
        mBluetoothDevices.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    Toast.makeText(BluetoothActivity.this, "BLE size: " + rcDevices.getAdapter().getItemCount(), Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mBluetoothDevices.contains(device)) {
                        mBluetoothDevices.add(device);
                        rcDevices.getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1001); //Any number
            }
        }else{
            Log.d(BLE_TAG, "CheckBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
