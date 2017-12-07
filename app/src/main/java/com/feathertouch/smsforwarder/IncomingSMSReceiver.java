package com.feathertouch.smsforwarder;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.feathertouch.smsforwarder.model.SMSMessage;
import com.feathertouch.smsforwarder.service.SMSForwarderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bbooshan on 8/20/16.
 */
public class IncomingSMSReceiver  extends BroadcastReceiver{

    public String TAG = "DEBUG";




    @Override
    public void onReceive(Context context, Intent intent) {



        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();




        String str = "";
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");
            JSONArray smsArray = new JSONArray();

            // For every SMS message received
            for (int i=0; i < pdus.length; i++) {
                // Convert Object array
                SmsMessage sms = null;

                if (currentapiVersion >= 23 ){
                    // Do something for lollipop and above versions
                    sms = getSMS_MVersion((byte[]) pdus[i],format);
                } else{
                    // do something for phones running an SDK before lollipop
                    sms = getSMS_KVersion((byte[]) pdus[i]);
                }

                JSONObject smsObject = new JSONObject();

                try {
                    smsObject.put(SMSMessage.NUMBER_KEY, sms.getOriginatingAddress() );
                    smsObject.put(SMSMessage.TEXT_KEY, sms.getMessageBody().toString());
                    smsObject.put(SMSMessage.TIMESTAMP_KEY, sms.getTimestampMillis());
                    smsArray.put(smsObject);
                }
                catch (JSONException e){

                }
            }

            if( smsArray.length() > 0 ){
                String serializedString = smsArray.toString();
                Intent smsReceivedIntent = new Intent(context, SMSForwarderService.class);
                smsReceivedIntent.putExtra(SMSForwarderService.SMS_MESSAGES_JSON_KEY, serializedString);
                smsReceivedIntent.putExtra(SMSForwarderService.COMMAND_KEY, SMSForwarderService.SMS_RECEIVED_COMMAND);
                context.startService(smsReceivedIntent);
            }


        } else {
            Log.e(TAG, "Cannot read message");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private SmsMessage getSMS_MVersion(byte[] pdu, String format) {
        return  SmsMessage.createFromPdu(pdu, format);
    }

    private SmsMessage getSMS_KVersion(byte[] pdu ) {
        return  SmsMessage.createFromPdu(pdu);
    }
}
