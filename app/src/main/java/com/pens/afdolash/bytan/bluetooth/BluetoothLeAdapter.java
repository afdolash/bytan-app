package com.pens.afdolash.bytan.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.DEVICE_PREF;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_ADDRESS;
import static com.pens.afdolash.bytan.bluetooth.BluetoothActivity.EXTRAS_DEVICE_NAME;

/**
 * Created by afdol on 4/5/2018.
 */

public class BluetoothLeAdapter extends RecyclerView.Adapter<BluetoothLeAdapter.MyViewHolder> {

    private Context context;
    private List<BluetoothDevice> mLeDevices;

    public BluetoothLeAdapter(Context context, List<BluetoothDevice> mLeDevices) {
        this.context = context;
        this.mLeDevices = mLeDevices;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bt_device, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BluetoothDevice device = mLeDevices.get(position);

        if (device.getName() == null)
            holder.tvName.setText("Unknown device");
        else
            holder.tvName.setText(device.getName());

        holder.tvAddress.setText(device.getAddress());
        holder.cardDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device == null) return;

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
                context.startActivity(intent);

                ((BluetoothActivity) context).finish();
                ((BluetoothActivity) context).scanLeDevice(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardDevice;
        private TextView tvName, tvAddress;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardDevice = (CardView) itemView.findViewById(R.id.card_device);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_mac);
        }
    }
}
