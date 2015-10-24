package com.adityaworks.collegetools;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.adityaworks.collegetools.util.TimetableHelper;

import java.util.ArrayList;

public class TimetableList extends BaseActivity {

    public static final String DAY_KEY = "selected_date";
    private static final String LOG_TAG = TimetableList.class.getSimpleName();
    public static TimetableAdapter timetableAdapter;
    public static ListView listView;
    public static String selectedDay = "1";

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && hasFocus)
            CircularRevealActivity.sendClearDisplayBroadcast(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            selectedDay = Integer.toString(getIntent().getIntExtra(DAY_KEY, 1));
        }

        listView = (ListView) findViewById(R.id.timetable_list);
        ArrayList<Lecture> timetable = TimetableHelper.getTimetable(selectedDay);
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
