package com.pens.afdolash.bytan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pens.afdolash.bytan.bluetooth.BluetoothActivity;
import com.pens.afdolash.bytan.intro.IntroductionActivity;
import com.pens.afdolash.bytan.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    public static final String USER_PREF = "user-pref";
    public static final String USER_NAME = "user-name";
    public static final String USER_GENDER = "user-gender";

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                preferences = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
                String name = preferences.getString(USER_NAME, null);
                String address = preferences.getString(USER_NAME, null);

                if (name == null) {
                    Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, BluetoothActivity.class);
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
