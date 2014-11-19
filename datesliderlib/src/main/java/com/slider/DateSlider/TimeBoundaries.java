package com.slider.DateSlider;

/**
 * This class holds all boundaries relevant for the views. It exists just once per dialog.
 * Created by DELL on 19.11.2014.
 *
 */
public class TimeBoundaries {

    public long minTime = -1;

    public long maxTime = -1;

    /**
     * The interval for the minutes. valid values are from 1 to 60
     */
    public int minuteInterval = 1;

    public int startHour = -1;

    public int endHour = -1;
}
