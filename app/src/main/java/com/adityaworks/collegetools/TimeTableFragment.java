package com.adityaworks.collegetools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by callingmedic911 on 23/3/15.
 */
public class TimeTableFragment extends Fragment {

    ActionBarActivity activity;

    public TimeTableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Some traditional stuff
        ActionBarDrawerToggle actionBarDrawerToggle;
        DrawerLayout drawerLayout;
        Toolbar toolbar;
        View rootView;

        activity = (ActionBarActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_bunk_calculator, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        //Traditional Stuff part 2
        activity.setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.syncState();

        return rootView;
    }
}
