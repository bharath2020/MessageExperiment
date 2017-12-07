package com.feathertouch.smsforwarder.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.feathertouch.smsforwarder.Util;

@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Scheduler extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        if ( !SMSForwarderService.isAlive ) {
            Util.sendNotification(this,"Start Job");
            Intent service = new Intent(getApplicationContext(), SMSForwarderService.class);
            service.putExtra(SMSForwarderService.COMMAND_KEY, SMSForwarderService.SMS_UPLOAD_RECENT_MESSAGES);
            getApplicationContext().startService(service);
            Util.scheduleJob(getApplicationContext()); // reschedule the job
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
