package com.adityaworks.collegetools;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class Timetable extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Work in progress
        String[] gridViewItems = getResources().getStringArray(R.array.gridview_drawer_items);
        GridView gridView = (GridView) findViewById(R.id.gridview);

        gridView.setAdapter(
                new ArrayAdapter<String>(
                        this, R.layout.gridview_list_item,
                        R.id.gridview_text,
                        gridViewItems
                ));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });

    }

    private void openActivity(int position) {
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_timetable;
    }
}