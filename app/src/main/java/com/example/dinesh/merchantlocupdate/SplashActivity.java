package com.example.dinesh.merchantlocupdate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.dinesh.merchantlocupdate.main.MainActivity;
import com.google.android.gms.nearby.messages.Message;

/**
 * Created by Owner on 7/31/2016.
 */
public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);


        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();
            Log.d("dinesh","lat : " + latitude + "lon from splash : "+longitude);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    i.putExtra("lat",latitude);
                    i.putExtra("lon",longitude);
                    Toast.makeText(SplashActivity.this, "lat: " + latitude + "\nlong: " + longitude, Toast.LENGTH_SHORT).show();
                    startActivity(i);

                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}