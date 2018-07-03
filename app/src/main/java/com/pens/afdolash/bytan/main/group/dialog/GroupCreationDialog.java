package com.pens.afdolash.bytan.main.group.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pens.afdolash.bytan.R;

/**
 * Created by afdol on 5/1/2018.
 */

public class GroupCreationDialog extends DialogFragment {
    public interface GroupCreationListener {
        void onGroupCreated(String groupName);
    }

    private GroupCreationListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_group, null);

        final EditText etGroupName = view.findViewById(R.id.et_group_name);
        RelativeLayout rvCreate = view.findViewById(R.id.rv_create);

        builder.setView(view);
        builder.setCancelable(true);
        rvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = etGroupName.getText().toString();
                if (listener != null) {
                    listener.onGroupCreated(groupName);
                }
            }
        });

        return builder.create();
    }

    public void setGroupCreationListener(GroupCreationListener listener) {
        this.listener = listener;
    }
}
