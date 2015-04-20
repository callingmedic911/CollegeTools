package com.adityaworks.collegetools;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class TimetableList extends BaseActivity {

    private final String LOG_TAG = TimetableList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dummyTimetableStr = "{\"1\":[{\"name\":\"Computer Graphics\",\"acro\":\"CG\",\"faculty\":\"Ms. Surbhi\",\"start-time\":\"09:00 am\",\"end-time\":\"09:50 am\"},{\"name\":\"Theory of Automata and Formal Languages\",\"acro\":\"TAFL\",\"faculty\":\"Ms. Megha\",\"start-time\":\"09:50 am\",\"end-time\":\"10:40 am\"},{\"name\":\"Introduction to Microprocessor\",\"acro\":\"MP\",\"faculty\":\"Ms. Sweta\",\"start-time\":\"10:50 am\",\"end-time\":\"11:40 am\"},{\"name\":\"Operating System\",\"acro\":\"OS\",\"faculty\":\"Mr. Amit Kumar\",\"start-time\":\"11:40 am\",\"end-time\":\"12:30 pm\"},{\"name\":\"MP Lab (T1) / CG Lab T2\",\"acro\":\"LAB\",\"faculty\":\"Ms. Sweta / Ms. Surbhi\",\"start-time\":\"01:40 pm\",\"end-time\":\"02:30 pm\"},{\"name\":\"MP Lab (T1) / CG Lab T2\",\"acro\":\"LAB\",\"faculty\":\"Ms. Sweta / Ms. Surbhi\",\"start-time\":\"02:30 pm\",\"end-time\":\"03:20 pm\"},{\"name\":\"Cyber Security\",\"acro\":\"CS\",\"faculty\":\"Mr. Abhishek\",\"start-time\":\"03:20 pm\",\"end-time\":\"04:10 pm\"},{\"name\":\"Industrial Sociology\",\"acro\":\"IS\",\"faculty\":\"Mr. Prashant\",\"start-time\":\"04:10 pm\",\"end-time\":\"05:00 pm\"}]}";

        ListView listView = (ListView) findViewById(R.id.timetable_list);
        ArrayList<Lecture> dummyTimetable = null;
        try {
            dummyTimetable = getTimetableDataFromJson(dummyTimetableStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        listView.setAdapter(
                new TimetableAdapter(
                        this, R.layout.timetable_list,
                        dummyTimetable
                ));
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

    private Date getReadableDateString(String dateStr) {
        return null;
    }

    private ArrayList<Lecture> getTimetableDataFromJson(String timetableJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String CURRENT_DAY = "1";
        final String LECTURE_NAME = "name";
        final String ACRONYM = "acro";
        final String FACULTY_NAME = "faculty";
        final String START_TIME = "start-time";
        final String END_TIME = "end-time";

        JSONObject timetableJson = new JSONObject(timetableJsonStr);
        JSONArray timetableArray = timetableJson.getJSONArray(CURRENT_DAY);

        ArrayList<Lecture> resultList = new ArrayList<Lecture>();
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_timetable_list;
    }
}
