package com.sandbox.k.yogurtparkflavors;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);

        // Get Views
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        final Button flavorsButton = (Button) findViewById(R.id.see_flavors_btn);
        final TextView debugTV = (TextView) findViewById(R.id.debug_vals);


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
}
