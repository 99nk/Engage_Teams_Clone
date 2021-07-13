package com.niharika.engage_ms_teams.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.niharika.engage_ms_teams.R;

public class ResetPassword extends AppCompatActivity {
    EditText mEmail;
    Button mReset;
    String email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mReset = findViewById(R.id.resetButton);
        mEmail = findViewById(R.id.enterEmailForReset);
        mAuth = FirebaseAuth.getInstance();
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPassword.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Check your email account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                            } else {
                                Toast.makeText(ResetPassword.this, "Error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}