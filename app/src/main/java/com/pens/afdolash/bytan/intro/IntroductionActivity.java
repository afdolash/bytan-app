package com.pens.afdolash.bytan.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.bluetooth.BluetoothActivity;

public class IntroductionActivity extends AppCompatActivity {
    public static final String USER_PREF = "USER_PREF";
    public static final String EXTRAS_USER_NAME = "USER_NAME";
    public static final String EXTRAS_USER_GENDER = "USER_GENDER";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private RelativeLayout rvStart;
    private EditText etName;
    private RadioGroup radGender;
    private RadioButton radButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        rvStart = (RelativeLayout) findViewById(R.id.rv_start);
        etName = (EditText) findViewById(R.id.et_name);
        radGender = (RadioGroup)  findViewById(R.id.rad_gender);

        preferences = getSharedPreferences(USER_PREF, MODE_PRIVATE);
        editor = preferences.edit();

        rvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radGender.getCheckedRadioButtonId();

                radButton = (RadioButton) findViewById(selectedId);

                String name = etName.getText().toString();
                String gender = radButton.getText().toString();

                editor.putString(EXTRAS_USER_NAME, name);
                editor.putString(EXTRAS_USER_GENDER, gender);
                editor.apply();

                Intent intent = new Intent(IntroductionActivity.this, BluetoothActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
