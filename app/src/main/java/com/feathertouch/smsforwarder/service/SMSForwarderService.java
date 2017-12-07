package com.feathertouch.smsforwarder.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.feathertouch.smsforwarder.SMSReaderWriter;
import com.feathertouch.smsforwarder.Util;
import com.feathertouch.smsforwarder.database.SMSDbHelper;
import com.feathertouch.smsforwarder.model.SMSMessage;
import com.feathertouch.smsforwarder.network.SMSNetworkUploader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bbooshan on 8/20/16.
 */
public class SMSForwarderService extends Service {

    public static final String SMS_RECEIVED_COMMAND = "sms_received";
    public static final String SMS_UPLOAD_ALL_COMMAND = "upload_messages";
    public static final String SMS_UPLOAD_RECENT_MESSAGES = "upload_recent";
    public static final String SMS_MESSAGES_JSON_KEY = "messages_json";
    public static final String COMMAND_KEY = "command";
    private static final String TAG = "SMSForwarderService";

    private  SMSReaderWriter smsReaderWriter;
    private SMSNetworkUploader smsUploader;

    static boolean isAlive = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        smsReaderWriter = new SMSReaderWriter(new SMSDbHelper(this));
        smsUploader = new SMSNetworkUploader(this);
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
isAlive = true;
        if ( intent != null ){

            String command = intent.getStringExtra(COMMAND_KEY);
            switch (command) {
                case SMS_RECEIVED_COMMAND:
                {
                    String messagesJSONString = intent.getStringExtra(SMS_MESSAGES_JSON_KEY);
                    try{
                        JSONArray smsArray = new JSONArray(messagesJSONString);
                        if (smsArray.length() > 0) {
                            SMSMessage[] messages = new SMSMessage[smsArray.length()];
                            for( int index =0 ; index < smsArray.length(); index++ ){
                                try{
                                    JSONObject sms = smsArray.getJSONObject(index);
                                    SMSMessage msg = new SMSMessage(sms);
                                    messages[index] = msg;
                                }
                                catch (JSONException e){

                                }
                            }
                            uploadSMS(messages);
                        }
                    }
                    catch ( JSONException e){

                    }
                }
                break;

                case SMS_UPLOAD_ALL_COMMAND: {
                    uploadSMS(null);
                }
                    break;
                case SMS_UPLOAD_RECENT_MESSAGES: {
                    uploadRecent();
                }
            }
        }


        return START_STICKY;
    }

    private void saveSMS(SMSMessage[] messages){
        smsReaderWriter.addMessages(messages);
    }

    private void uploadSMS(final SMSMessage[] messages){

        if (messages != null) {
            saveSMS(messages);
        }

        final SMSMessage[] curMessages = smsReaderWriter.allMessages();
        if (curMessages == null) {
            Log.wtf(TAG,"No messages to upload");
            stopSelf();
            return;
        }

        Log.wtf(TAG, "Uploading " + curMessages.length + " messages");

        JSONArray smsArray = new JSONArray();
        for( int index=0 ; index < curMessages.length; index++ ){
            SMSMessage message = curMessages[index];
            JSONObject object = message.toJSONObject();
            if( object != null ){
                smsArray.put(object);
            }
        }

        if ( smsArray.length() > 0 ){
            Log.wtf(TAG, smsArray.toString());
            smsUploader.uploadMessages(smsArray, new SMSNetworkUploader.SMSNetworkCallback() {
                @Override
                public void onSuccess() {
                    smsReaderWriter.deleteMessages(curMessages);
                    Toast.makeText(SMSForwarderService.this,"Successfully Upload SMS", Toast.LENGTH_SHORT).show();

                    SMSMessage[] newMessages = smsReaderWriter.allMessages();
                    if (newMessages != null && newMessages.length > 0 ){
                        uploadSMS(null);
                    }
                    else{
                        stopSelf();
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(SMSForwarderService.this,"Failed Upload SMS", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            });
        }
    }

    private void uploadRecent() {
        Util.sendNotification(this,"Upload recent Job");

        final String lastFetchedIdentifier = Util.recentSMSIdentifier(this);
        ArrayList<SMSMessage> messages = Util.recentMessages(this, 10);
        JSONArray smsArray = new JSONArray();
        for( int index=0 ; index < messages.size(); index++ ){
            SMSMessage message = messages.get(index);
            JSONObject object = message.toJSONObject();
            if( object != null ){
                smsArray.put(object);
            }
        }

        if ( smsArray.length() > 0 ){
            smsUploader.uploadMessages(smsArray, new SMSNetworkUploader.SMSNetworkCallback() {
                @Override
                public void onSuccess() {
                    Util.recordLastFetchedIdentifier(SMSForwarderService.this, lastFetchedIdentifier);
                    Util.sendNotification(SMSForwarderService.this,"Successfully Upload Service");

                    stopSelf();
                }

                @Override
                public void onFailure() {
                    Util.sendNotification(SMSForwarderService.this,"FAiled Upload Service");

                    stopSelf();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        super.onDestroy();

    }
}
