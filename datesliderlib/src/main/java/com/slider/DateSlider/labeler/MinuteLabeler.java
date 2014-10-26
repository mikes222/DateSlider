package com.slider.DateSlider.labeler;

import com.slider.DateSlider.TimeObject;

import java.util.Calendar;

/**
 * A Labeler that displays minutes
 */
public class MinuteLabeler extends TimeLabeler {


    public MinuteLabeler(String formatString) {
        super(formatString);
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectFromCalendar(Util.addMinutes(time, val, minuteInterval));
    }

    @Override
    protected TimeObject timeObjectFromCalendar(Calendar c) {
    	if (minuteInterval>1) {
    		int minutes = c.get(Calendar.MINUTE);
    		c.set(Calendar.MINUTE, minutes-(minutes%minuteInterval));
    	}
        return Util.getMinute(c, mFormatString, minuteInterval);
    }

}
