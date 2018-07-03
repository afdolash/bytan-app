package com.pens.afdolash.bytan.main.group.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.group.model.MemberData;
import com.pens.afdolash.bytan.other.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by afdol on 6/10/2018.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {

    private Context context;
    private List<MemberData> memberData;
    private GPSTracker tracker;

    public MemberAdapter(Context context, List<MemberData> memberData) {
        this.context = context;
        this.memberData = memberData;

        tracker = new GPSTracker(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_member, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MemberData member = memberData.get(position);

        holder.tvName.setText(member.getDevice().getDeviceName());

        String date = new SimpleDateFormat("HHmmss").format(new Date());
        int timestamp = Integer.parseInt(date) - Integer.parseInt(member.getTimestamp());

        if (timestamp > 59) {
            holder.tvTimestamp.setText(timestamp / 60 +" minutes ago");
        } else {
            holder.tvTimestamp.setText(timestamp +" seconds ago");
        }

        Location memberLoc = new Location("");
        memberLoc.setLatitude(Double.parseDouble(member.getLatitude()));
        memberLoc.setLongitude(Double.parseDouble(member.getLongitude()));

        Location myLoc = new Location("");
        myLoc.setLatitude(tracker.getLatitude());
        myLoc.setLongitude(tracker.getLongitude());

        holder.tvDistance.setText((int)myLoc.distanceTo(memberLoc) +"m");

        switch (Integer.parseInt(member.getCode())) {
            case 0:
                holder.imgStatus.setColorFilter(ContextCompat.getColor(context, R.color.statusHealthy), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 1:
                holder.imgStatus.setColorFilter(ContextCompat.getColor(context, R.color.statusRest), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                holder.imgStatus.setColorFilter(ContextCompat.getColor(context, R.color.statusHyphothermia), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 3:
                holder.imgStatus.setColorFilter(ContextCompat.getColor(context, R.color.statusEmergency), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                holder.imgStatus.setColorFilter(ContextCompat.getColor(context, R.color.statusHealthy), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return memberData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout cardMember;
        TextView tvName, tvTimestamp, tvDistance;
        ImageView imgStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardMember = (LinearLayout) itemView.findViewById(R.id.card_member);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            imgStatus = (ImageView) itemView.findViewById(R.id.img_status);
        }
    }
}
