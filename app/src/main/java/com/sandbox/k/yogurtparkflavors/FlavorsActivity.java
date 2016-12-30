package com.sandbox.k.yogurtparkflavors;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
                Document doc = getDocViaParse(urls[0]);
                Element todaysPostElem = doc
//                        .select("div._5rgt._5nk5._5msi")
//                        .select("div:contains(Flavors)")
                        .select("div#viewport")
                        .first();

//                Elements debugElems = doc.select("div._uag").first().children();

//                flavorData = String.format("id:\n%s\nclass:\n%s",
//                        todaysPostElem.id(),
//                        todaysPostElem.className());
                flavorData = todaysPostElem.text();
//                for (Element e : debugElems) {
//                    flavorData += e.className() + "*";
//                }

//                flavorData = todaysPostElem.id();
            } catch (IOException e) {
                Log.d(TAG_DEBUG, e.getMessage());
                flavorData = null;
            }

            return flavorData;
        }

        private InputStream getHtmlInputStream(String urlStr) throws IOException{
            URL url = new URL(urlStr);
            InputStream stream = null;
            HttpsURLConnection connection = null;
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

            } finally {
                // Disconnect HTTPS connection.  *Don't* close stream yet.
                if (connection != null) {
                    connection.disconnect();
                }

            }

//            if (stream != null) {
//                Log.d(TAG_DEBUG, "READING STREAM:  " + readStream(stream, 500));
//            }

            return stream;
        }


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


        private void updateHtmlFile(String filePath, String urlStr) {
            // TODO:  Fetch html page via http query.  Update local copy.
            InputStream in = null;
            FileOutputStream out = null;
            HttpsURLConnection connection = null;

            try {
                // get http request as an InputStream
                URL url = new URL(urlStr);

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
                in = connection.getInputStream();

                if (in != null) {
                    Log.d(TAG_DEBUG, "READING STREAM:  " + readStream(in, 500));
                }


//                in = getHtmlInputStream(url);

                // delete file if it already exists
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                }

                // create new instance of file
                f.createNewFile();

                // write to new instance of file
                out = new FileOutputStream(filePath);

                int bytesRead = 0;
                byte[] buff = new byte[1024];

                while ((bytesRead = in.read(buff)) != -1) {
                    out.write(buff, 0, bytesRead);
                }
            } catch(IOException e) {
                Log.d(TAG_DEBUG, e.getMessage());
            } finally {
                // Disconnect HTTPS connection.  *Don't* close stream yet.
                if (connection != null) {
                    connection.disconnect();
                }
            }


        }

        private File getHtmlFile(String fileName, String url) throws IOException {
            String outFilePath = "";

            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                File externalFilesDir = context.getExternalFilesDir(null);
                if (externalFilesDir != null) {
                    outFilePath = externalFilesDir.getPath();
                }
            } else {
                Log.d(TAG_DEBUG, "*** External Storage Unavailable!");
                outFilePath = context.getFilesDir().getPath();
            }
            outFilePath +=  ("/" + fileName);

            Log.d(TAG_DEBUG, "*** outFilePath:  " + outFilePath);
            updateHtmlFile(outFilePath, url);

            return new File(outFilePath);
        }

        private Document getDocViaConnect(String url) throws IOException {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Linux; Android 6.0; LG-H810 Build/MRA58K) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.85 " +
                            "Mobile Safari/537.36")
                    .referrer("https://www.google.com")
                    .get();
        }

        private Document getDocViaParse(String url) throws IOException {
            File inputFile = getHtmlFile("m-yogurt-park.html", url);

            return Jsoup.parse(inputFile, "UTF-8", url);
        }




        /*  Moooooaaaarr helpers. Courtesy of Android docu. */

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
