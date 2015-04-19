package com.adityaworks.collegetools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by callingmedic911 on 12/4/15.
 * Copyright Aditya Pandey <aditya@autospace.co>. All right reserved.
 */
public class TimetableAdapter extends ArrayAdapter<Lecture> {

    public TimetableAdapter(Context context, int resource, ArrayList<Lecture> lectures) {
        super(context, resource, lectures);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Lecture lecture = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_list, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.subject_name);
        TextView acro = (TextView) convertView.findViewById(R.id.acro);
        TextView faculty = (TextView) convertView.findViewById(R.id.faculty_name);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);

        // Populate the data into the template view using the data object
        name.setText(lecture.name);
        acro.setText(lecture.acro);
        faculty.setText("By " + lecture.faculty);
        duration.setText(lecture.startTime + " - " + lecture.endTime);

        // Return the completed view to render on screen
        return convertView;
    }
}
