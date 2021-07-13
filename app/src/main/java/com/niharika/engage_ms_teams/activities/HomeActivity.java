package com.niharika.engage_ms_teams.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.adapter.InviteAdapter;
import com.niharika.engage_ms_teams.adapter.UpcomingAdapter;
import com.niharika.engage_ms_teams.appIntro.WelcomeActivity;
import com.niharika.engage_ms_teams.authentication.LoginActivity;
import com.niharika.engage_ms_teams.model.InviteModel;
import com.niharika.engage_ms_teams.model.UpcomingModel;
import com.niharika.engage_ms_teams.utils.CodeDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    RelativeLayout relativeLayout;
    private String currentUserID;
    private Toolbar mToolbar;
    Button newMeeting, joinWithCode, scheduleMeet;
    RecyclerView invite, upcoming;
    DatabaseReference databaseReference;
    InviteAdapter myInviteAdapter;
    ArrayList<InviteModel> inviteList;
    UpcomingAdapter myUpcomingAdapter;
    ArrayList<UpcomingModel> upcomingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        relativeLayout = findViewById(R.id.rl);
        mToolbar = (Toolbar) findViewById(R.id.home_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Microsoft Teams");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UserRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        newMeeting = findViewById(R.id.newMeeting);
        joinWithCode = findViewById(R.id.joinUsingLink);
        scheduleMeet = findViewById(R.id.scheduleMeetButton);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);


        newMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, StartInstantMeet.class));
            }
        });

        joinWithCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        scheduleMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ScheduleMeet.class));
            }
        });

        invite = findViewById(R.id.invites_recycler);
        upcoming = findViewById(R.id.upcoming_recycler);

        invite.setHasFixedSize(true);
        invite.setLayoutManager(new LinearLayoutManager(this));
        inviteList = new ArrayList<>();
        myInviteAdapter = new InviteAdapter(this, inviteList);
        invite.setAdapter(myInviteAdapter);

        upcoming.setHasFixedSize(true);
        upcoming.setLayoutManager(new LinearLayoutManager(this));
        upcomingList = new ArrayList<>();
        myUpcomingAdapter = new UpcomingAdapter(this, upcomingList);
        upcoming.setAdapter(myUpcomingAdapter);


        databaseReference.child(currentUserID).child("invite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InviteModel user = dataSnapshot.getValue(InviteModel.class);
                    inviteList.add(user);
                }
                myInviteAdapter.notifyDataSetChanged();
                int upcomingMeets = myInviteAdapter.getItemCount();
                if (upcomingMeets != 0)
                    relativeLayout.setBackgroundResource(R.drawable.back);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child(currentUserID).child("upcoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UpcomingModel user = dataSnapshot.getValue(UpcomingModel.class);
                    upcomingList.add(user);
                }
                myUpcomingAdapter.notifyDataSetChanged();
                int i = myUpcomingAdapter.getItemCount();
                if (i != 0)
                    relativeLayout.setBackgroundResource(R.drawable.back);
                else
                    relativeLayout.setBackgroundResource(R.drawable.sleep);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        case R.id.nav_teams:
                            Intent teamIntent = new Intent(HomeActivity.this, TeamsActivity.class);
                            teamIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(teamIntent);
                            break;
                        case R.id.nav_chat:
                            Intent chatIntent = new Intent(HomeActivity.this, MainChatActivity.class);
                            chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(chatIntent);
                            break;
                        case R.id.nav_profile:
                            Intent profileIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(profileIntent);
                    }
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option) {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_feedback_option) {
            startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
        }
        if (item.getItemId() == R.id.main_help_option) {
            startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            SendUserToLoginActivity();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null) {
            updateUserStatus("offline");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);
        UserRef.child("Users").child(currentUserID).child("userState").updateChildren(onlineStateMap);

    }

    private void openDialog() {
        CodeDialog codeDialog = new CodeDialog();
        codeDialog.show(getSupportFragmentManager(), "code dialog");
    }

}