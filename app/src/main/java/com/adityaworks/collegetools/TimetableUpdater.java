package com.adityaworks.collegetools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.adityaworks.collegetools.util.CloudConnect;
import com.adityaworks.collegetools.util.TimetableHelper;

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

        float cloudVersion, localVersion = TimetableHelper.getLocalVersion();
        final String fileName = "timetable.version";

        Log.v(LOG_TAG, "Local version is " + localVersion);

        try {
            Log.v(LOG_TAG, "BuiltURL " + CloudConnect.getURL(fileName).toString());
            Log.v(LOG_TAG, "Cloud version is " + CloudConnect.urlToString(CloudConnect.getURL(fileName)));
            cloudVersion = Float.parseFloat(CloudConnect.urlToString(CloudConnect.getURL(fileName)));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Error: Received null, stack trace : ", e);
            return false;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to download.", e);
            return false;
        }

        //If everything goes good, then we'll check current version with cloud's
        if (cloudVersion > localVersion) {
            boolean result;
            try {
                CloudConnect.syncTimetable();
                Log.v(LOG_TAG, "Sync Successful");
                return true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to download update.");
            }
        }
        return false;
    }

    @Override
    public void onPostExecute(Boolean updated) {

        if (updated) {
            ArrayList<Lecture> newTimetable = TimetableHelper.getTimetable(TimetableList.selectedDay);
            TimetableList.timetableAdapter = new TimetableAdapter(
                    mContext, R.layout.timetable_list,
                    newTimetable
            );
            TimetableList.listView.setAdapter(TimetableList.timetableAdapter);
            Log.v(LOG_TAG, "Notified DataSet Change.");
            Toast.makeText(
                    mContext,
                    "Boom! Timetable updated.",
                    Toast.LENGTH_LONG
            ).show();
        }

    }

}
