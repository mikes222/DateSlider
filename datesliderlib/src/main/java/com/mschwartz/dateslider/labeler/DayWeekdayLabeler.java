package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.timeview.DayWeekdayLayoutView;
import com.mschwartz.dateslider.timeview.TimeView;

/**
 * A Labeler that displays days using {@link DayWeekdayLayoutView}.
 */
public class DayWeekdayLabeler extends DayLabeler {
    /**
     * The format string that specifies how to display the day. Since this class
     * uses a DayTimeLayoutView, the format string should consist of two strings
     * separated by a space.
     *
     * @param formatString
     */
    public DayWeekdayLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new DayWeekdayLayoutView(context, isCenterView, 30, 8, 0.8f);
    }
}