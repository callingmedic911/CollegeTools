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

import com.adityaworks.collegetools.util.MaterialPalette;

public class DayGrid extends BaseActivity {

    private static final String LOG_TAG = DayGrid.class.getSimpleName();
    private Point touchPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default title output
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //TODO: Work in progress
        String[] gridViewItems = getResources().getStringArray(R.array.gridview_drawer_items);
        GridView gridView = (GridView) findViewById(R.id.gridview);


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