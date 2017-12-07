package com.feathertouch.smsforwarder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.feathertouch.smsforwarder.service.SMSForwarderService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageReceiveService extends FirebaseMessagingService {
    final String TAG = "MessageReceiveService";
    public MessageReceiveService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       Util.sendNotification(this,"FCM Message received");
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());
            startMessageUploadService();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void startMessageUploadService() {
        Intent uploadAllSMS = new Intent(this, SMSForwarderService.class);
        uploadAllSMS.putExtra(SMSForwarderService.COMMAND_KEY, SMSForwarderService.SMS_UPLOAD_ALL_COMMAND);
        this.startService(uploadAllSMS);
    }


}
