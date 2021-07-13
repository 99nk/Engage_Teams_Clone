package com.niharika.engage_ms_teams.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.niharika.engage_ms_teams.activities.GroupChatActivity;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.model.TeamsModel;

import java.util.ArrayList;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder> {
    Context context;
    ArrayList<TeamsModel> list;

    public TeamsAdapter(Context context, ArrayList<TeamsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teams, parent, false);
        return new TeamsAdapter.TeamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamsViewHolder holder, int position) {
        TeamsModel user = list.get(position);
        int l = user.getGroup_name().length();
        holder.mGroupName.setText(user.getGroup_name().substring(0, l - 36));
        String initial = "";
        initial += user.getGroup_name().charAt(0);
        TextDrawable drawable = TextDrawable.builder().buildRect(initial, R.color.teal_200);
        holder.mTeamIcon.setImageDrawable(drawable);

        holder.mTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupChatIntent = new Intent(context, GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", user.getGroup_name());
                context.startActivity(groupChatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TeamsViewHolder extends RecyclerView.ViewHolder {
        TextView mGroupName;
        ImageView mTeamIcon;
        CardView mTeam;

        public TeamsViewHolder(@NonNull View itemView) {
            super(itemView);
            mGroupName = itemView.findViewById(R.id.group_name);
            mTeamIcon = itemView.findViewById(R.id.team_icon);
            mTeam = itemView.findViewById(R.id.cardView_team);
        }
    }
}
