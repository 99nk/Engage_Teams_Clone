package com.niharika.engage_ms_teams;

import android.content.Intent;
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

import androidx.annotation.NonNull;
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
import com.niharika.engage_ms_teams.adapter.inviteAdapter;
import com.niharika.engage_ms_teams.model.inviteModel;
import com.niharika.engage_ms_teams.model.upcomingModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity
{
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

    groupChatAdapter myAdapter;
    ArrayList<groupChatModel> myList;
    RecyclerView recyclerView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        joinTeamMeet=findViewById(R.id.joinTeamMeet);
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        int l=currentGroupName.length();
        title=currentGroupName.substring(0,l-37);
//        int grpnameendindex=0;
//        for(int i=0;i<currentGroupName.length();i++)
//        {
//            if(currentGroupName.charAt(i)=='\n')
//            {
//                grpnameendindex=i;
//                break;
//            }
//        }
//        String name=currentGroupName.substring(0,grpnameendindex);
//        meet_url=currentGroupName.substring(grpnameendindex+5,currentGroupName.length());
//        currentGroupName=name;
//        meet_url=currentGroupName+meet_url;
//        title=currentGroupName;
//        currentGroupName=meet_url;
        meet_url=currentGroupName;
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        recyclerView=findViewById(R.id.recycler_group_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myList=new ArrayList<>();
        myAdapter=new groupChatAdapter(this,myList);
        recyclerView.setAdapter(myAdapter);

        InitializeFields();


        GetUserInfo();

        joinTeamMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pre="https://test-video12.herokuapp.com/";//Enter here
                String join_url=pre+meet_url+"/preview";
                Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(join_url));
                startActivity(browserIntent);
                finish();
                Toast.makeText(GroupChatActivity.this, "url is"+meet_url, Toast.LENGTH_SHORT).show();
            }
        });

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveMessageInfoToDatabase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }



    @Override
    protected void onStart()
    {
        super.onStart();
//        GroupNameRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                for(DataSnapshot dataSnapshot:snapshot.getChildren())
//                {
//                    groupChatModel user=dataSnapshot.getValue(groupChatModel.class);
//                    myList.add(user);
//                }
//                myAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
                    //DisplayMessages(dataSnapshot);
                    //for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    //{
                        groupChatModel user=dataSnapshot.getValue(groupChatModel.class);
                        myList.add(user);
                    //}
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if (dataSnapshot.exists())
                {
                    //DisplayMessages(dataSnapshot);
                    groupChatModel user=dataSnapshot.getValue(groupChatModel.class);
                    myList.add(user);
                    //}
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
    private void InitializeFields()
    {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }



    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void SaveMessageInfoToDatabase()
    {
        String message = userMessageInput.getText().toString();
        String messagekEY = GroupNameRef.push().getKey();//used to create unique key values-auto generated

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            messagekEY=currentDate+currentTime;

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



    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
//        Iterator iterator = dataSnapshot.getChildren().iterator();
//
//        while(iterator.hasNext())
//        {
//            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
//
//            displayTextMessages.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");
//
//            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
//        }
        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
        {
            groupChatModel user=dataSnapshot1.getValue(groupChatModel.class);
            myList.add(user);
        }
        myAdapter.notifyDataSetChanged();
    }
}