package com.adityaworks.collegetools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityaworks.collegetools.updater.CloudConnect;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by callingmedic911 on 12/4/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class TimetableUpdater extends AsyncTask<Void, Void, Boolean> {

    private final static String LOG_TAG = TimetableUpdater.class.getSimpleName();
    private Context mContext;

    public TimetableUpdater(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        int cloudVersion, localVersion;
        final String fileName = "timetable.version";

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.appContext);
        localVersion = sharedPref.getInt("localVersion", 0);

        try {
            Log.v(LOG_TAG, "BuiltURL " + CloudConnect.getURL(fileName).toString());
            Log.v(LOG_TAG, "Cloud version is " + CloudConnect.urlToString(CloudConnect.getURL(fileName)));
            cloudVersion = Integer.parseInt(CloudConnect.urlToString(CloudConnect.getURL(fileName)));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error: Received null, orginal error - ", e);
            return false;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return false;
        }

        //If everything goes good, then we'll check current version with cloud's
        if (cloudVersion > localVersion) {
            try {
                CloudConnect.syncTimetable();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to download update.");
            } finally {
                Log.v(LOG_TAG, "Sync Successful");
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPostExecute(Boolean updated) {

        if (updated) {
            ArrayList<Lecture> newTimetable = TimetableList.getTimetable();
            TimetableList.timetableAdapter = new TimetableAdapter(
                    mContext, R.layout.timetable_list,
                    newTimetable
            );
            TimetableList.listView.setAdapter(TimetableList.timetableAdapter);
            Log.v(LOG_TAG, "Notified DataSet Change.");
        }

    }

}
