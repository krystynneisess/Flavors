package com.sandbox.k.yogurtparkflavors;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class FlavorsActivity extends AppCompatActivity {
    final String TAG_DEBUG = "TAG_DEBUG";
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flavors);
        context = getApplicationContext();

        TextView flavorsList = (TextView) findViewById(R.id.flavors_list);

        String retrievedFlavors = retrieveFlavors();

        if (retrievedFlavors == null) {
            retrievedFlavors = "No flavors found.";
        }

        flavorsList.setText(retrievedFlavors);
    }

    String retrieveFlavors() {
        String flavorData;
        String url = "https://m.facebook.com/YogurtPark/";
//        String url = "https://www.facebook.com/YogurtPark/";

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
            String flavorData = "";

            try {
                flavorData = getHttpResponse(urls[0]);

            } catch (IOException e) {
                Log.d(TAG_DEBUG, e.getMessage());
                flavorData = null;
            }

            return flavorData;
        }

        private String getHttpResponse(String urlStr) throws IOException{
            URL url = new URL(urlStr);
            HttpsURLConnection connection = null;
            InputStream stream = null;
            String streamAsString = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();
                // Check response code for success.
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                // Convert to string
                streamAsString = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();

            } finally {
                // Disconnect HTTPS connection.
                if (connection != null) {
                    connection.disconnect();
                }

                if (stream != null) {
                    stream.close();
                }

            }

//            if (stream != null) {
//                Log.d(TAG_DEBUG, "READING STREAM:  " + readStream(stream, 500));
//            }

            return streamAsString;
        }






        /*  Moooooaaaarr helpers. Courtesy of Android docu. */

        /* Returns up to maxLength characters of stream as a String. */
        private String readStream(InputStream stream, int maxLength) throws IOException {
            String result = null;
            // Read InputStream using the UTF-8 charset.
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            // Create temporary buffer to hold Stream data with specified max length.
            char[] buffer = new char[maxLength];
            // Populate temporary buffer with Stream data.
            int numChars = 0;
            int readSize = 0;
            while (numChars < maxLength && readSize != -1) {
                numChars += readSize;
                readSize = reader.read(buffer, numChars, buffer.length - numChars);
            }
            if (numChars != -1) {
                // The stream was not empty.
                // Create String that is actual length of response body if actual length was less than
                // max length.
                numChars = Math.min(numChars, maxLength);
                result = new String(buffer, 0, numChars);
            }
            return result;
        }

        /* Checks if external storage is available for read and write */
        public boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

        /* Checks if external storage is available to at least read */
        public boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }
    }
}
