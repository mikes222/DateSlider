package com.slider.DateSlider;

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

    public final boolean outOfBounds;

    public TimeObject(final CharSequence text, final long startTime, final long endTime, final boolean outOfBounds) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
        this.outOfBounds = outOfBounds;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "TimeObject{" +
                "text=" + text +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", outOfBounds=" + outOfBounds +
                '}';
    }
}