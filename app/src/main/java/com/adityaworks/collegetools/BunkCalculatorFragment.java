package com.adityaworks.collegetools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by callingmedic911 on 3/3/15.
 */
//TODO: C'mon follow OOP, you dumbshit!

public class BunkCalculatorFragment extends Fragment {

    int[] percent = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100 };
    EditText conducted_in, attended_in;
    TextView percent_out;
    SeekBar seekbar;
    Button submit;
    TextView answer;
    double conducted, init_conduct , attended, init_attend, current, init_current, required, class_no, noOfClass;
    String days, classes;
    ActionBarActivity activity;
    InputMethodManager keyboard;

    public BunkCalculatorFragment() {
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

        // Initialization
        conducted_in = (EditText)rootView.findViewById(R.id.conducted);
        attended_in = (EditText)rootView.findViewById(R.id.attended);
        percent_out = (TextView)rootView.findViewById(R.id.percent_out);
        seekbar = (SeekBar)rootView.findViewById(R.id.required);
        submit = (Button)rootView.findViewById(R.id.submit);
        answer = (TextView)rootView.findViewById(R.id.answer);
        keyboard = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        //Traditional Stuff part 2
        activity.setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.syncState();

        initProcess();

        return rootView;
    }

    // Returns percentage
    public double calcPercentage(double conducted, double attended ) {
        return (attended/conducted)*100;
    }

    // Returns no, of class need to attend or bunk
    public double noClass() {

        if ( required >= current ) {
            while ( current <= required ) {
                conducted++;
                attended++;
                current = calcPercentage(conducted, attended);

            }
            class_no = (int) Math.ceil(Math.abs(attended - init_attend));
        } else {
            while ( current >= (required + 0.20) ) { // Added 0.20 as error from some answer.
                conducted++;
                current = calcPercentage(conducted, attended);
            }
            class_no = Math.ceil(Math.abs(conducted - init_conduct));
        }

        return (class_no);
    }

    //Processing Part
    private void processOutput() {

        keyboard.hideSoftInputFromWindow(conducted_in.getWindowToken(), 0);
        keyboard.hideSoftInputFromWindow(attended_in.getWindowToken(), 0);

        required = percent[seekbar.getProgress()];
        try {
            conducted = Double.parseDouble(conducted_in.getText().toString());
        } catch (NumberFormatException e) {
            conducted = 0;
        }
        try {
            attended = Double.parseDouble(attended_in.getText().toString());
        } catch (NumberFormatException e) {
            attended = 0;
        }
        current = calcPercentage(conducted, attended);

        init_conduct = conducted;
        init_attend = attended;
        init_current = current;

        DecimalFormat df = new DecimalFormat("0.00");

        if (conducted == 0) {
            answer.setText(getString(R.string.impossible));
        } else if (required == 100 && !(conducted == attended)) {
            answer.setText(getString(R.string.impossible_100));
        } else if (required == 100 && (conducted == attended)) {
            answer.setText(getString(R.string.good_100));
        } else if (required == 0 && attended > 0) {
            answer.setText(getString(R.string.impossible_0));
        } else if (required == 0 && attended == 0) {
            answer.setText(getString(R.string.perfecto));
        } else {

            noOfClass = noClass();
            classes = (int) noOfClass > 1 ? " classes." : " class.";
            days = (int) Math.ceil(noOfClass / 8) > 1 ? " days." : " day.";

            if (required > init_current) {
                answer.setText(getString(R.string.running_low) + df.format(init_current)
                        + getString(R.string.attend_next)
                        + Integer.toString((int) noOfClass)
                        + classes + getString(R.string.thatll)
                        + Integer.toString((int) Math.ceil(noOfClass / 8)) + days);
            } else {
                answer.setText(getString(R.string.running_good)
                        + df.format(init_current) + getString(R.string.bunk_next)
                        + Integer.toString((int) noOfClass)
                        + classes + getString(R.string.thatll)
                        + Integer.toString((int) Math.ceil(noOfClass / 8)) + days);
            }
        }

    }

    //Initialize the process of this bunk calculation fragment.
    private void initProcess() {

        //Check if app is running in test period.
        String date_s = "02-04-2015";
        Date date = null;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = format.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ( new Date().after(date) ) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Testing period over!");
            dialog.setMessage("The testing period of application is now over. Contact Aditya for more details.");
            dialog.setCancelable(false);
            dialog.setNegativeButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                }
            });
            dialog.show();
        }

        //Output SeekBar value to TextView
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        percent_out.setText(Integer.toString(percent[progress]) + "%");
                        processOutput();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processOutput();
                    }
                }
        );
    }

}
