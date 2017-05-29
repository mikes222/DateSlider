package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.timeview.TwoItemsLayoutView;
import com.mschwartz.dateslider.timeview.TimeView;

/**
 * A Labeler that displays months using {@link TwoItemsLayoutView}.
 */
public class MonthYearLabeler extends MonthLabeler {

    public MonthYearLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new TwoItemsLayoutView(context, isCenterView, 25, 8, 0.95f);
    }
}