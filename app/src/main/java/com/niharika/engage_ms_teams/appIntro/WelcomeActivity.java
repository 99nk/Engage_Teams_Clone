package com.niharika.engage_ms_teams.appIntro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.niharika.engage_ms_teams.R;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    //make View pager object
    private ViewPager mPager;
    private LinearLayout Dots_Layout;
    private ImageView[] dots;
    private Button BnNext,BnSkip;
    private  int[] layouts={R.layout.first_slide,R.layout.second_slide,R.layout.third_slide,R.layout.fourth_slide};
    private MPagerAdapter mpagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new PreferenceManager(this).checkPreference())
        {
            loadHome();
        }
        if(Build.VERSION.SDK_INT>=19)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_welcome);
        //initialize the object
        mPager=(ViewPager)findViewById(R.id.viewPager);
        mpagerAdapter=new MPagerAdapter(layouts,this);
        mPager.setAdapter(mpagerAdapter);
        Dots_Layout=(LinearLayout)findViewById(R.id.dotsLayout);
        BnNext=(Button)findViewById(R.id.bnNext);
        BnSkip=(Button)findViewById(R.id.bnSkip);
        BnNext.setOnClickListener(this);
        BnSkip.setOnClickListener(this);
        createDots(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
                if(position==layouts.length-1)
                {
                    BnNext.setText("Start");
                    BnSkip.setVisibility(View.INVISIBLE);
                }
                else {
                    BnNext.setText("Next");
                    BnSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void createDots(int current_position)
    {
        if(Dots_Layout!=null)
            Dots_Layout.removeAllViews();

        dots=new ImageView[layouts.length];
        for (int i=0;i<layouts.length;i++)
        {
            dots[i]=new ImageView(this);
            if(i==current_position)
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dots));
            }
            else
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.default_dots));
            }
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            Dots_Layout.addView(dots[i],params);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bnNext:
                loadNextSlide();
                break;
            case R.id.bnSkip:
                loadHome();
                new PreferenceManager(this).writePreference();
                break;
        }
    }
    private void loadHome()
    {
        startActivity(new Intent(this,SplashScreen.class));
        finish();
    }
    private void loadNextSlide()
    {
        int next_slide=mPager.getCurrentItem()+1;
        if(next_slide<layouts.length)
        {
            mPager.setCurrentItem(next_slide);
        }
        else {
            loadHome();
            new PreferenceManager(this).writePreference();
        }
    }
}

