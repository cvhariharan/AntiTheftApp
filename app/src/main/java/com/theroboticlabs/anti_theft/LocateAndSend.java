package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocateAndSend extends IntentService {

    private static final String TAG = "com.theroboticlabs";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationClient;


    public LocateAndSend() {
        super("LocateAndSend");
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        String number = intent.getExtras().getString("number");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.d(TAG, "onSuccess: "+location.getLatitude() + " " + location.getLongitude());
                    Toast.makeText(getBaseContext(), "Location: "+location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//        Log.d(TAG, "onHandleIntent: "+number);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Toast.makeText(getBaseContext(), location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_LONG);
//                Log.d(TAG, "onLocationChanged: "+location.getLongitude());
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

//    }

}
