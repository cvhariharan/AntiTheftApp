package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.android.gms.location.FusedLocationProviderClient;

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
                    Intent l = new Intent(context, LocateAndSendJob.class);
                    l.putExtra("number", number);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(l);
                    }
                    else
                        context.startService(l);
                }
            }
        }
    }
}
