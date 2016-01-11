package com.adityaworks.collegetools;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adityaworks.collegetools.util.MaterialPalette;
import com.adityaworks.collegetools.util.TimetableHelper;

import org.json.JSONException;

import java.util.Calendar;

public class DayGrid extends BaseActivity {

    private static final String LOG_TAG = DayGrid.class.getSimpleName();
    private Point touchPoint;
    private TextView subjectName;
    private TextView expDay;
    private TextView timeDur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default title output
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup grid layout of days
        String[] gridViewItems = getResources().getStringArray(R.array.gridview_drawer_items);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        //Setup next lecture tile
        subjectName = (TextView) findViewById(R.id.subject);
        expDay = (TextView) findViewById(R.id.day);
        timeDur = (TextView) findViewById(R.id.time);
        if (TimetableHelper.getLocalVersion() != 0) {
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
        if (TimetableHelper.getLocalVersion() != 0)
            setupNextLecture();
    }

    private void setupNextLecture() {
        int lectureIndex = TimetableHelper.getLectureIndex();
        int day = TimetableHelper.calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Lecture nextLecture = null;
        String expDayStr;

        if (day == 6 && lectureIndex == 0) {
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
            nextLecture = TimetableHelper.getSubjectDetailAt(lectureIndex, Integer.toString(day));
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


}