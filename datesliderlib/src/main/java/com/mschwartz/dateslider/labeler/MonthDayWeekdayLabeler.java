package com.mschwartz.dateslider.labeler;

import android.content.Context;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.timeview.DayWeekdayLayoutView;
import com.mschwartz.dateslider.timeview.MonthDayWeekdayLayoutView;
import com.mschwartz.dateslider.timeview.TimeView;

/**
 * Created by Mike on 5/29/2017.
 */

public class MonthDayWeekdayLabeler extends DayWeekdayLabeler {
    /**
     * The format string that specifies how to display the day. Since this class
     * uses a DayTimeLayoutView, the format string should consist of two strings
     * separated by a space.
     *
     * @param formatString
     */
    public MonthDayWeekdayLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new MonthDayWeekdayLayoutView(context, isCenterView, 30, 12, 8, 0.8f);
    }
}