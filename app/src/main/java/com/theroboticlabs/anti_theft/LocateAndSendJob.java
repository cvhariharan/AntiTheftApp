package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.support.constraint.Constraints.TAG;


/**
 * Created by hariharan on 7/10/18.
 */


@SuppressLint("NewApi")
public class LocateAndSendJob extends JobService {

    private AsyncTask background;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "LocateAndSendJob";
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {

        background = new AsyncTask() {
            String number = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                    .getString(getBaseContext().getString(R.string.phone_key),"");
            @SuppressLint("MissingPermission")
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d(TAG, "doInBackground: ");
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String smsBody = "Location: "+location.getLatitude() + " " + location.getLongitude();
                            Log.d(TAG, "onSuccess: "+location.getLatitude() + " " + location.getLongitude());
                            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, smsBody, null, null);
                            Log.d(TAG, "SMS sent!");
                            Toast.makeText(getBaseContext(), smsBody, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Couldn't locate", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

//    @Override
//    public boolean onStartJob(final JobParameters params) {
//        background = new AsyncTask() {
//            String number = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getBaseContext())
//                    .getString(getBaseContext().getString(R.string.phone_key),"");
//            @SuppressLint("MissingPermission")
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
//                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            String smsBody = "Location: "+location.getLatitude() + " " + location.getLongitude();
//                            Log.d(TAG, "onSuccess: "+location.getLatitude() + " " + location.getLongitude());
//                            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
//                            smsManager.sendTextMessage(number, null, smsBody, null, null);
//                            Log.d(TAG, "SMS sent!");
//                            Toast.makeText(getBaseContext(), smsBody, Toast.LENGTH_LONG).show();
//                        }
//                        else {
//                            Toast.makeText(getBaseContext(), "Couldn't locate", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                jobFinished(params, false);
//            }
//        };
//        return false;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        return false;
//    }
}
