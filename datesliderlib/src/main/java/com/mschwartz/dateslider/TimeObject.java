package com.mschwartz.dateslider;

import com.mschwartz.dateslider.labeler.Util;

/**
 * Very simple helper class that defines a time unit with a label (text), its start-
 * and end time/date. This object represents one label in the UI. It may span one minute/several minutes or one hour/day/week/month/year.
 */
public class TimeObject {

    public final CharSequence text;

    /**
     * The start time (earliest time) which is represented by this object
     */
    public final long startTime;

    /**
     * The end time (latest time) which is represented by this object
     */
    public final long endTime;

    /**
     * The time which is used for displaying. It is the nominal time of the object and will reported to the listener
     */
    public final long displayTime;

    /**
     * True if the object is completely out of bounds
     */
    public boolean outOfBounds;

    /**
     * True if the left (start) time is out of bounds
     */
    public boolean oobLeft;

    /**
     * True if the right (end) time is out of bounds
     */
    public boolean oobRight;

    public TimeObject(final CharSequence text, final long startTime, final long endTime, final long displayTime) {
        this.text = text;
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = displayTime;
    }

    public void setOob(boolean outOfBounds, boolean oobLeft, boolean oobRight) {
        this.outOfBounds = outOfBounds;
        this.oobLeft = oobLeft;
        this.oobRight = oobRight;
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
        return "TimeObject{" +
                "text=" + text +
                ", displayTime=" + Util.format(displayTime) +
                ", startTime=" + Util.format(startTime) +
                ", endTime=" + Util.format(endTime) +
                ", outOfBounds=" + outOfBounds +
                ", oobLeft=" + oobLeft +
                ", oobRight=" + oobRight +
                '}';
    }
}