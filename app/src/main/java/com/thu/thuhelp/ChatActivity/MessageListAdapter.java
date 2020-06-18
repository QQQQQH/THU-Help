package com.thu.thuhelp.ChatActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.Message;

import java.util.LinkedList;

public class MessageListAdapter
        extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private LayoutInflater inflater;
    private LinkedList<Message> messageList;

    public MessageListAdapter(Context context, LinkedList<Message> messageList) {
        inflater = LayoutInflater.from(context);
        this.messageList = messageList;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout leftLayout, rightLayout;
        final TextView textViewLeftMsg, textViewRightMsg;
        final View itemView;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);
            textViewLeftMsg = itemView.findViewById(R.id.left_msg);
            textViewRightMsg = itemView.findViewById(R.id.right_msg);
            this.itemView = itemView;
        }
    }

    @NonNull
    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.type == message.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.textViewLeftMsg.setText(message.content);
        } else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.textViewRightMsg.setText(message.content);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
