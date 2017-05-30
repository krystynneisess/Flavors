package com.sandbox.k.yogurtparkflavors;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import android.os.Handler;

import static java.lang.Thread.sleep;

/**
 * Created by k on 5/29/17.
 */

public class FlavorJobService extends JobService {
    private static final String TAG = FlavorJobService.class.getSimpleName();

//    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
//        return START_NOT_STICKY;
//    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        // The work that this service "does" is simply wait for a certain duration and finish
        // the job (on another thread).

//        sendMessage(MSG_COLOR_START, params.getJobId());
//
//        long duration = params.getExtras().getLong(WORK_DURATION_KEY);

        // Uses a handler to delay the execution of jobFinished().
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Service actions here!
//                sendMessage(MSG_COLOR_STOP, params.getJobId());
//                jobFinished(params, false);
                int i = 0;
                while (i++ < 10) {
                    try {
                        Log.i(TAG, "*** Service is RUNNING!  Loop #" + i);
                        sleep(3);
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }, 0);
        Log.i(TAG, "on start job: " + params.getJobId());

        // Return true as there's more work to be done with this job.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
//        sendMessage(MSG_COLOR_STOP, params.getJobId());
        Log.i(TAG, "*** Service has been FORCEFULLY STOPPED!");
        Log.i(TAG, "on stop job: " + params.getJobId());

        // Return false to drop the job.
        return false;
    }



//    private void sendMessage(int messageID, @Nullable Object params) {
//        // If this service is launched by the JobScheduler, there's no callback Messenger. It
//        // only exists when the MainActivity calls startService() with the callback in the Intent.
//        if (mActivityMessenger == null) {
//            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
//            return;
//        }
//        Message m = Message.obtain();
//        m.what = messageID;
//        m.obj = params;
//        try {
//            mActivityMessenger.send(m);
//        } catch (RemoteException e) {
//            Log.e(TAG, "Error passing service object back to activity.");
//        }
//    }
}
