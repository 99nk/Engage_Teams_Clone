package com.niharika.engage_ms_teams.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.activities.HomeActivity;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.model.InviteModel;

import java.util.ArrayList;
import java.util.HashMap;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.inviteViewHolder> {
    Context context;
    ArrayList<InviteModel> list;
    FirebaseAuth mAuth;
    String current_user_id;

    public InviteAdapter(Context context, ArrayList<InviteModel> list) {
        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public inviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invites, parent, false);
        return new inviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull inviteViewHolder holder, int position) {
        InviteModel user = list.get(position);
        holder.mName.setText(user.getName());
        holder.mDate.setText(user.getDate());
        holder.mTime.setText(user.getTime());
        holder.mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shift to upcoming
                HashMap user1 = new HashMap();
                user1.put("name", user.getName());
                user1.put("date", user.getDate());
                user1.put("time", user.getTime());
                user1.put("meetId", user.getMeetId());
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int HOUR = calendar.get(Calendar.HOUR);
                int MINUTE = calendar.get(Calendar.MINUTE);
                String randomKey = HOUR + "" + MINUTE;
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                userRef.child(current_user_id).child("upcoming").child(randomKey).updateChildren(user1).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                            Toast.makeText(context, "Invite accepted", Toast.LENGTH_SHORT).show();
                    }
                });

                //delete from incoming
                Query meetQuery = userRef.child(current_user_id).child("invite").orderByChild("meetId").equalTo(user.getMeetId());
                meetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                        Toast.makeText(context, "Added to upcoming meet", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //add in the group
                userRef.child(current_user_id).child("groups").child(user.getMeetId()).child("group_name").setValue(user.getMeetId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "You are added in the group", Toast.LENGTH_SHORT).show();
                    }
                });

                context.startActivity(new Intent(context, HomeActivity.class));
            }
        });
        holder.mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                Query meetQuery = userRef.child(current_user_id).child("invite").orderByChild("meetId").equalTo(user.getMeetId());
                meetQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                        Toast.makeText(context, "Meet Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                context.startActivity(new Intent(context, HomeActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class inviteViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mDate, mTime;
        Button mReject, mAccept;

        public inviteViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);
            mReject = itemView.findViewById(R.id.reject);
            mAccept = itemView.findViewById(R.id.accept);
        }
    }
}
