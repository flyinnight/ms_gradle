package com.dilapp.radar.util;

import android.view.View;

public class ViewUtils {

    public static void releaseBackground(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
            view.setBackground(null);
        }
    }

    public static void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }
}
