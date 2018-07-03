package com.pens.afdolash.bytan.main.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DescriptionActivity extends AppCompatActivity {
    public final static String DATE_EXTRAS = "DATE_EXTRAS";
    public final static String STATUS_EXTRAS = "STATUS_EXTRAS";
    public final static String LAT_EXTRAS = "LAT_EXTRAS";
    public final static String LNG_EXTRAS = "LNG_EXTRAS";
    public final static String HEART_EXTRAS = "HEART_EXTRAS";
    public final static String TEMP_EXTRAS = "TEMP_EXTRAS";

    private TextView tvDate, tvStatus, tvDescription, tvTemp, tvHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Bundle bundle = getIntent().getExtras();

        String date = bundle.getString(DATE_EXTRAS);
        String status = bundle.getString(STATUS_EXTRAS);
        String lat = bundle.getString(LAT_EXTRAS);
        String lng = bundle.getString(LNG_EXTRAS);
        String heart = bundle.getString(HEART_EXTRAS);
        String temp = bundle.getString(TEMP_EXTRAS);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvHeart = (TextView) findViewById(R.id.tv_heart);
        tvTemp = (TextView) findViewById(R.id.tv_temperature);

        tvDate.setText(formatDate(date));
        tvStatus.setText(status);
        tvHeart.setText(heart);
        tvTemp.setText(temp);
        tvDescription.setText("Sorry, I am very sad when you are infected with hypothermia today at "
                + lat +", "+ lng +". Do not forget to eat healthy food and rest until your health " +
                "improves. Keep the spirit in doing your daily activities :)");
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: 21 Feb 2018
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {
            return "Error date convertions!";
        }
    }
}
