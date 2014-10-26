package com.slider.DateSlider;

/**
 * Very simple helper class that defines a time unit with a label (text) its start-
 * and end date
 */
public class TimeObject {

    public final CharSequence text;

    public final long startTime;

    public final long endTime;

    public TimeObject(final CharSequence text, final long startTime, final long endTime) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}