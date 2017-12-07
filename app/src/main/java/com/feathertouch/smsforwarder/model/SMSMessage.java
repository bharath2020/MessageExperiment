package com.feathertouch.smsforwarder.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bbooshan on 8/20/16.
 */
public class SMSMessage {

    public static final String NUMBER_KEY = "number";
    public static final String TEXT_KEY = "text";
    public static final String TIMESTAMP_KEY = "timestamp";

    public String number;
    public String text;
    public Long uniqueId;
    public long timestampInMillis = -1;


    public SMSMessage(JSONObject msgObj) throws JSONException {
        this.number = msgObj.getString(NUMBER_KEY);
        this.text = msgObj.getString(TEXT_KEY);
        this.timestampInMillis = msgObj.getLong(TIMESTAMP_KEY);

        this.number = this.number != null ? this.number : "";
        this.text = this.text != null ? this.text : "";
        this.uniqueId = -1L;

        Log.wtf("SMS", "n: " + number + ", t: " + text);

    }

    public SMSMessage(String num, String message, Long uniqueId){
        this.number = num;
        this.text = message;
        this.uniqueId = uniqueId;
    }

    public SMSMessage(String num, String message, Long uniqueId, Long timestamp) {
        this.number = num;
        this.text = message;
        this.uniqueId = uniqueId;
        this.timestampInMillis = timestamp;
    }

    @Override
    public String toString() {
        return number + " " + text;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try{
            object.put(NUMBER_KEY,number);
            object.put(TEXT_KEY,text);
            object.put(TIMESTAMP_KEY,timestampInMillis);
        }
        catch (JSONException e){

        }
        return  object;
    }
}
