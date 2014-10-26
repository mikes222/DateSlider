package com.slider.DateSlider.labeler;

import com.slider.DateSlider.TimeObject;

import java.util.Calendar;

/**
 * A Labeler that displays months
 */
public class MonthLabeler extends Labeler {
    private final String mFormatString;

    public MonthLabeler(String formatString) {
        super(80, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectFromCalendar(Util.addMonths(time, val));
    }

    @Override
    protected TimeObject timeObjectFromCalendar(Calendar c) {
        return Util.getMonth(c, mFormatString);
    }
}