package com.feathertouch.smsforwarder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.feathertouch.smsforwarder.database.SMSDbHelper;
import com.feathertouch.smsforwarder.model.SMSMessage;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_READ_SMS = 2;
    final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Log.wtf(TAG, "Fetching token");


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.wtf(TAG, "Refreshed token: " + refreshedToken);

        updateMessagesCount();

        Util.scheduleJob(this);



        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessagesCount();
    }

    void updateMessagesCount() {
        ArrayList<SMSMessage> messages = Util.recentMessages(this, 9999);
        TextView messageCountText = (TextView)findViewById(R.id.messageCount);
        messageCountText.setText("Total Messages: " + messages.size());
    }
}
