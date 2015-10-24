package com.adityaworks.collegetools.util;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityaworks.collegetools.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by callingmedic911 on 14/7/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class CloudConnect {
    final static String TIMETABLE_BASE_URI =
            "http://collegetools.adityaworks.com";
    final static String BRANCH_PATH = "cse";
    final static String YEAR_PATH = "third";
    final static String SECTION_PATH = "a";
    private final static String LOG_TAG = CloudConnect.class.getSimpleName();

    public static URL getURL(String fileName) {
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

    public static String urlToString(URL url) throws IOException {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            // Create the request to server, and open the connection
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

    public static void syncTimetable() throws IOException {
        String cloudVersion;
        String newTimetableJSON;
        String timetable = "timetable.json";
        String version = "timetable.version";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.appContext);
        SharedPreferences.Editor editor = sharedPref.edit();

        cloudVersion = urlToString(getURL(version));
        newTimetableJSON = urlToString(getURL(timetable));

        if ((cloudVersion != null) && (newTimetableJSON != null)) {
            editor.putString("timetableStr", newTimetableJSON);
            editor.putFloat("localVersion", Float.parseFloat(cloudVersion));
        }

        editor.apply();

    }
}
