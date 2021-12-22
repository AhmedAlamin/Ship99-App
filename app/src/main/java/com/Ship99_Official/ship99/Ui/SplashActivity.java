package com.Ship99_Official.ship99.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Ship99_Official.ship99.R;
import com.onesignal.OneSignal;

public class SplashActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "14be1c2d-84b4-48d7-a59e-47f986717dc4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getSupportActionBar().hide();



        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        final TextView tv = (TextView) findViewById(R.id.loloText);

        final Intent i = new Intent(this,WelcomeActivity.class);



        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       tv.setText("To");
                    }
                }, 1500);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText("Ship99");
            }
        }, 3000);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your Code
                startActivity(i);
                finish();
            }
        }, 5000);

            }

    }


