package com.feathertouch.smsforwarder;


import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.feathertouch.smsforwarder.model.SMSMessage;
import com.feathertouch.smsforwarder.service.Scheduler;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, Scheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setPeriodic(5 * 60 * 1000);
        builder.setPersisted(true);

        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static ArrayList<SMSMessage> recentMessages(Context context, int maxCount) {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);

        Boolean result = cursor.moveToFirst();
        ArrayList<SMSMessage> messages = new ArrayList<>();

        SharedPreferences pref = context.getSharedPreferences("com.feathertouch.recent_messages",MODE_PRIVATE);
        String lastFetchedId = pref.getString("last_fetched_identifier", "");

        while  (result && messages.size() <= maxCount)
        {
            String identifier = cursor.getString(0);
            String address = cursor.getString(1);
            String body = cursor.getString(3);
            String timestampString = cursor.getString(2);
            Long timestamp = Long.parseLong(timestampString);
            result = false;
            if ( !lastFetchedId.equals(identifier) && timestamp != null) {
                SMSMessage message = new SMSMessage(address, body, 0L, timestamp);
                messages.add(message);
                result = cursor.moveToNext();
            }
        }
        return  messages;
    }

    public  static String recentSMSIdentifier(Context context) {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);
        if ( cursor.moveToFirst() ) {
            return cursor.getString(0);
        }
        return "";
    }

    public static  void recordLastFetchedIdentifier(Context context, String identifier) {
        SharedPreferences pref = context.getSharedPreferences("com.feathertouch.recent_messages",MODE_PRIVATE);
        pref.edit().putString("last_fetched_identifier", identifier).commit();
    }

    public static void sendNotification(Context context, String message) {

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(context);
//
//        mBuilder.setContentTitle(message);
//        mBuilder.setSmallIcon(R.drawable.notification_icon);
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        mBuilder.setSound(uri);
//        NotificationManager mNotificationManager =
//
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(001, mBuilder.build());
    }
}