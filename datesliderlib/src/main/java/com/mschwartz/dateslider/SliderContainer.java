package com.mschwartz.dateslider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.mschwartz.dateslider.labeler.Util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * This is a container class for ScrollLayouts. It coordinates the scrolling
 * between them, so that if one is scrolled, the others are scrolled too to
 * keep a consistent display of the time. It also notifies an optional
 * observer anytime the time is changed.
 */
public class SliderContainer extends LinearLayout {

    private static String TAG = "SliderContainer";

    /**
     * The currently selected time in local timezone. Changes whenever the user moves one of the sliders.
     */
    private final Calendar mTime = Calendar.getInstance();

    private String timezone;
    /**
     * This listener gets informed whenever the user moves one of the sliders and hence changes the time.
     */
    private OnTimeChangeListener mOnTimeChangeListener;

    private TimeBoundaries timeBoundaries = new TimeBoundaries();

    public SliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        timeBoundaries.minuteInterval = 1;
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        ScrollLayout last = null;
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v instanceof ScrollLayout) {
                final ScrollLayout sl = (ScrollLayout) v;
                sl.setOnScrollListener(
                        new ScrollLayout.OnScrollListener() {
                            public void onScroll(long x) {
                                mTime.setTimeInMillis(x);
                                //arrangeScrollLayout(sl);
                                if (mOnTimeChangeListener != null) {
                                    mOnTimeChangeListener.onTimeChange(mTime);
                                }
                            }
                        });
                sl.setTimeBoundaries(timeBoundaries);
                if (last != null) {
                    sl.setParent(last);
                    last.setChild(sl);
                }
                last = sl;
            }
        }
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        if (timezone != null)
            mTime.setTimeZone(TimeZone.getTimeZone(timezone));
    }

    public void setTime(Calendar calendar, TimeBoundaries tempTimeBoundaries) {
        if (tempTimeBoundaries.minuteInterval > 1)
            timeBoundaries.minuteInterval = tempTimeBoundaries.minuteInterval;
        if (tempTimeBoundaries.startHour != -1) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, tempTimeBoundaries.startHour);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            timeBoundaries.startHour = Util.alignMinuteInterval(timeBoundaries, c).get(Calendar.HOUR_OF_DAY);
        }
        if (tempTimeBoundaries.endHour != -1) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, tempTimeBoundaries.endHour);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.add(Calendar.MILLISECOND, -1);
            timeBoundaries.endHour = Util.alignMinuteInterval(timeBoundaries, c).get(Calendar.HOUR_OF_DAY) + 1;
        }
        if (tempTimeBoundaries.minTime != -1) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(tempTimeBoundaries.minTime);
            timeBoundaries.minTime = Util.maxEndTime(timeBoundaries, Util.minStartTime(timeBoundaries, c)).getTimeInMillis();
        }
        if (tempTimeBoundaries.maxTime != -1) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(tempTimeBoundaries.maxTime);
            timeBoundaries.maxTime = Util.maxEndTime(timeBoundaries, Util.minStartTime(timeBoundaries, c)).getTimeInMillis();
        }
        setTime(calendar);
    }

    /**
     * Set the current time and update all of the child ScrollLayouts accordingly.
     */
    public void setTime(Calendar calendar) {
        // first bind the time to the min and max start/end times, then align it. Goal is to get the displayed time.
        calendar.setTimeInMillis(Util.bindToMinMax(timeBoundaries, calendar.getTimeInMillis()));
        calendar = Util.minStartTime(timeBoundaries, calendar);
        calendar = Util.maxEndTime(timeBoundaries, calendar);
        calendar = Util.alignMinuteInterval(timeBoundaries, calendar);

        mTime.setTimeInMillis(calendar.getTimeInMillis());

        arrangeScrollLayout(null);
    }

    /**
     * Get the current time
     *
     * @return The current time
     */
    public Calendar getTime() {
        return mTime;
    }

    public void setMinTime(long minTime) {
        timeBoundaries.minTime = minTime;
        arrangeScrollLayout(null);
    }

    public void setMaxTime(long maxTime) {
        timeBoundaries.maxTime = maxTime;
        arrangeScrollLayout(null);
    }

    public void setMinuteInterval(int minuteInterval) {
        timeBoundaries.minuteInterval = minuteInterval;
        arrangeScrollLayout(null);
    }

    public void setHours(int startHour, int endHour) {
        timeBoundaries.startHour = startHour;
        timeBoundaries.endHour = endHour;
        arrangeScrollLayout(null);
    }

    /**
     * Sets the OnTimeChangeListener, which will be notified anytime the time is
     * set or changed.
     */
    public void setOnTimeChangeListener(OnTimeChangeListener l) {
        mOnTimeChangeListener = l;
    }

    /**
     * Pushes our current time into all child ScrollLayouts, except the source
     * of the time change (if specified)
     *
     * @param source The ScrollLayout that generated the time change, or null if
     *               this isn't the result of a ScrollLayout-generated time change.
     */
    private void arrangeScrollLayout(ScrollLayout source) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v == source) {
                continue;
            }
            if (v instanceof ScrollLayout) {
                ScrollLayout scroller = (ScrollLayout) v;
                scroller.setTime(mTime.getTimeInMillis());
            }
        }
        if (mOnTimeChangeListener != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mTime.getTimeInMillis());
            mOnTimeChangeListener.onTimeChange(calendar);
        }
    }

    /////////////////////////////////////////////////////////////////////////

    /**
     * This listener gets called whenever the user moves one of the time sliders.
     */
    public static interface OnTimeChangeListener {
        public void onTimeChange(Calendar time);
    }
}
