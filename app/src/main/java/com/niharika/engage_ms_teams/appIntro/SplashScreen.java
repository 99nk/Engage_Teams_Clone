package com.niharika.engage_ms_teams.appIntro;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.niharika.engage_ms_teams.R;
import com.niharika.engage_ms_teams.authentication.LoginActivity;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        Splash ob = new Splash();
        ob.start();
    }

    private class Splash extends Thread {
        public void run() {
            try {
                sleep(4500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            SplashScreen.this.finish();
        }
    }
}