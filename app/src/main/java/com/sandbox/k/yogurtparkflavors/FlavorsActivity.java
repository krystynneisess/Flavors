package com.sandbox.k.yogurtparkflavors;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlavorsActivity extends AppCompatActivity {
    final String TAG_DEBUG = "TAG_DEBUG";
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_flavors);
        context = getApplicationContext();

        retrieveFlavors();
    }

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

                        TextView flavorsList = (TextView) findViewById(R.id.flavors_list);
                        flavorsList.setText(flavorData);

                        Log.d(TAG_DEBUG, "flavorData:  " + flavorData);
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
            Log.d(TAG_DEBUG, e.getMessage());
        }

        return flavorsString;
    }

}
