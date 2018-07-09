package com.theroboticlabs.anti_theft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_KEY = "pdus";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null) {
            Object[] allSms = (Object[])extras.get(SMS_KEY);
            for(int i = 0; i < allSms.length; i++){
                SmsMessage message = SmsMessage.createFromPdu((byte[]) allSms[i]);
                String body = message.getMessageBody();
                String number = message.getOriginatingAddress();
                Toast.makeText(context, body+" "+number, Toast.LENGTH_LONG).show();
            }
        }
    }
}
