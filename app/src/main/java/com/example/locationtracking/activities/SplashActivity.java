package com.example.locationtracking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.locationtracking.R;
import com.facebook.drawee.backends.pipeline.Fresco;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        Thread mSplash = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 2 seconds
                    sleep(SPLASH_DURATION);
                    Intent intent = new Intent(getBaseContext(), UserLoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        mSplash.start();
    }
}
