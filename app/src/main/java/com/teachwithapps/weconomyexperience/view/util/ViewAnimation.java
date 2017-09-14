package com.teachwithapps.weconomyexperience.view.util;

import android.view.View;

import com.teachwithapps.weconomyexperience.util.Log;

/**
 * Created by mint on 14-9-17.
 */

public class ViewAnimation {

    private static final String TAG = ViewAnimation.class.getName();

    public static void viewScaleUp(View view) {
        view.animate()
                .scaleX(1.5f).scaleY(1.5f)
                .setDuration(100)
                .start();
    }

    public static void viewScaleDown(View view) {
        view.animate()
                .scaleX(1.f).scaleY(1.f)
                .setDuration(100)
                .start();
    }

    public static void viewScaleIn(View view) {
        view.animate()
                .alpha(1.f)
                .scaleX(1.f).scaleY(1.f)
                .setDuration(100)
                .start();
    }

    public static void viewScaleOut(final View view) {
        view.animate()
                .alpha(0.f)
                .scaleX(0.f).scaleY(0.f)
                .setDuration(300)
                .start();
    }
}
