package com.pens.afdolash.bytan.main.group.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pens.afdolash.bytan.R;
import com.pens.afdolash.bytan.main.group.model.ChatData;

import java.util.List;

import static com.pens.afdolash.bytan.main.group.model.ChatData.CHAT_MEMBER;
import static com.pens.afdolash.bytan.main.group.model.ChatData.CHAT_OWNER;

/**
 * Created by afdol on 5/24/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ChatData> messages;

    public ChatAdapter(Context context, List<ChatData> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case CHAT_OWNER:
                view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.item_chat_owner, parent, false);
                return new OwnerHolder(view);
            case CHAT_MEMBER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_member, parent, false);
                return new MemberHolder(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (messages.get(position).type) {
            case 0:
                return CHAT_OWNER;
            case 1:
                return CHAT_MEMBER;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatData data = messages.get(position);

        if (data != null) {
            switch (data.type) {
                case CHAT_OWNER:
                    ((OwnerHolder) holder).tvMessage.setText(data.getMessage());
                    break;
                case CHAT_MEMBER:
                    ((MemberHolder) holder).tvUsername.setText(data.getUsername());
                    ((MemberHolder) holder).tvMessage.setText(data.getMessage());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class OwnerHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;

        public OwnerHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }

    public class MemberHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvMessage;

        public MemberHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }
}
