package com.adityaworks.collegetools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adityaworks.collegetools.util.MaterialPalette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayGrid extends BaseActivity {

    private static final String LOG_TAG = DayGrid.class.getSimpleName();
    private static SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.appContext);
    private static String timetableStr = sharedPref.getString("timetableStr", TimetableList.defaultTimetable);
    private static Calendar calendar = Calendar.getInstance();
    private Point touchPoint;
    private TextView subjectName;
    private TextView expDay;
    private TextView timeDur;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private Date currentTime;

    public static Lecture getSubjectDetailAt(int lectureIndex, String day) throws JSONException {

        final String LECTURE_NAME = "name";
        final String ACRONYM = "acro";
        final String FACULTY_NAME = "faculty";
        final String START_TIME = "start-time";
        final String END_TIME = "end-time";

        JSONObject timetableJson = new JSONObject(timetableStr);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default title output
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get current time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        try {
            currentTime = simpleDateFormat.parse(hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Setup grid layout of days
        String[] gridViewItems = getResources().getStringArray(R.array.gridview_drawer_items);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        //Setup next lecture tile
        if (timetableStr != null) {
            subjectName = (TextView) findViewById(R.id.subject);
            expDay = (TextView) findViewById(R.id.day);
            timeDur = (TextView) findViewById(R.id.time);
            setupNextLecture();
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gridView.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.toolbar);
            findViewById(R.id.card).setVisibility(View.GONE);
            gridView.setLayoutParams(params);
        }

        gridView.setAdapter(
                new DayAdapter(
                        this, R.layout.gridview_list_item,
                        gridViewItems
                ));
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touchPoint = new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                return false;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNextLecture();
    }

    private void setupNextLecture() {
        int lectureIndex = getLectureIndex();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Lecture nextLecture = null;
        String expDayStr;

        if (day == 6 && lectureIndex == 0){
            day = 1;
            lectureIndex = 0;
            expDayStr = "Monday";
        } else if (day == 0 || lectureIndex == 0) {
            day += 1;
            lectureIndex = 0;
            expDayStr = "Tomorrow";
        } else {
            expDayStr = "Today";
        }

        try {
            nextLecture = getSubjectDetailAt(lectureIndex, Integer.toString(day));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        subjectName.setText(nextLecture.name);
        expDay.setText(expDayStr);
        timeDur.setText(nextLecture.startTime + " - " + nextLecture.endTime);

    }

    private void openActivity(int position) {
        Intent intent = new Intent(this, TimetableList.class);
        intent.putExtra(TimetableList.DAY_KEY, position + 1);
        startActivityWithAnimation(intent);
    }

    private void startActivityWithAnimation(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MaterialPalette materialPalette = new MaterialPalette(getResources().getColor(R.color.primary_timetable), getResources().getColor(R.color.primary_dark_timetable));
            Intent animationIntent = new Intent(this, CircularRevealActivity.class);
            animationIntent.putExtra(CircularRevealActivity.TOUCH_POINT, touchPoint);
            animationIntent.putExtra(CircularRevealActivity.EXTRA_THEME_COLORS, materialPalette);
            animationIntent.putExtra(CircularRevealActivity.ACTIVITY_INTENT, intent);
            Log.i(LOG_TAG, "Animation started.");
            startActivity(animationIntent);
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_timetable;
    }

    public int getLectureIndex() {
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

    private boolean checkTimeIsBefore(String time) throws ParseException {
        Log.i(LOG_TAG, currentTime.toString());
        return currentTime.before(simpleDateFormat.parse(time));
    }
}