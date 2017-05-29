package com.mschwartz.dateslider;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * This class holds all boundaries relevant for the views. It exists just once per dialog.
 * Created by DELL on 19.11.2014.
 */
public class TimeBoundaries implements Serializable {

    public long minTime = -1;

    public long maxTime = -1;

    /**
     * The interval for the minutes. valid values are from 1 to 60
     */
    public int minuteInterval = 1;

    /**
     * The starting hour of the day. Must be either <code>-1</code> or before endHour.
     */
    public int startHour = -1;

    /**
     * The ending hour of the day. Must be either <code>-1</code> or after startHour.
     */
    public int endHour = -1;

    public TimeZone timezone = TimeZone.getDefault();
}
