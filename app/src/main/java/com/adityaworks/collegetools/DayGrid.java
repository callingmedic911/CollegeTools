package com.adityaworks.collegetools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class DayGrid extends BaseActivity {

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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });

    }

    private void openActivity(int position) {
        Intent intent = new Intent(this, TimetableList.class);
        intent.putExtra(TimetableList.DAY_KEY, position + 1);
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_timetable;
    }
}