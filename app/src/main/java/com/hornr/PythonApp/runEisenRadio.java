package com.hornr.PythonApp;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.PyException;
import com.chaquo.python.android.AndroidPlatform;
import com.hornr.R;

import java.util.Arrays;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

public class runEisenRadio extends Service {
    // class that extends the Service class and overrides its methods.

    private static final String LOG_TAG = "[runEisenRadio]";
    private Context context;
    private final int NOTIFICATION_ID = 123;
    private final String CHANNEL_ID = "321";
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
        Toast.makeText(context, "Starting service ...", Toast.LENGTH_SHORT).show();
        try {
            doTask();
        } catch (ExecutionException | InterruptedException | IOException e) {
            String errMsg = "RuntimeException in doTask() " + LOG_TAG;
            Log.i(LOG_TAG, errMsg, e);
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startID);
    }

    void RadioRecorderStart() {
        /* Python package */
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        try {
            PyObject mock = py.getModule("eisen_mock");  // mocked for Android
            Log.i(LOG_TAG, "Start Flask and Recorder ...");
            mock.callAttr("main_mock", "None");  // run a pkg that blocks the thread
        }catch (PyException e){
            String msg = "eisen" + "\n\tcustom error " + e.getMessage();
            Log.d(LOG_TAG, msg);
            Log.i(LOG_TAG, Arrays.toString(e.getStackTrace()));
        }
    }
    private void doTask() throws IOException, ExecutionException, InterruptedException {

        // Python app in blocked thread.
        new Thread(this::RadioRecorderStart).start();
        // Application info collector for user notification channel
        RadioRecorderInfo appInfo = new RadioRecorderInfo();

        final String[] notifyUpd = new String[1];
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            // Service loop
            for(int i=0; i <= 100; i++) {
                if(isDestoyed) {
                    break;
                }
                try {
                    notifyUpd[0] = appInfo.AppServerInfoGet();  // callable
                    Log.d(LOG_TAG, notifyUpd[0]);
                } catch (ExecutionException | IOException | InterruptedException e) {
                    Log.d(LOG_TAG + " ::app:: ", e.toString());
                }
                try {
                    handler.post(() -> {
                        updateNotification(String.valueOf(notifyUpd[0]));  // update Android UI
                    });
                    Thread.sleep(5000);
                    if (i % 2 == 0) {
                        Log.w(LOG_TAG + " ::AVD:: ", "AVD *Browser* and *App notifications*. Enable them!");
                    }
                    if (i == 100) {
                        i = 0;
                    }
                } catch (InterruptedException e) {
                    Log.i(LOG_TAG + " ::loop:: ", Arrays.toString(e.getStackTrace()));
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
    public IBinder onBind(Intent intent) {  // A bound service lives only while it serves another component.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestoyed = true;
        Toast.makeText(context, "Stopping service ...", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "In onDestroy");  // button not implemented yet, kill app so far
    }
}

