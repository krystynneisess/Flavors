package com.sandbox.k.yogurtparkflavors;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FlavorsActivity extends AppCompatActivity {
    final String TAG_DEBUG = "TAG_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flavors);

        TextView flavorsList = (TextView) findViewById(R.id.flavors_list);

        String retrievedFlavors = retrieveFlavors();

        if (retrievedFlavors == null) {
            retrievedFlavors = "No flavors found.";
        }

        flavorsList.setText(retrievedFlavors);
    }

    String retrieveFlavors() {
        String flavorData;
//        String url = "https://www.google.com";
        String url = "https://www.facebook.com/YogurtPark/";

        try {
            flavorData = new FlavorRetrievalTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG_DEBUG, e.getMessage());
            flavorData = null;
        }


        return flavorData;
    }

    private class FlavorRetrievalTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            // do retrieval
            String flavorData;

            try {
                Document doc = Jsoup.connect(urls[0]).get();
                flavorData = doc.title();
            } catch (IOException e) {
                Log.d(TAG_DEBUG, e.getMessage());
                flavorData = null;
            }

            return flavorData;
        }
    }

}
