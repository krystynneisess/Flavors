package com.sandbox.k.yogurtparkflavors;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import android.os.Handler;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

/**
 * Created by k on 5/29/17.
 */

public class FlavorJobService extends JobService {
    private static final String TAG = FlavorJobService.class.getSimpleName();
    private static Context context;

//    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        context = getApplicationContext();

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

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
//                int i = 0;
//                while (i++ < 3) {
//                    try {
//                        Log.i(TAG, "*** Service is RUNNING!  Loop #" + i);
//                        sleep(5);
//                    } catch (InterruptedException e) {
//                        Log.d(TAG, e.getMessage());
//                    }
//                }

                // Retrieve flavors!
                retrieveFlavors();
            }
        }).start();






        Log.i(TAG, "on start job: " + params.getJobId());
        jobFinished(params, false);

        // Return true as there's more work to be done with this job.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
//        sendMessage(MSG_COLOR_STOP, params.getJobId());
        Log.i(TAG, "*** Service has been FORCEFULLY STOPPED!");
        Log.i(TAG, "on stop job: " + params.getJobId());

        // Return false to drop the job; true to reschedule.
        return true;
    }

    /*************************
     * Flavor retrieval helpers
     *************************
     */
    private void retrieveFlavors() {
        // Generate Graph API Request
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/YogurtPark/posts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    // Handle request response
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();

                        String flavorData = jsonToFlavorsString(jsonObject);

//                        TextView flavorsList = (TextView) findViewById(R.id.flavors_list);
//                        flavorsList.setText(flavorData);

                        Log.d(TAG, "flavorData:  " + flavorData);
                    }
                });

        request.executeAsync();
    }

    /* Helper.  Extracts most recent post's message from the above GraphResponse's
     * JSONObject. */
    private String jsonToFlavorsString(JSONObject jsonObject) {
        String flavorsString = null;

        try {
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject newestPost = data.getJSONObject(0);
            flavorsString = newestPost.getString("message");

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return flavorsString;
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
