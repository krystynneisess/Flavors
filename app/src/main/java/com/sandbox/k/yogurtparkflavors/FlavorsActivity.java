package com.sandbox.k.yogurtparkflavors;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

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
                        .select("div._5rgt._5nk5._5msi")
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

        private void updateHtmlAssetFile(String url) {
            // TODO:  Fetch html page via http query.  Update local copy.
        }

        private File getHtmlAssetFile(String assetName, String url) throws IOException {
            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open(assetName);
            String outFilePath = context.getFilesDir().getPath() + "/html.tmp";
            FileOutputStream out = new FileOutputStream(outFilePath);

            int bytesRead = 0;
            byte[] buff = new byte[1024];

            while ((bytesRead = in.read(buff)) != -1) {
                out.write(buff, 0, bytesRead);
            }
            in.close();
            out.close();

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
            File inputFile = getHtmlAssetFile("m-yogurt-park.html", url);

            return Jsoup.parse(inputFile, "UTF-8", url);
        }
    }

}
