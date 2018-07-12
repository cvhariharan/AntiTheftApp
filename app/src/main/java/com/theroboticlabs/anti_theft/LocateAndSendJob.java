package com.theroboticlabs.anti_theft;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;



/**
 * Created by hariharan on 7/10/18.
 */

public class LocateAndSendJob extends Service {

    private SmsReceiver smsReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "LocateAndSendJob";
    private static final String CHANNEL_ID = "running";
    private static final int PINTENT_REQUEST = 42;
    private static final int NOTIFICATION_ID = 22;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

        createNotificationChannel();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String number;

                Intent i = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, PINTENT_REQUEST, i, 0);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Anti-Theft")
                        .setContentText("App running and listening for SMS")
                        .setChannelId(CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pIntent)
                        .build();

                startForeground(NOTIFICATION_ID, notification);
        if (intent.hasExtra("number")) {
            number = intent.getExtras().getString("number");
            if (!"".equals(number)) {
                Runnable run = new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    String smsBody = "Location: " + location.getLatitude() + " " + location.getLongitude();
                                    Log.d(TAG, "onSuccess: " + location.getLatitude() + " " + location.getLongitude());
                                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                    smsManager.sendTextMessage(number, null, smsBody, null, null);
                                    Log.d(TAG, "SMS sent!");
//                                    Toast.makeText(context, smsBody, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, "Couldn't Locate. NULL");
//                                    Toast.makeText(context, "Couldn't locate", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                };
                Handler mainHandler = new Handler(getMainLooper());
                mainHandler.post(run);
//                Thread smsThread = new Thread(run);
//                smsThread.start();
                stopSelf();
            }

        }

        return START_STICKY;
    }

    public void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Running", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Running services channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
    }

}
