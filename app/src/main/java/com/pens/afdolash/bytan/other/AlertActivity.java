package com.pens.afdolash.bytan.other;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pens.afdolash.bytan.R;

import static com.pens.afdolash.bytan.intro.IntroductionActivity.EXTRAS_USER_GENDER;
import static com.pens.afdolash.bytan.intro.IntroductionActivity.USER_PREF;
import static com.pens.afdolash.bytan.main.MainActivity.EXTRAS_BODY_CODE;

public class AlertActivity extends AppCompatActivity {

    private final String GENDER_MALE = "Male";
    private final String GENDER_FEMALE = "Female";

    private SharedPreferences preferences;

    private ImageView imgAlert;
    private TextView tvTitle, tvMessage;
    private RelativeLayout rvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        imgAlert = (ImageView) findViewById(R.id.img_alert);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        rvClose = (RelativeLayout) findViewById(R.id.rv_close);

        preferences = getSharedPreferences(USER_PREF, MODE_PRIVATE);
        String gender = preferences.getString(EXTRAS_USER_GENDER, null);
        int bodyCode = getIntent().getExtras().getInt(EXTRAS_BODY_CODE);

        if (bodyCode == 1) {
            if (gender.equals(GENDER_FEMALE)) imgAlert.setImageResource(R.drawable.ai_woman_rest);
            else imgAlert.setImageResource(R.drawable.ai_man_rest);
            tvTitle.setText("Rest");
            tvMessage.setText(R.string.message_rest);

        } else if (bodyCode == 2) {
            if (gender.equals(GENDER_FEMALE)) imgAlert.setImageResource(R.drawable.ai_woman_hipotermia);
            else imgAlert.setImageResource(R.drawable.ai_man_hipotermia);
            tvTitle.setText("Hipotermia");
            tvMessage.setText(R.string.message_hipotermia);

        } else if (bodyCode == 3) {
            if (gender.equals(GENDER_FEMALE)) imgAlert.setImageResource(R.drawable.ai_woman_emergency);
            else imgAlert.setImageResource(R.drawable.ai_man_emergency);
            tvTitle.setText("Emergency");
            tvMessage.setText(R.string.message_emergency);

        } else {
            finish();
        }

        rvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
