package com.niharika.engage_ms_teams.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.niharika.engage_ms_teams.R;

public class startInstantMeet extends AppCompatActivity
{
    Button join,share;
    TextView code;
    FirebaseAuth mAuth;
    String mCode;

    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_instant_meet);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.80),(int)(height*.40));
        WindowManager.LayoutParams windowManager=getWindow().getAttributes();
        windowManager.dimAmount=0.5f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mAuth=FirebaseAuth.getInstance();
        mCode=mAuth.getCurrentUser().getUid();
        join=findViewById(R.id.join_meet);
        share=findViewById(R.id.share_code);
        code=findViewById(R.id.meet_code);
        String pre="https://test-video12.herokuapp.com/";//Enter here
        String join_url=pre+mCode+"/preview";

        code.setText(join_url);


        share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Join meet using this link "+join_url);
                startActivity(Intent.createChooser(intent,"Share via"));
            }
        });

         join.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                 Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(join_url));
                 startActivity(browserIntent);
             }
         });
    }
}