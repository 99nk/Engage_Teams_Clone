package com.niharika.engage_ms_teams.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.model.GroupChatModel;

import java.util.ArrayList;

//Adapter to handle the group chats data and bind it it to the view item_groupchat

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupVideoViewHolder> {
    Context context;
    ArrayList<GroupChatModel> list;

    //Constructor to initialise the values
    public GroupChatAdapter(Context context, ArrayList<GroupChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GroupVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_groupchat, parent, false);
        return new GroupVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupVideoViewHolder holder, int position) {
        GroupChatModel user = list.get(position);
        holder.mName.setText(user.getName());
        holder.mDate.setText(user.getDate());
        holder.mTime.setText(user.getTime());
        holder.mMessage.setText(user.getMessage());
        String initial = "";
        initial += user.getName().charAt(0);
        TextDrawable drawable = TextDrawable.builder().buildRect(initial, R.color.teal_200);
        holder.mIcon.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GroupVideoViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mDate, mTime, mMessage;
        ImageView mIcon;

        public GroupVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.group_chat_icon);
            mName = itemView.findViewById(R.id.name);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);
            mMessage = itemView.findViewById(R.id.message);
        }
    }
}
