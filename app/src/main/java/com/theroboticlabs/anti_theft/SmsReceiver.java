package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_KEY = "pdus";
    private FirebaseJobDispatcher mDispatcher;
    private static final String JOB_ID = "SMS_JOB";
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(final Context context, Intent intent) {
        String registeredNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.phone_key),"");
        String phrase = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.phrase_key),"locate");
        Bundle extras = intent.getExtras();
        if(extras != null) {
            Object[] allSms = (Object[])extras.get(SMS_KEY);
            for(int i = 0; i < allSms.length; i++){
                SmsMessage message = SmsMessage.createFromPdu((byte[]) allSms[i]);
                String body = message.getMessageBody();
                final String number = message.getOriginatingAddress();
                if(phrase.equals(body) && registeredNumber.equals(number)){
                    Toast.makeText(context, "Locating...", Toast.LENGTH_LONG).show();
//                    mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
//                    Job smsJob = mDispatcher.newJobBuilder()
//                            .setService(LocateAndSendJob.class)
//                            .setReplaceCurrent(true)
//                            .setTrigger(Trigger.executionWindow(0, 0))
//                            .setTag(JOB_ID)
//                            .build();
//                    mDispatcher.schedule(smsJob);
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String smsBody = "Location: "+location.getLatitude() + " " + location.getLongitude();
                                //Log.d(TAG, "onSuccess: "+location.getLatitude() + " " + location.getLongitude());
                                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                smsManager.sendTextMessage(number, null, smsBody, null, null);
                                //Log.d(TAG, "SMS sent!");
                                Toast.makeText(context, smsBody, Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(context, "Couldn't locate", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
//                    Intent locate = new Intent(context, LocateAndSend.class);
//                    locate.putExtra("number", number);
//                    context.startService(locate);
                }
            }
        }
    }
}
