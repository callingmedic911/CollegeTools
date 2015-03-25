package com.adityaworks.collegetools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BunkCalculator extends BaseActivity {

    private static int[] percent = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100 };
    private EditText mConductedIn, mAttendedIn;
    private TextView mPercentOut;
    private SeekBar mSeekbar;
    private Button mSubmit;
    private TextView mAnswer;
    private double mConducted, mInitConduct , mAttended, mInitAttend, mCurrent, mInitCurrent, mRequired, mClassNo, mNoOfClass;
    private String mDays, mClasses;
    private InputMethodManager mKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialization
        mConductedIn = (EditText) findViewById(R.id.conducted);
        mAttendedIn = (EditText) findViewById(R.id.attended);
        mPercentOut = (TextView) findViewById(R.id.percent_out);
        mSeekbar = (SeekBar) findViewById(R.id.required);
        mSubmit = (Button) findViewById(R.id.submit);
        mAnswer = (TextView) findViewById(R.id.answer);
        mKeyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initProcess();

    }

    // Returns percentage
    private double calcPercentage(double conducted, double attended ) {
        return (attended/conducted)*100;
    }

    // Returns no, of class need to attend or bunk
    private double noClass() {

        if ( mRequired >= mCurrent ) {
            while ( mCurrent <= mRequired ) {
                mConducted++;
                mAttended++;
                mCurrent = calcPercentage(mConducted, mAttended);

            }
            mClassNo = (int) Math.ceil(Math.abs(mAttended - mInitAttend));
        } else {
            while ( mCurrent >= (mRequired + 0.20) ) { // Added 0.20 as error from some answer.
                mConducted++;
                mCurrent = calcPercentage(mConducted, mAttended);
            }
            mClassNo = Math.ceil(Math.abs(mConducted - mInitConduct));
        }

        return (mClassNo);
    }

    //Processing Part
    private void processOutput() {

        mKeyboard.hideSoftInputFromWindow(mConductedIn.getWindowToken(), 0);
        mKeyboard.hideSoftInputFromWindow(mAttendedIn.getWindowToken(), 0);

        mRequired = percent[mSeekbar.getProgress()];
        try {
            mConducted = Double.parseDouble(mConductedIn.getText().toString());
        } catch (NumberFormatException e) {
            mConducted = 0;
        }
        try {
            mAttended = Double.parseDouble(mAttendedIn.getText().toString());
        } catch (NumberFormatException e) {
            mAttended = 0;
        }
        mCurrent = calcPercentage(mConducted, mAttended);

        mInitConduct = mConducted;
        mInitAttend = mAttended;
        mInitCurrent = mCurrent;

        DecimalFormat df = new DecimalFormat("0.00");

        if (mConducted == 0) {
            mAnswer.setText(getString(R.string.impossible));
        } else if (mRequired == 100 && !(mConducted == mAttended)) {
            mAnswer.setText(getString(R.string.impossible_100));
        } else if (mRequired == 100 && (mConducted == mAttended)) {
            mAnswer.setText(getString(R.string.good_100));
        } else if (mRequired == 0 && mAttended > 0) {
            mAnswer.setText(getString(R.string.impossible_0));
        } else if (mRequired == 0 && mAttended == 0) {
            mAnswer.setText(getString(R.string.perfecto));
        } else {

            mNoOfClass = noClass();
            mClasses = (int) mNoOfClass > 1 ? " classes." : " class.";
            mDays = (int) Math.ceil(mNoOfClass / 8) > 1 ? " days." : " day.";

            if (mRequired > mInitCurrent) {
                mAnswer.setText(getString(R.string.running_low) + df.format(mInitCurrent)
                        + getString(R.string.attend_next)
                        + Integer.toString((int) mNoOfClass)
                        + mClasses + getString(R.string.thatll)
                        + Integer.toString((int) Math.ceil(mNoOfClass / 8)) + mDays);
            } else {
                mAnswer.setText(getString(R.string.running_good)
                        + df.format(mInitCurrent) + getString(R.string.bunk_next)
                        + Integer.toString((int) mNoOfClass)
                        + mClasses + getString(R.string.thatll)
                        + Integer.toString((int) Math.ceil(mNoOfClass / 8)) + mDays);
            }
        }

    }

    //Initialize the process of this bunk calculation activity.
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Testing period over!");
            dialog.setMessage("The testing period of application is now over. Contact Aditya for more details.");
            dialog.setCancelable(false);
            dialog.setNegativeButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }

        //Output SeekBar value to TextView
        mSeekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mPercentOut.setText(Integer.toString(percent[progress]) + "%");
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

        mSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processOutput();
                    }
                }
        );
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_bunk_calculator;
    }

}
