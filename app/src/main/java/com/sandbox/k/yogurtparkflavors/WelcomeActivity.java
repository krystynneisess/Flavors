package com.sandbox.k.yogurtparkflavors;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class WelcomeActivity extends AppCompatActivity {
    final String TAG_DEBUG = "TAG_DEBUG";

    // Facebook API globals
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    // Flavor service globals
    ComponentName mFlavorServiceComponent;
    int mJobId;
    JobScheduler mJobScheduler;
    boolean mServiceToggleValue;

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean("mServiceToggleValue", mServiceToggleValue);
//        Log.d(TAG_DEBUG, "***** Saved state!  toggleVal = " + mServiceToggleValue);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        mServiceToggleValue = savedInstanceState.getBoolean("mServiceToggleValue");
//        Log.d(TAG_DEBUG, "***** onRestoreInstanceState():  restored state!  toggleVal = " +
//                mServiceToggleValue);
//    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.service_toggle_key), mServiceToggleValue);
        editor.commit();
        Log.d(TAG_DEBUG, "***** Saved state!  toggleVal = " + mServiceToggleValue);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mServiceToggleValue = sharedPref.getBoolean(getString(R.string.service_toggle_key), false);

        ToggleButton serviceToggle = (ToggleButton) findViewById(R.id.service_toggle);
        serviceToggle.setChecked(mServiceToggleValue);

        Log.d(TAG_DEBUG, "***** Restored state!  toggleVal = " +
                mServiceToggleValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recover state
//        Log.d(TAG_DEBUG, "***** onCreate: about to restore state!");
//        if (savedInstanceState != null) {
//            mServiceToggleValue = savedInstanceState.getBoolean("mServiceToggleValue");
//            Log.d(TAG_DEBUG, "***** Restored state!  toggleVal = " + mServiceToggleValue);
//        } else {
//            mServiceToggleValue = false;
//            Log.d(TAG_DEBUG, "***** DID NOT restore state!  toggleVal = " + mServiceToggleValue);
//        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);

        // Get Views
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        final Button flavorsButton = (Button) findViewById(R.id.see_flavors_btn);
        final TextView debugTV = (TextView) findViewById(R.id.debug_vals);
        final ToggleButton serviceToggle = (ToggleButton) findViewById(R.id.service_toggle);
//        serviceToggle.setChecked(mServiceToggleValue);

        /*************************
         * Service initializations
         *************************
         */
        mJobId = 0;
        mFlavorServiceComponent = new ComponentName(this, FlavorJobService.class);
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        /*************************
         * Facebook login handling
         *************************
         */
        // Initialize app logging and CallbackManager
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();

        // Initialize AccessTokenTracker
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    // User has logged out.
                    flavorsButton.setVisibility(View.GONE);
                }
            }
        };

        // Register login callback
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String id = loginResult.getAccessToken().getUserId();
                String accessToken = loginResult.getAccessToken().getToken();

                debugTV.setText(String.format("id:  %s\naccessToken:  %s", id, accessToken));
                flavorsButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                debugTV.setText("Login cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                debugTV.setText("Login failed.");
            }
        });


        /*************************
         * Other UI handling
         *************************
         */
//        if (!serviceToggle.isChecked()) {
//            mJobScheduler.cancelAll();
//        }

        // Initalize service button state change listener
        serviceToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = serviceToggle.isChecked();
                mServiceToggleValue = isChecked;

                if (isChecked) {
                    // The toggle is enabled
                    Log.d(TAG_DEBUG, "*** Toggle is now ON!");
                    // Start service!
                    scheduleJob();
                    Log.d(TAG_DEBUG, "*** Service SCHEDULED!");
                } else {
                    // The toggle is disabled
                    Log.d(TAG_DEBUG, "*** Toggle is now OFF!");
                    // End services
                    // TODO:  don't use janky JobScheduler.cancelAll()
                    mJobScheduler.cancelAll();
                    Log.d(TAG_DEBUG, "*** Service SCHEDULED!");
                }
            }
        });
//        serviceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // The toggle is enabled
//                    Log.d(TAG_DEBUG, "*** Toggle is now ON!");
//                    // Start service!
//                    scheduleJob();
//                } else {
//                    // The toggle is disabled
//                    Log.d(TAG_DEBUG, "*** Toggle is now OFF!");
//                    // End services
//                    // TODO:  don't use janky JobScheduler.cancelAll()
////                    mJobScheduler.cancelAll();
//                }
//            }
//        });

        // Initialize flavors button OnClickListener
        flavorsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent seeFlavorsIntent = new Intent(WelcomeActivity.this, FlavorsActivity.class);
                startActivity(seeFlavorsIntent);
            }
        });

        // Reveal flavors button if logged in already
        if (AccessToken.getCurrentAccessToken() != null) {
            flavorsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*************************
     * Service Scheduling Helpers
     *************************
     */

    public void scheduleJob() {
        // Initialize builder
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, mFlavorServiceComponent);
        // TODO:  add timing/network requirements to builder
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(1000);

        // Schedule job
        mJobScheduler.schedule(builder.build());
    }


}
