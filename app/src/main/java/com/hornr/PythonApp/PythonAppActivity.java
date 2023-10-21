/* Entrypoint for App, should have a progressbar in foreground since we load the default browser.
   Get storage permission.
   Calls runEisenRadio the ForeGroundService -> calls RadioRecorderInfo, Notification content
 */
package com.hornr.PythonApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.os.Build;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.pm.PackageManager;

public class PythonAppActivity extends AppCompatActivity {

    private static final String LOG_TAG = "[PythonAppActivity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StringBuilder strBuild = new StringBuilder();
        strBuild.append("\n\n\tEnable Chrome Browser and").append("\n\t");
        strBuild.append("Notifications.").append("\n\n\t");
        strBuild.append("URL \"localhost:5050\"").append("\n\n\n\n\t");
        strBuild.append("Enjoy logcat with Flask.");

        TextView textView = new TextView(this);
        textView.setTextSize(22);
        textView.setText(strBuild);
        setContentView(textView);

        String[] PERMISSIONS = PermissionArrayGet();
        PermissionsAsk(PERMISSIONS);  // ask for a permissions from the list, Android decides which is displayed
    }

    public String[] PermissionArrayGet() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            return new String[] {
                    /* <uses-permission android:name="android.permission.CAMERA" /> see multiple permission questions working at once */
                    // Manifest.permission.CAMERA,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,  // older than SDK 33 TIRAMISU
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        return new String[]{
                // Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_MEDIA_VIDEO,  // JS media player of EisenRadio
                android.Manifest.permission.READ_MEDIA_AUDIO,  // Py GhettoRecorder module of EisenRadio
        };
    }

    protected void PermissionsAsk(String[] PERMISSIONS) {

        ActivityResultLauncher<String[]> mRequestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            // Handle activity result here

            Log.d(LOG_TAG, result.toString());  // current permission

            String msgOK = "Got permission.";
            String msgFail = "No storage access. APP EXIT";

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.w(LOG_TAG+" ::Perm:: ", msgOK);
            } else if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Log.w(LOG_TAG+" ::Perm:: ", msgOK);
            } else {
                Toast.makeText(PythonAppActivity.this, msgFail, Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG+" ::fail::", msgFail);
            }
        });
        mRequestPermissionsLauncher.launch(PERMISSIONS);
        // no exit strategy implemented if denied
        Intent runRadio = new Intent(PythonAppActivity.this, runEisenRadio.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.startForegroundService(runRadio);  // else can not use startForeG...
        } else {
            startService(runRadio);  // what is the diff
        }
    }
}