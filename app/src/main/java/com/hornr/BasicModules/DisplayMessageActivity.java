/* Simple, write a msg and push send button. Open TextView and display msg.
 *
 * MainActivity intercepts BUTTON press and calls *method sendMessage* in MainActivity
 * /res/layout/activity_main.xml  edit text box and send msg BUTTON
 * /res/layout/activity_display_message.xml  show title
 * /res/layout/strings.xml  str title for TextView and BUTTON text
 * AndroidManifest.xml register this activity with parent activity (... meta-data ...)
 * review here
 * https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/firstapp/starting-activity.html
 */
package com.hornr.BasicModules;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;
import android.annotation.SuppressLint;

import com.hornr.ui.MainActivity;

public class DisplayMessageActivity extends AppCompatActivity {
    private static final String LOG_TAG = "[DisplayMessageActivity]";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        // Set the text view as the activity layout
        setContentView(textView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar shows the application icon on the left, followed by the activity title. I
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}