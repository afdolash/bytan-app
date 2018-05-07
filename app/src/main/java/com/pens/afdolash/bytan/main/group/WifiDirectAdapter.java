package com.pens.afdolash.bytan.main.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.WroupServiceDevice;
import com.abemart.wroup.common.listeners.ServiceConnectedListener;
import com.abemart.wroup.service.WroupService;
import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.MainActivity;

import java.util.List;

import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_IS_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_NAME;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_OWNER;
import static com.pens.afdolash.bytan.main.group.GroupFragment.GROUP_PREF;

/**
 * Created by afdol on 5/1/2018.
 */

public class WifiDirectAdapter extends RecyclerView.Adapter<WifiDirectAdapter.MyViewHolder> {
    private Context context;
    private List<WroupServiceDevice> devices;
    private WroupClient client;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public WifiDirectAdapter(Context context, List<WroupServiceDevice> devices, WroupClient client) {
        this.context = context;
        this.devices = devices;
        this.client = client;
    }

    @Override
    public WifiDirectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_device, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WifiDirectAdapter.MyViewHolder holder, int position) {
        final WroupServiceDevice device = devices.get(position);

        preferences = context.getSharedPreferences(GROUP_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();

        holder.tvName.setText(device.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME));
        holder.tvMac.setText(device.getDeviceName() +"'s Group");
        holder.cardWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Connecting to the group...");
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                client.connectToService(device, new ServiceConnectedListener() {
                    @Override
                    public void onServiceConnected(WroupDevice serviceDevice) {
                        progressDialog.dismiss();
                        ((MainActivity) context).loadFragment(new MemberFragment());

                        editor.putString(GROUP_NAME, device.getTxtRecordMap().get(WroupService.SERVICE_GROUP_NAME));
                        editor.putString(GROUP_OWNER, device.getDeviceName() +"'s Group");
                        editor.putBoolean(GROUP_IS_OWNER, false);
                        editor.commit();

                        Toast.makeText(context, "Connected.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardWifi;
        private TextView tvName, tvMac;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardWifi = (CardView) itemView.findViewById(R.id.card_wifi);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMac = (TextView) itemView.findViewById(R.id.tv_mac);
        }
    }
}
