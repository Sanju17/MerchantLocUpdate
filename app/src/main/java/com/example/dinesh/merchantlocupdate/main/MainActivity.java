package com.example.dinesh.merchantlocupdate.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dinesh.merchantlocupdate.GPSTracker;
import com.example.dinesh.merchantlocupdate.R;

public class MainActivity extends AppCompatActivity {

    GPSTracker gps;
    private  double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat=getIntent().getDoubleExtra("lat",0);
        lon=getIntent().getDoubleExtra("lon",0);
        Log.d("dinesh","lat lng from main "+ lat+lon);

        gps = new GPSTracker(this);
    }

    public void scanQRCode(View v){
        Intent i = new Intent(MainActivity.this, SubActivity.class);
        i.putExtra("lat",lat);
        i.putExtra("lon",lon);
        startActivity(i);
    }
}
