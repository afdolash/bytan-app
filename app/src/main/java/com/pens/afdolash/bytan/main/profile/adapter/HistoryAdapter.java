package com.pens.afdolash.bytan.main.profile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.profile.DescriptionActivity;
import com.pens.afdolash.bytan.main.profile.model.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.DATE_EXTRAS;
import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.HEART_EXTRAS;
import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.LAT_EXTRAS;
import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.LNG_EXTRAS;
import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.STATUS_EXTRAS;
import static com.pens.afdolash.bytan.main.profile.DescriptionActivity.TEMP_EXTRAS;

/**
 * Created by afdol on 5/29/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<History> histories;

    public HistoryAdapter(Context context, List<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final History history = histories.get(position);

        float dataTemp = Float.parseFloat(history.getObjTemp());
        if (dataTemp < 35 && dataTemp >= 31) {
            holder.tvStatus.setText("Mild Hypothermia");
        } else if (dataTemp < 31 && dataTemp >= 28) {
            holder.tvStatus.setText("Moderate Hypothermia");
        } else if (dataTemp < 28){
            holder.tvStatus.setText("Severe Hypothermia");
        } else {
            holder.tvStatus.setText("Hypothermia");
        }

        holder.tvLocation.setText(history.getLatitude() +" ,"+ history.getLongitude());

        final String date = formatDate(history.getTimestamp());
        String[] dates = date.split(" ");

        holder.tvDay.setText(dates[1]);
        holder.tvMonth.setText(dates[0]);

        holder.cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DescriptionActivity.class);
                intent.putExtra(DATE_EXTRAS, history.getTimestamp());
                intent.putExtra(STATUS_EXTRAS, holder.tvStatus.getText());
                intent.putExtra(LAT_EXTRAS, history.getLatitude());
                intent.putExtra(LNG_EXTRAS, history.getLongitude());
                intent.putExtra(TEMP_EXTRAS, history.getObjTemp());
                intent.putExtra(HEART_EXTRAS, history.getHeartRate());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardHistory;
        TextView tvDay, tvMonth, tvStatus, tvLocation;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardHistory = (CardView) itemView.findViewById(R.id.card_history);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
        }
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
