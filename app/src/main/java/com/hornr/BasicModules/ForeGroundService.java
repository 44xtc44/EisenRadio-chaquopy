package com.hornr.BasicModules;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Intent;
import android.content.Context;

import android.os.Build;
import android.os.Looper;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;
import android.widget.Toast;

import com.hornr.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
/*
        Needs Android Network access config
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        In Android Manifest, in tag <application add:

        android:usesCleartextTraffic="true"
        android:networkSecurityConfig="@xml/network_security_config"
        <uses-library android:name="org.apache.http.legacy" android:required="false"/>

        Create a resource folder type xml and add a file *network_security_config.xml* and write this:

        <?xml version="1.0" encoding="utf-8"?>
          <network-security-config>
            <base-config cleartextTrafficPermitted="true" />
          </network-security-config>
        For Certificate Authorities (CA) see https://developer.android.com/privacy-and-security/security-config
 */
public class ForeGroundService extends Service {
    // class that extends the Service class and overrides its methods.

    private static final String LOG_TAG = "[ForegroundService]";
    private Context context;
    private final int NOTIFICATION_ID = 1;  // min
    private final String CHANNEL_ID = "100";
    private boolean isDestoyed = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        startForeground(NOTIFICATION_ID, showNotification("This is content"));
    }

    private Notification showNotification(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "ForeGround Notification",
                            NotificationManager.IMPORTANCE_HIGH));
        }
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ForeGround Service")
                .setContentText(content)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Toast.makeText(context, "Starting service ... (see Logcat)", Toast.LENGTH_SHORT).show();
        doTask();
        return super.onStartCommand(intent, flags, startID);
    }

    private void doTask() {

        final int[] data = new int[1];
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            // task
            for(int i=0; i <= 100; i++) {
                if(isDestoyed) {
                    break;
                }
                data[0] = i;
                try {
                    handler.post(() -> {
                        // update UI
                        updateNotification(String.valueOf(data[0]));
                    });
                    Thread.sleep(2500);
                    Log.i(LOG_TAG, "doTask: enable *App notifications* in VM to see the counter : " + i);
                    if (i == 100) {
                        i = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateNotification(String data) {
        Notification notification = showNotification(data);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {  // A bound service lives only while it serves another component
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestoyed = true;
        Toast.makeText(context, "Stopping service ...(see Logcat)", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "In onDestroy");
    }
}
