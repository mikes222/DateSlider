package com.slider.DateSlider.labeler;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;

import com.slider.DateSlider.TimeBoundaries;
import com.slider.DateSlider.TimeObject;
import com.slider.DateSlider.timeview.TimeTextView;
import com.slider.DateSlider.timeview.TimeView;

import java.util.Calendar;

/**
 * A customized Labeler that displays weeks using a CustomTimeTextView
 */
public class WeekLabeler extends Labeler {


    public WeekLabeler(String formatString, TimeBoundaries timeBoundaries) {
        super(formatString, timeBoundaries);
    }

    @Override
    public TimeObject add(long time, int val) {
        return Util.addWeeks(time, val, mFormatString, timeBoundaries);
    }

    /**
     * create our customized TimeTextView and return it
     */
    public TimeView createView(Context context, boolean isCenterView) {
        return new CustomTimeTextView(context, isCenterView, 20);
    }

    /**
     * Here we define our Custom TimeTextView which will display the fonts in its very own way.
     */
    private static class CustomTimeTextView extends TimeTextView {

        public CustomTimeTextView(Context context, boolean isCenterView, int textSize) {
            super(context, isCenterView, textSize);
        }

        /**
         * Here we set up the text characteristics for the TextView, i.e. red colour,
         * serif font and semi-transparent white background for the centerView... and shadow!!!
         */
        @Override
        protected void setupView(boolean isCenterView, int textSize) {
            setGravity(Gravity.CENTER);
            setTextColor(0xFF883333);
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            setTypeface(Typeface.SERIF);
            if (isCenterView) {
                setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
                setBackgroundColor(0x55FFFFFF);
                setShadowLayer(2.5f, 3, 3, 0xFF999999);
            }
        }

    }

    public TimeObject getElem(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return Util.getWeek(c, mFormatString, timeBoundaries);
    }

}