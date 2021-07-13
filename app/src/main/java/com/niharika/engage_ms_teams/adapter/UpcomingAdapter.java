package com.niharika.engage_ms_teams.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.activities.HomeActivity;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.model.UpcomingModel;
import com.niharika.engage_ms_teams.utils.Constants;

import java.util.ArrayList;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.upcomingViewHolder> {
    Context context;
    ArrayList<UpcomingModel> list;
    FirebaseAuth mAuth;
    String currentUserId;


    public UpcomingAdapter(Context context, ArrayList<UpcomingModel> list) {
        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public upcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upcoming, parent, false);
        return new upcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull upcomingViewHolder holder, int position) {
        UpcomingModel user = list.get(position);
        holder.mName.setText(user.getName());
        holder.mDate.setText(user.getDate());
        holder.mTime.setText(user.getTime());
        holder.join_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pre = Constants.meet_url_prefix;
                String join_url = pre + user.getMeetId() + "/preview";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(join_url));
                context.startActivity(browserIntent);
            }
        });
        holder.delete_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                Query meetQuery = userRef.child(currentUserId).child("upcoming").orderByChild("meetId").equalTo(user.getMeetId());
                meetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                context.startActivity(new Intent(context, HomeActivity.class));
            }
        });
        holder.share_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pre = Constants.meet_url_prefix;
                String join_url = pre + user.getMeetId() + "/preview";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Join meet using this link " + join_url);
                context.startActivity(Intent.createChooser(intent, "Share via"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class upcomingViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mDate, mTime;
        ImageButton join_up, delete_up, share_up;

        public upcomingViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);
            join_up = itemView.findViewById(R.id.join_up);
            delete_up = itemView.findViewById(R.id.delete_up);
            share_up = itemView.findViewById(R.id.share_up);
        }
    }
}
