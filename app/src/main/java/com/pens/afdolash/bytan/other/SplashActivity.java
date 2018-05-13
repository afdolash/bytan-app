package com.pens.afdolash.bytan.other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothActivity;
import com.pens.afdolash.bytan.intro.IntroductionActivity;
import com.pens.afdolash.bytan.main.MainActivity;

import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.DEVICE_PREF;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_ADDRESS;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_NAME;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences userPref, devicePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userPref = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
                String name = userPref.getString(EXTRAS_USER_NAME, null);

                devicePref = getSharedPreferences(DEVICE_PREF, Context.MODE_PRIVATE);
                String address = devicePref.getString(EXTRAS_DEVICE_ADDRESS, null);

                if (name == null) {
                    Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
                    startActivity(intent);
                } else if (address == null){
                    Intent intent = new Intent(SplashActivity.this, BluetoothActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
    }
}
