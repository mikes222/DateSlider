package com.slider.DateSlider.labeler;

import com.slider.DateSlider.TimeObject;

import java.util.Calendar;

/**
 * A Labeler that displays days
 */
public class DayLabeler extends Labeler {
    private final String mFormatString;

    public DayLabeler(String formatString) {
        super(80, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectFromCalendar(Util.addDays(time, val));
    }

    @Override
    protected TimeObject timeObjectFromCalendar(Calendar c) {
        return Util.getDay(c, mFormatString);
    }
}