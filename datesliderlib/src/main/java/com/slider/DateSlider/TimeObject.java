package com.slider.DateSlider;

import java.util.Calendar;

/**
 * Very simple helper class that defines a time unit with a label (text) its start-
 * and end date
 */
public class TimeObject {

    public final CharSequence text;

    /**
     * The start time (earliest time) which is represented by this object
     */
    public final long startTime;

    /**
     * The end time which is represented by this object
     */
    public final long endTime;

    /**
     * The time which is used for displaying. It is the nominal time of the object and will reported to the listener
     */
    public final long displayTime;

    public final boolean outOfBounds;

    public TimeObject(final CharSequence text, final long startTime, final long endTime, final long displayTime, final boolean outOfBounds) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = displayTime;
        this.outOfBounds = outOfBounds;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDisplayTime() {
        return displayTime;
    }

    @Override
    public String toString() {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startTime);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endTime);
        Calendar display = Calendar.getInstance();
        display.setTimeInMillis(displayTime);

        return "TimeObject{" +
                "text=" + text +
                ", startTime=" + start.getTime().toString() +
                ", endTime=" + end.getTime().toString() +
                ", displayTime=" + display.getTime().toString() +
                ", outOfBounds=" + outOfBounds +
                '}';
    }
}