package com.niharika.engage_ms_teams.activities;

import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.adapter.GroupChatAdapter;
import com.niharika.engage_ms_teams.model.GroupChatModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private Button joinTeamMeet;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;
    private String title;
    private String meet_url;
    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    GroupChatAdapter myAdapter;
    ArrayList<GroupChatModel> myList;
    RecyclerView recyclerView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        joinTeamMeet = findViewById(R.id.joinTeamMeet);

        //UUID is 36 characters long, so group name is of total length-36

        int lengthOfName = currentGroupName.length();
        title = currentGroupName.substring(0, lengthOfName - 36);
        meet_url = currentGroupName;

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        recyclerView = findViewById(R.id.recycler_group_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myList = new ArrayList<>();
        myAdapter = new GroupChatAdapter(this, myList);
        recyclerView.setAdapter(myAdapter);

        InitializeFields();

        GetUserInfo();

        joinTeamMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pre = "https://test-video12.herokuapp.com/";//Enter here
                String join_url = pre + meet_url + "/preview";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(join_url));
                startActivity(browserIntent);
                finish();
                Toast.makeText(GroupChatActivity.this, "url is" + meet_url, Toast.LENGTH_SHORT).show();
            }
        });

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                SaveMessageInfoToDatabase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    GroupChatModel user = dataSnapshot.getValue(GroupChatModel.class);
                    myList.add(user);

                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    GroupChatModel user = dataSnapshot.getValue(GroupChatModel.class);
                    myList.add(user);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void InitializeFields() {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }


    private void GetUserInfo() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SaveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString();
        String messagekEY = GroupNameRef.push().getKey();//used to create unique key values-auto generated

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        } else {
            android.icu.util.Calendar calFordDate = android.icu.util.Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calFordDate.getTime());

            android.icu.util.Calendar calFordTime = android.icu.util.Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calFordTime.getTime());

            android.icu.util.Calendar calFordTime1 = android.icu.util.Calendar.getInstance();
            SimpleDateFormat currentTime1 = new SimpleDateFormat("HH:mm:ss");
            String saveCurrentTime = currentTime1.format(calFordTime1.getTime());

            int date1 = calFordTime.get(android.icu.util.Calendar.DAY_OF_MONTH);
            int month = calFordTime.get(android.icu.util.Calendar.MONTH);
            int year = calFordTime.get(Calendar.YEAR);

            String postRandomName;
            if (month < 10) {
                if (date1 < 10)
                    postRandomName = Integer.toString(year) + "0" + Integer.toString(month) + "0" + Integer.toString(date1) + saveCurrentTime;
                else
                    postRandomName = Integer.toString(year) + "0" + Integer.toString(month) + Integer.toString(date1) + saveCurrentTime;
            } else {
                if (date1 < 10)
                    postRandomName = Integer.toString(year) + Integer.toString(month) + "0" + Integer.toString(date1) + saveCurrentTime;
                else
                    postRandomName = Integer.toString(year) + Integer.toString(month) + Integer.toString(date1) + saveCurrentTime;
            }
            messagekEY = postRandomName;
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef = GroupNameRef.child(messagekEY);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

}