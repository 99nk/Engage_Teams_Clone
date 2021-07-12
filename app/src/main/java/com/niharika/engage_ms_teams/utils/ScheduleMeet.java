package com.niharika.engage_ms_teams.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.activities.HomeActivity;
import com.niharika.engage_ms_teams.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

public class ScheduleMeet extends AppCompatActivity {
    Button setDate,setTime,next,done,send;
    TextView set_date,set_time;
    Boolean checkTime,checkDate,checkTitle;
    String date,time;
    FirebaseAuth mAuth;
    DatabaseReference userRef,RootRef;
    EditText email,team_name;
    LinearLayout send_invite_mail,enter_meet_details;
    String user_id,user_email,sender_name;
    String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meet);

        checkTime=false;
        checkDate=false;
        checkTitle=false;

        date=" ";
        time=" ";
        send_invite_mail=findViewById(R.id.send_invite_mail);
        enter_meet_details=findViewById(R.id.enter_meet_details_schedule);
        setDate=findViewById(R.id.setDate);
        setTime=findViewById(R.id.setTime);
        next=findViewById(R.id.next);
        done=findViewById(R.id.done);
        team_name=findViewById(R.id.team_name);
        email=findViewById(R.id.email_invite);
        send=findViewById(R.id.send_invite);
        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();
        user_email=mAuth.getCurrentUser().getEmail();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef= FirebaseDatabase.getInstance().getReference();
        set_date=findViewById(R.id.set_date_text);
        set_time=findViewById(R.id.set_time_text);


        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                java.util.Calendar calendar= java.util.Calendar.getInstance();
                int HOUR=calendar.get(Calendar.HOUR);
                int MINUTE=calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog=new TimePickerDialog(ScheduleMeet.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        time=hourOfDay+":"+minute;
                        set_time.setText(time);
                        checkTime=true;
                    }
                },HOUR,MINUTE,true);
                timePickerDialog.show();

            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar calendar= java.util.Calendar.getInstance();
                int YEAR=calendar.get(Calendar.YEAR);
                int MONTH=calendar.get(Calendar.MONTH);
                int DATE=calendar.get(Calendar.DATE);
                DatePickerDialog datePickerDialog=new DatePickerDialog(ScheduleMeet.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date=dayOfMonth+"."+month+"."+year;
                        set_date.setText(date);
                        checkDate=true;
                    }
                },YEAR,MONTH,DATE);
                datePickerDialog.show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName = team_name.getText().toString().trim();
                //uuid has 36 characters

                if (groupName.length() > 0)
                    checkTitle = true;

                if (checkTitle && checkDate & checkTime) {
                    groupName += UUID.randomUUID().toString();

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            sender_name = snapshot.child(user_id).child("name").getValue().toString();
                            DatabaseReference inRef = userRef.child(user_id).child("upcoming");
                            String finalGroupName = groupName;
                            inRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    HashMap user1 = new HashMap();
                                    user1.put("name", "me");
                                    user1.put("date", date);
                                    user1.put("time", time);
                                    user1.put("meetId", finalGroupName);

                                    Calendar calFordDate = Calendar.getInstance();
                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String saveCurrentDate = currentDate.format(calFordDate.getTime());
                                    Calendar calFordTime = Calendar.getInstance();
                                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                                    String saveCurrentTime = currentTime.format(calFordDate.getTime());
                                    int date1=calFordDate.get(Calendar.DAY_OF_MONTH);
                                    int month=calFordDate.get(Calendar.MONTH);

                                    int year=calFordDate.get(Calendar.YEAR);
                                    String postRandomName;
                                    if(month<10)
                                    {
                                        if(date1<10)
                                            postRandomName=Integer.toString(year)+"0"+Integer.toString(month)+"0"+Integer.toString(date1)+saveCurrentTime;
                                        else
                                            postRandomName=Integer.toString(year)+"0"+Integer.toString(month)+Integer.toString(date1)+saveCurrentTime;
                                    }
                                    else
                                    {
                                        if(date1<10)
                                            postRandomName=Integer.toString(year)+Integer.toString(month)+"0"+Integer.toString(date1)+saveCurrentTime;
                                        else
                                            postRandomName=Integer.toString(year)+Integer.toString(month)+Integer.toString(date1)+saveCurrentTime;
                                    }



                                    inRef.child(postRandomName).updateChildren(user1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful())
                                                Toast.makeText(ScheduleMeet.this, "Your meeting is created", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //create a group

                    //RootRef.child("Groups").child(groupName).setValue(" ");
                    RootRef.child("Groups").child(groupName).setValue(" ")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ScheduleMeet.this, " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    RootRef.child("Users").child(user_id).child("groups").child(groupName).child("group_name").setValue(groupName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ScheduleMeet.this, "You are added in the group", Toast.LENGTH_SHORT).show();
                                }
                            });
                    enter_meet_details.setVisibility(View.GONE);
                    send_invite_mail.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(ScheduleMeet.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }
            }

        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //send mail
                String emailId=email.getText().toString();

                Intent mailIntent =new Intent(Intent.ACTION_VIEW);
                Uri data=Uri.parse("mailto:?subject=" + "Meeting link-Microsoft Teams" + "&body=" + "body text " + "&to=" + emailId);
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent,"Send mail...."));

                //add meet invite
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            String receipt_id=dataSnapshot.child("id").getValue().toString();
                            String email_id = dataSnapshot.child("email").getValue().toString();
                            DatabaseReference inRef=userRef.child(receipt_id).child("invite");
                            inRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if(email_id.equals(emailId))
                                    {
                                        HashMap user1=new HashMap();
                                        user1.put("name",sender_name);
                                        user1.put("date",date);
                                        user1.put("time",time);
                                        user1.put("meetId",groupName);
                                        Calendar calFordDate = Calendar.getInstance();
                                        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                        String saveCurrentDate = currentDate.format(calFordDate.getTime());
                                        Calendar calFordTime = Calendar.getInstance();
                                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                                        String saveCurrentTime = currentTime.format(calFordDate.getTime());
                                        int date1=calFordDate.get(Calendar.DAY_OF_MONTH);
                                        int month=calFordDate.get(Calendar.MONTH);

                                        int year=calFordDate.get(Calendar.YEAR);
                                        String postRandomName;
                                        if(month<10)
                                        {
                                            if(date1<10)
                                                postRandomName=Integer.toString(year)+"0"+Integer.toString(month)+"0"+Integer.toString(date1)+saveCurrentTime;
                                            else
                                                postRandomName=Integer.toString(year)+"0"+Integer.toString(month)+Integer.toString(date1)+saveCurrentTime;
                                        }
                                        else
                                        {
                                            if(date1<10)
                                                postRandomName=Integer.toString(year)+Integer.toString(month)+"0"+Integer.toString(date1)+saveCurrentTime;
                                            else
                                                postRandomName=Integer.toString(year)+Integer.toString(month)+Integer.toString(date1)+saveCurrentTime;
                                        }
                                        userRef.child(receipt_id).child("invite").child(postRandomName).updateChildren(user1).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                if(task.isSuccessful())
                                                    Toast.makeText(ScheduleMeet.this, "Invite sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();

            }
        });
    }
}