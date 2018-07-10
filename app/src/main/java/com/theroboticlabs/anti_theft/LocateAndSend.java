package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
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
        final Context mContext = this;
        final String number = intent.getExtras().getString("number");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String smsBody = "Location: "+location.getLatitude() + " " + location.getLongitude();
                    Log.d(TAG, "onSuccess: "+location.getLatitude() + " " + location.getLongitude());
                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, smsBody, null, null);
                    Log.d(TAG, "SMS sent!");
                    Toast.makeText(mContext, smsBody, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(mContext, "Couldn't locate", Toast.LENGTH_LONG).show();
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
