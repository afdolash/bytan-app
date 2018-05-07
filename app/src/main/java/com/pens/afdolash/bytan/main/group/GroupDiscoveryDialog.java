package com.pens.afdolash.bytan.main.group;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.pens.afdolash.bytan.R;

/**
 * Created by afdol on 5/1/2018.
 */

public class GroupDiscoveryDialog extends DialogFragment {
    public interface GroupDiscoveryListener {
        void onGroupDiscovered(RecyclerView rcWifi);
    }

    private GroupDiscoveryListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_discover_group, null);

        RecyclerView rcWifi = view.findViewById(R.id.rc_wifi);

        if (listener != null) {
            listener.onGroupDiscovered(rcWifi);
        }

        builder.setView(view);
        builder.setCancelable(true);

        return builder.create();
    }

    public void setGroupDiscoveryListener(GroupDiscoveryListener listener) {
        this.listener = listener;
    }
}
