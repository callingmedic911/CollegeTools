package com.adityaworks.collegetools;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by callingmedic911 on 12/4/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class TimetableUpdater extends AsyncTask<Void, Void, Boolean> {

    final String TIMETABLE_BASE_URI =
            "http://collegetools.adityaworks.com";
    final String BRANCH_PATH = "cse";
    final String YEAR_PATH = "second";
    final String SECTION_PATH = "a";
    private final String LOG_TAG = TimetableUpdater.class.getSimpleName();
    private Context mContext;

    public TimetableUpdater (Context context){
        mContext = context;
    }

    URL getURL(String fileName) {
        Uri builtUri = Uri.parse(TIMETABLE_BASE_URI).buildUpon()
                .appendPath(YEAR_PATH)
                .appendPath(BRANCH_PATH)
                .appendPath(SECTION_PATH)
                .appendPath(fileName)
                .build();

        // Construct the URL for the private API at AdityaWorks at
        // http://collegetools.adityaworks.com/
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    String urlToString(URL url) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the data, there's no point in comparison.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        int cloudVersion;
        int localVersion = 0;
        final String fileName = "timetable.version";

        cloudVersion = Integer.parseInt(urlToString(getURL(fileName)));

        //If everything goes good, then we'll check current version with cloud's
        return cloudVersion > localVersion;

    }

    @Override
    public void onPostExecute (Boolean updating){

        final String fileName = "timetable.json";
        String timetableStr = urlToString(getURL(fileName));

        if(updating || timetableStr==null){

            ProgressDialog progress = new ProgressDialog(mContext);
            progress.setTitle("Downloading...");
            progress.setMessage("Downloading new timetable...");
            progress.show();

            if ( timetableStr != null ) {
                //TODO:Save to sharedPrefs > timetable_json and make local version same as of cloud version and reflect changes
            }

            progress.dismiss();
        }

    }

}
