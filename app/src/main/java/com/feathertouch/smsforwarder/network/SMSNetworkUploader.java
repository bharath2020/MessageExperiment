package com.feathertouch.smsforwarder.network;

import android.content.Context;
import android.os.Handler;

import com.feathertouch.smsforwarder.model.SMSMessage;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bbooshan on 8/21/16.
 */
public class SMSNetworkUploader {

    private Context context;
    private OkHttpClient client;

    public interface SMSNetworkCallback {
        void onSuccess();
        void onFailure();
    }

    public SMSNetworkUploader(Context context) {
        this.context = context;
        client = new OkHttpClient();
    }

    public void uploadMessages(JSONArray smsMessages, final SMSNetworkCallback callback){

       RequestBody body =  RequestBody.create(MediaType.parse("application/json"),smsMessages.toString());

            Request request = new Request.Builder()
                    .url("http://short-message-forward.herokuapp.com/messages/upload")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                Handler mainHandler = new Handler(context.getMainLooper());
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    response.close();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    });

                }
            });

    }
}
