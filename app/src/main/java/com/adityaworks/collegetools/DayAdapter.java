package com.adityaworks.collegetools;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by callingmedic911 on 27/3/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class DayAdapter extends ArrayAdapter<String> {
    public DayAdapter(Context context, int resource, String[] strings) {
        super(context, resource, strings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the current date  for this position
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Get data to populate
        String string = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_list_item, parent, false);
        }

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.gridview_layout);
        TextView dayName = (TextView) convertView.findViewById(R.id.gridview_text);
        TextView today = (TextView) convertView.findViewById(R.id.today);

        //Populate data with custom background
        dayName.setText(string);
        if ((day != 1) && (position == day - 2)) {
            layout.setBackgroundResource(R.color.primary_timetable);
            dayName.setTextColor(Color.WHITE);
            today.setText(R.string.today);
        }
        return convertView;
    }
}
