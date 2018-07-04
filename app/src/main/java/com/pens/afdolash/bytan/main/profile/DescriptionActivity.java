package com.pens.afdolash.bytan.main.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    private TextView tvDate, tvStatus, tvDescription, tvTreatment, tvTemp, tvHeart;

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
        tvTreatment= (TextView) findViewById(R.id.tv_treatment);
        tvHeart = (TextView) findViewById(R.id.tv_heart);
        tvTemp = (TextView) findViewById(R.id.tv_temperature);

        tvDate.setText(formatDate(date) +" - "+ formatTime(date)+ " wib");
        tvStatus.setText(status);
        tvHeart.setText(heart);
        tvTemp.setText(temp);
        tvDescription.setText("Sorry, I am very sad when you are infected with hypothermia today at "
                + lat +", "+ lng +". Do not forget to eat healthy food and rest until your health " +
                "improves. Keep the spirit in doing your daily activities :)");

        if (tvStatus.getText().equals("Mild Hypothermia")) {
            tvTreatment.setVisibility(View.VISIBLE);
            tvTreatment.setText("First,\n" +
                    "Come out of the cold environment, keep your head and neck closed.\n\n" +
                    "Second,\n" +
                    "Drink warm and sweet drinks (not alcohol, coffee or tea) and eat some foods with high energy."
            );
        } else if (tvStatus.getText().equals("Moderate Hypothermia")) {
            tvTreatment.setVisibility(View.VISIBLE);
            tvTreatment.setText("First,\n" +
                    "Come out of the cold environment, keep your head and neck closed. Apply a mild heat balm to the head, neck, chest, armpits and between the thighs.\n\n" +
                    "Second,\n" +
                    "Use hot water bottles or warm moist towel. To warm yourself up.\n\n" +
                    "Third,\n" +
                    "You should continue this treatment for a while. You should be checked by a doctor."
            );
        } else if ((tvStatus.getText().equals("Severe Hypothermia"))) {
            tvTreatment.setVisibility(View.VISIBLE);
            tvTreatment.setText("First,\n" +
                    "You should be placed in a sleeping bag with one or two of your friends. Skin to skin contact in the chest area (ribs) and neck effectively. Allow your friends to warm air near your nose and mouth, or introduce steam to the area.\n\n" +
                    "Second,\n" +
                    "Your friend should keep you awake, stay alert and keep an eye on you.\n\n" +
                    "Third,\n" +
                    "Apply lightweight heat balm, with the aim of stopping the temperature drop, not rewarming.\n\n" +
                    "Fourth,\n" +
                    "Check the pulse in the carotid artery. If, after two minutes you do not find the pulse, look on the other side of the neck for two minutes.\n\n" +
                    "Fifth,\n" +
                    "If there is a breathing or pulse, no matter how unconscious, do not give CPR but keep a close eye on the change of vital signs.\n\n" +
                    "Sixth,\n" +
                    "If no pulse is found immediately starting CPR, stop only when the heart starts beating or the person applying CPR can not do anything else without endangering himself.\n\n" +
                    "Seventh,\n" +
                    "Medical assistance is very important, you need to be hospitalized.");
        } else {
            tvTreatment.setVisibility(View.GONE);
        }
    }

    /**
     * Formatting timestamp to `dd MM  yyyy` format
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

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: 00.15
     */
    private String formatTime(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("HH.mm");
            return fmtOut.format(date);
        } catch (ParseException e) {
            return "Error time convertions!";
        }
    }
}
