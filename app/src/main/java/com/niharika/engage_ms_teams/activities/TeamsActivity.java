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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.adapter.teamsAdapter;
import com.niharika.engage_ms_teams.appIntro.WelcomeActivity;
import com.niharika.engage_ms_teams.authentication.LoginActivity;
import com.niharika.engage_ms_teams.model.teamsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TeamsActivity extends AppCompatActivity
{
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    FirebaseAuth mAuth;
    String current_user_id;
    private Toolbar mToolbar;
    private DatabaseReference GroupRef,RootRef;

    teamsAdapter myAdapter;
    ArrayList<teamsModel> myList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        mToolbar = (Toolbar) findViewById(R.id.teams_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Microsoft Teams");

        BottomNavigationView bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        bottomNav.getMenu().findItem(R.id.nav_teams).setChecked(true);

        recyclerView=findViewById(R.id.recycler_teams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myList=new ArrayList<>();
        myAdapter=new teamsAdapter(this,myList);
        recyclerView.setAdapter(myAdapter);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id).child("groups");
        IntializeFields();
        RetrieveAndDisplayGroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(TeamsActivity.this, GroupChatActivity.class);
                groupChatIntent.putExtra("groupName" , currentGroupName);
                startActivity(groupChatIntent);
            }
        });

    }
    private void IntializeFields()
    {
        list_view = findViewById(R.id.list_view_teams);
        arrayAdapter = new ArrayAdapter<String>(TeamsActivity.this, R.layout.item_listview, list_of_groups);
        list_view.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups()
    {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = dataSnapshot.getChildren().iterator();
//
//                while (iterator.hasNext())
//                {
////                    String grp=((DataSnapshot)iterator.next()).getKey();
////                    int l=grp.length();
////                    int endindx=l-8;
////                    String displayName=grp.substring(0,endindx);
////                    set.add(displayName);
//                      String grp=((DataSnapshot)iterator.next()).getKey();
//                      int l=grp.length();
//                      if(grp.length()>36)
//                      grp=grp.substring(0,l-36)+"\nId: "+grp.substring(l-36,l);
//                      set.add(grp);
//                }
//
//                list_of_groups.clear();
//                list_of_groups.addAll(set);
//                arrayAdapter.notifyDataSetChanged();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    teamsModel user=dataSnapshot.getValue(teamsModel.class);
                    myList.add(user);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.nav_home:
                            Intent intent=new Intent(TeamsActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.nav_teams:
                            Intent Pintent=new Intent(TeamsActivity.this, TeamsActivity.class);
                            Pintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(Pintent);
                            finish();
                            break;
                        case R.id.nav_chat:
                            Intent Pintent1=new Intent(TeamsActivity.this, MainChatActivity.class);
                            Pintent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(Pintent1);
                            finish();
                            break;
                        case R.id.nav_profile:
                            Intent Pintent2=new Intent(TeamsActivity.this, SettingsActivity.class);
                            Pintent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(Pintent2);
                            finish();
                            break;
                    }
                    return true;
                }
            };
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option)
        {
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_feedback_option)
        {
            startActivity(new Intent(TeamsActivity.this, FeedbackActivity.class));
            finish();
        }
        if (item.getItemId() == R.id.main_help_option)
        {
            startActivity(new Intent(TeamsActivity.this, WelcomeActivity.class));
            finish();
        }
        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(TeamsActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    private void updateUserStatus(String state)
    {
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

        RootRef.child("Users").child(current_user_id).child("userState")
                .updateChildren(onlineStateMap);

    }

}