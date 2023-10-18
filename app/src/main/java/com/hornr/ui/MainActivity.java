/* Android Studio BumbleBee is last version with all Java, next version Giraffe is all Kotlin.
 * Chaquopy version 14 is not usable with kotlin. It needs groovy for the ndk abi block, else error.
 *
 * Android UI, modified to send input to Python
 * https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/firstapp/building-ui.html
 * Python embed
 * https://chaquo.com/chaquopy/doc/current/android.html#android-startup
 *
 * Test Python shared library works, add folder with py files at runtime
 * https://github.com/chaquo/chaquopy/issues/593
 * *
 * auto uninstall app before new install:
 * Select Edit [app] Configurations from the dropwdown in navigation pane and scroll to bottom
 * Find the section Before Launch: Gradle task, Gradle-aware Make
 * Click + and choose Run Gradle task
 * In the Gradle project field, select the root of your project
 * In the Tasks field enter :app:uninstallAll   :app:uninstallAll
 *
 */
package com.hornr.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Color;
import android.widget.EditText;

import com.hornr.PythonApp.AssetExtractor;
import com.hornr.PythonApp.PythonAppActivity;
import com.hornr.BasicModules.ForeGroundService;
import com.hornr.BasicModules.DisplayMessageActivity;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.hornr.PythonModule.PythonModuleActivity;
import com.hornr.R;

// default class of an empty app
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "[MainActivity]";

    boolean isBtnForeGroundServicePressed = false;

    // Application.onCreate()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hornr.R.layout.activity_main);
    }

    /** Send message button */
    public void sendMessage(View view) {
        // response to writing text and button press, custom activity DisplayMessageActivity.java
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.txt_box_edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    public final static String EXTRA_MESSAGE = "com.hornr.MESSAGE";

    /** Python Module button */
    public void callPythonModule(View view) {
        Intent intent = new Intent(this, PythonModuleActivity.class);
        startActivity(intent);
    }

    /** Python App button */
    public void pythonApp(View View) {
        // ask for permission in activity in Main Thread, in service it is a pain
        Intent app = new Intent (MainActivity.this, PythonAppActivity.class);
        startActivity(app);
        // move default browser to front, top of view stack
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:5050"));
        browser.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(browser);
        }, 5000);  // don't hide a permission request dialog
    }

    /** Start ForeGroundService button */
    public void startForeGroundService(View view) {
        Intent foreGroundService = new Intent(this, ForeGroundService.class);
        Button button = findViewById(R.id.btnStartForegroundService);

        if (!isBtnForeGroundServicePressed) {
            button.setBackgroundColor(Color.GREEN);
            button.setTextColor(Color.BLACK);
            button.setText("Stop \n ForeGroundService");
            isBtnForeGroundServicePressed = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.startForegroundService(foreGroundService);  // else can not use startForeG...
            } else {
                startService(foreGroundService);  // what is the diff
            }
        } else {
            button.setBackgroundColor(0x1B0602);
            button.setTextColor(Color.WHITE);
            button.setText("Start \n ForeGroundService");
            isBtnForeGroundServicePressed = false;
            this.stopService(foreGroundService);
        }
    }
}
