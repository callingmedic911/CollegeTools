/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adityaworks.collegetools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Outline;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.adityaworks.collegetools.util.MaterialPalette;

/**
 * Lightweight activity used to display a circular reveal while InCallActivity is starting up.
 * A BroadcastReceiver is used to listen to broadcasts from a LocalBroadcastManager to finish
 * the activity at suitable times.
 */
public class CircularRevealActivity extends Activity {
    public static final String EXTRA_THEME_COLORS = "extra_theme_colors";
    public static final String TOUCH_POINT = "touchPoint";
    public static final String ACTIVITY_INTENT = "activityIntent";
    public static final String ACTION_CLEAR_DISPLAY = "action_clear_display";
    private static final int REVEAL_DURATION = 333;
    private static final String LOG_TAG = CircularRevealActivity.class.getSimpleName();
    final BroadcastReceiver mClearDisplayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            clearDisplay();
        }
    };

    public static void sendClearDisplayBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_CLEAR_DISPLAY));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.outgoing_call_animation);
        final Point touchPoint = getIntent().getParcelableExtra(TOUCH_POINT);
        final MaterialPalette palette = getIntent().getParcelableExtra(EXTRA_THEME_COLORS);
        final Intent intent = getIntent().getParcelableExtra(ACTIVITY_INTENT);
        setupDecorView(touchPoint, palette, intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLEAR_DISPLAY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mClearDisplayReceiver, filter);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mClearDisplayReceiver);
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupDecorView(final Point touchPoint, MaterialPalette palette, final Intent intent) {
        final View view = getWindow().getDecorView();
        // The circle starts from an initial size of 0 so clip it such that it is invisible. When
        // the animation later starts, this clip will be clobbered by the circular reveal clip.
        // See ViewAnimationUtils.createCircularReveal.
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Using (0, 0, 0, 0) will not work since the outline will simply be treated as
                // an empty outline.
                outline.setOval(-1, -1, 0, 0);
            }
        });
        view.setClipToOutline(true);

        if (palette != null) {
            view.findViewById(R.id.outgoing_call_animation_circle).setBackgroundColor(
                    palette.mPrimaryColor);
            getWindow().setStatusBarColor(palette.mSecondaryColor);
        }

        view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final ViewTreeObserver vto = view.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                }
                final Animator animator = getRevealAnimator(touchPoint);
                // Since this animator is a RenderNodeAnimator (native animator), add an arbitary
                // start delay to force the onAnimationStart callback to happen later on the UI
                // thread. Otherwise it would happen right away inside animator.start()
                animator.setStartDelay(5);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        onCircularRevealStarted(intent);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setClipToOutline(false);
                        super.onAnimationEnd(animation);
                    }
                });
                animator.start();
                return false;
            }
        });
    }

    private void clearDisplay() {
        getWindow().getDecorView().setVisibility(View.INVISIBLE);
        Log.i(LOG_TAG, "Animation finished.");
        finish();
    }

    private void onCircularRevealStarted(final Intent intent) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "Starting activity after circular reveal");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Animator getRevealAnimator(Point touchPoint) {
        final View view = getWindow().getDecorView();
        final Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        int startX = size.x / 2;
        int startY = size.y / 2;
        if (touchPoint != null) {
            startX = touchPoint.x;
            startY = touchPoint.y;
        }

        final Animator valueAnimator = ViewAnimationUtils.createCircularReveal(view,
                startX, startY, 0, Math.max(size.x, size.y));
        valueAnimator.setDuration(REVEAL_DURATION);
        return valueAnimator;
    }
}
