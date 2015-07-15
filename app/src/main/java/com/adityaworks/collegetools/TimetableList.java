package com.adityaworks.collegetools;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimetableList extends BaseActivity {

    public static final String DAY_KEY = "selected_date";
    private static final String LOG_TAG = TimetableList.class.getSimpleName();
    public static String selectedDay;
    public static TimetableAdapter timetableAdapter;
    public static ListView listView;

    public static ArrayList<Lecture> getTimetable() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.appContext);

        String defaultTimetable = "{\"1\":[{\"name\":\"No Timetable\",\"acro\":\"404\",\"faculty\":\"\",\"start-time\":\"\",\"end-time\":\"\"}]}";
        String timetableStr = sharedPref.getString("timetableStr", defaultTimetable);

        ArrayList<Lecture> timetable;
        try {
            timetable = getTimetableDataFromJson(timetableStr, selectedDay);
            return timetable;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Lecture> getTimetableDataFromJson(String timetableJsonStr, String day)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String LECTURE_NAME = "name";
        final String ACRONYM = "acro";
        final String FACULTY_NAME = "faculty";
        final String START_TIME = "start-time";
        final String END_TIME = "end-time";

        JSONObject timetableJson = new JSONObject(timetableJsonStr);
        JSONArray timetableArray = timetableJson.getJSONArray(day);

        ArrayList<Lecture> resultList = new ArrayList<>();
        for(int i = 0; i < timetableArray.length(); i++) {
            // Get the JSON object for each lecture
            JSONObject lectureDetails = timetableArray.getJSONObject(i);

            // Assigning data
            String name = lectureDetails.getString(LECTURE_NAME);
            String acro = lectureDetails.getString(ACRONYM);
            String faculty = lectureDetails.getString(FACULTY_NAME);
            String startTime = lectureDetails.getString(START_TIME);
            String endTime = lectureDetails.getString(END_TIME);

            // Add item for lunch
            if (i==4)
                resultList.add(new Lecture("Lunch", "", "", "12:30 pm", "01:40 pm"));

            resultList.add(new Lecture(name, acro, faculty, startTime, endTime));
        }
        return resultList;

    }

    private void updateTimetable() {
        TimetableUpdater updaterTask = new TimetableUpdater(this);
        updaterTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTimetable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            selectedDay = Integer.toString(getIntent().getIntExtra(DAY_KEY, 1));
        }

        listView = (ListView) findViewById(R.id.timetable_list);
        ArrayList<Lecture> timetable = getTimetable();
        timetableAdapter = new TimetableAdapter(
                this, R.layout.timetable_list,
                timetable
        );

        listView.setAdapter(timetableAdapter);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_timetable_list;
    }
}
