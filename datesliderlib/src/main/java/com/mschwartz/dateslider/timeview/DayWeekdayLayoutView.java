package com.mschwartz.dateslider.timeview;

import android.content.Context;

import com.mschwartz.dateslider.R;
import com.mschwartz.dateslider.TimeObject;

import java.util.Calendar;

/**
 * This is a subclass of the {@link TwoItemsLayoutView} that represents a day. It uses
 * a different color to distinguish Sundays from other days.
 */
public class DayWeekdayLayoutView extends TwoItemsLayoutView {

    /**
     * Constructor
     *
     * @param isCenterView   true if the element is the centered view in the ScrollLayout
     * @param topTextSize    text size of the top TextView in dps
     * @param bottomTextSize text size of the bottom TextView in dps
     * @param lineHeight     LineHeight of the top TextView
     */
    public DayWeekdayLayoutView(Context context, boolean isCenterView,
                                int topTextSize, int bottomTextSize, float lineHeight) {
        super(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
    }

    @Override
    public void setTime(TimeObject to) {
        super.setTime(to);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((to.startTime + to.endTime) / 2);
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            colorMeSunday();
        } else {
            colorMeWorkday();
        }
    }

    /**
     * this method is called when the current View takes a Sunday as time unit
     */
    protected void colorMeSunday() {
        if (timeObject.outOfBounds) return;
        if (isCenter) {
            bottomView.setTextColor(getResources().getColor(R.color.sundayBottomCenter));
            topView.setTextColor(getResources().getColor(R.color.sundayTopCenter));
        } else {
            bottomView.setTextColor(getResources().getColor(R.color.sundayBottom));
            topView.setTextColor(getResources().getColor(R.color.sundayTop));
        }
    }


    /**
     * this method is called when the current View takes no Sunday as time unit
     */
    protected void colorMeWorkday() {
        if (timeObject.outOfBounds) return;
        if (isCenter) {
            topView.setTextColor(getResources().getColor(R.color.workdayTopCenter));
            bottomView.setTextColor(getResources().getColor(R.color.workdayBottomCenter));
        } else {
            topView.setTextColor(getResources().getColor(R.color.workdayTop));
            bottomView.setTextColor(getResources().getColor(R.color.workdayBottom));
        }
    }


}