package com.niharika.engage_ms_teams.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niharika.engage_ms_teams.activities.HomeActivity;
import com.niharika.engage_ms_teams.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText mName,mEmail,mPassword,mPhone;
    Button mRegister;
    public String email,password;
    TextView mLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName=findViewById(R.id.reg_name);
        mEmail=findViewById(R.id.reg_email);
        mPassword=findViewById(R.id.reg_password);
        //mPhone=findViewById(R.id.reg_ph);
        mRegister=findViewById(R.id.regbtn);
        mLogin=findViewById(R.id.reg_login);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.reg_progress);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
//        if(mAuth.getCurrentUser()!=null)
//        {
//            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
//            finish();
//        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=mEmail.getText().toString().trim();
                password=mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is req");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is req");
                    return;
                }
                if(password.length()<6)
                {
                    mPassword.setError("Min 6 char password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            final String current_user_id=mAuth.getCurrentUser().getUid();
                            final DatabaseReference UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
                            UserRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(!dataSnapshot.hasChild(current_user_id))
                                    {
                                        String id =mAuth.getCurrentUser().getUid().toString();
                                        String Mail=email;
                                        String Name=mName.getText().toString().trim();
                                        HashMap user1=new HashMap();
                                        user1.put("name",Name);
                                        user1.put("email",Mail);
                                        user1.put("id",id);
                                        user1.put("status","Connecting with microsoft teams!");
                                        //user1.put("userState"," ");

                                        UserRef.child(id).updateChildren(user1).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful())
                                                {
                                                    //progressBar.setVisibility(View.GONE);
                                                    updateUserStatus("online");
                                                    Toast.makeText(RegisterActivity.this, "Details added", Toast.LENGTH_SHORT).show();
                                                    int interval=3000;
                                                    Handler handler=new Handler();
                                                    Runnable runnable=new Runnable()
                                                    {
                                                        @Override
                                                        public void run() {
                                                            progressBar.setVisibility(View.GONE);
                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        }
                                                    };
                                                    handler.postAtTime(runnable,System.currentTimeMillis()+interval);
                                                    handler.postDelayed(runnable,interval);

                                                    //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        //Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

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
        DatabaseReference UserRef=FirebaseDatabase.getInstance().getReference();
        String currentUserID=mAuth.getCurrentUser().getUid();
        UserRef.child("Users").child(currentUserID).child("userState").updateChildren(onlineStateMap);
    }
}