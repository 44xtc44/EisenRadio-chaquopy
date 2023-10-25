/*
 */
package com.hornr.PythonModule;
import com.hornr.R;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONException;

import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PythonModuleActivity extends AppCompatActivity {
// Execute the Python code, better call it in a Java thread to not block Android UI
    private static final String LOG_TAG = "[PythonModuleActivity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default method created if activity was implemented.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_module);  // /res/layout/activity_python_module.xml

        // Get the TextView to show the result.
        TextView textView = findViewById(R.id.txtViewPythonModule);
        // Upscale the font size in the view.
        textView.setTextSize(24);

        // Auto startup; put in AndroidManifest.xml, android:name="com.chaquo.python.android.PyApplication"
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        try {
            doTask();  // thread
        } catch (IOException | ExecutionException | InterruptedException e) {
            StringBuilder errMsg = new StringBuilder();
            errMsg.append("RuntimeException in ").append(LOG_TAG).append(e);
            textView.setText(errMsg);
        }
    }
    private void doTask() throws IOException, ExecutionException, InterruptedException {

        final String[] msg = new String[1];
        StringBuilder strBuilder = new StringBuilder();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());  // UI comm

        executorService.execute(() -> {

            // Obtain the python instance, https://chaquo.com/chaquopy/doc/current/java/com/chaquo/python/Python.html
            Python py = Python.getInstance();
            // Obtain the fib.py module
            PyObject mod = py.getModule("fib");

            // Call Python module. Caller fun collects all findings and send as JSON.
            PyObject jsonPy = mod.callAttr("findings_print", "Results: ");
            Log.i(LOG_TAG, jsonPy.toString());
            try {
                JSONObject jsonJava = new JSONObject(jsonPy.toString());
                strBuilder.append(jsonJava.get("prefix")).append("\n");
                strBuilder.append("Fun: ").append(jsonJava.get("fibonacci_list")).append("\n");
                strBuilder.append("PyPi: ").append(jsonJava.get("pypi_pkg_lst")).append("\n");
                msg[0] = strBuilder.toString();

            }catch (JSONException e){
                Log.d(LOG_TAG, e.toString());
                msg[0] = "JSONException in " + LOG_TAG;  // method exception caught in "onCreate"
            }
            handler.post(() -> {
                // update Android UI
                Toast.makeText(PythonModuleActivity.this, msg[0], Toast.LENGTH_SHORT).show();
                TextView textView = findViewById(R.id.txtViewPythonModule);
                textView.setText(msg[0]);
            });
        });
    }
}