package com.adityaworks.collegetools.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityaworks.collegetools.BaseActivity;
import com.adityaworks.collegetools.Lecture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by callingmedic911 on 04-Sep-15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All rights reserved.
 */
public class TimetableHelper {

    private static final String LOG_TAG = TimetableHelper.class.getSimpleName();
    public static Calendar calendar = Calendar.getInstance();
    private static SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.appContext);

    public static ArrayList<Lecture> getTimetable(String selectedDay) {
        ArrayList<Lecture> timetable;
        String getForDay;
        float localVersion = getLocalVersion();

        Log.v(LOG_TAG, "Local Version " + localVersion);
        getForDay = (localVersion == 0) ? "1" : selectedDay;
        Log.v(LOG_TAG, "Selected day " + getForDay);

        try {
            timetable = getTimetableDataFromJson(getTimetableStr(), getForDay);
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
        for (int i = 0; i < timetableArray.length(); i++) {
            // Get the JSON object for each lecture
            JSONObject lectureDetails = timetableArray.getJSONObject(i);

            // Assigning data
            String name = lectureDetails.getString(LECTURE_NAME);
            String acro = lectureDetails.getString(ACRONYM);
            String faculty = lectureDetails.getString(FACULTY_NAME);
            String startTime = lectureDetails.getString(START_TIME);
            String endTime = lectureDetails.getString(END_TIME);

            // Add item for lunch
            if (i == 4)
                resultList.add(new Lecture("Lunch", "", "", "12:30 pm", "01:40 pm"));

            resultList.add(new Lecture(name, acro, faculty, startTime, endTime));
        }
        return resultList;

    }

    public static Lecture getSubjectDetailAt(int lectureIndex, String day) throws JSONException {

        final String LECTURE_NAME = "name";
        final String ACRONYM = "acro";
        final String FACULTY_NAME = "faculty";
        final String START_TIME = "start-time";
        final String END_TIME = "end-time";

        JSONObject timetableJson = new JSONObject(getTimetableStr());
        JSONArray timetableArray = timetableJson.getJSONArray(day);
        JSONObject lectureDetails = timetableArray.getJSONObject(lectureIndex);

        // Assigning data
        String name = lectureDetails.getString(LECTURE_NAME);
        String acro = lectureDetails.getString(ACRONYM);
        String faculty = lectureDetails.getString(FACULTY_NAME);
        String startTime = lectureDetails.getString(START_TIME);
        String endTime = lectureDetails.getString(END_TIME);

        return new Lecture(name, acro, faculty, startTime, endTime);

    }

    public static int getLectureIndex() {
        int lectureIndex = 0;
        try {

            if (checkTimeIsBefore("09:00"))
                lectureIndex = 0;
            else if (checkTimeIsBefore("09:50"))
                lectureIndex = 1;
            else if (checkTimeIsBefore("10:50"))
                lectureIndex = 2;
            else if (checkTimeIsBefore("11:40"))
                lectureIndex = 3;
            else if (checkTimeIsBefore("13:40"))
                lectureIndex = 4;
            else if (checkTimeIsBefore("14:30"))
                lectureIndex = 5;
            else if (checkTimeIsBefore("15:20"))
                lectureIndex = 6;
            else if (checkTimeIsBefore("16:10"))
                lectureIndex = 7;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lectureIndex;
    }

    public static String getTimetableStr() {
        String defaultTimetable = "{\"1\":[{\"name\":\"Timetable Not Found\",\"acro\":\"404\",\"faculty\":\"Error Reporter\",\"start-time\":\"\",\"end-time\":\"\"}]}";
        return sharedPref.getString("timetableStr", defaultTimetable);
    }

    public static float getLocalVersion() {
        return sharedPref.getFloat("localVersion", 0);
    }

    private static boolean checkTimeIsBefore(String time) throws ParseException {

        //Get current time
        Date currentTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        try {
            currentTime = simpleDateFormat.parse(hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, currentTime.toString());
        return currentTime.before(simpleDateFormat.parse(time));
    }
}
