package com.mschwartz.dateslider.timeview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;

import com.mschwartz.dateslider.R;
import com.mschwartz.dateslider.TimeObject;

/**
 * This is a simple implementation of a TimeView which is implemented
 * as a TextView. It is aware of whether or not it is the center view
 * in the ScrollLayout so that it can alter its appearance to indicate
 * that it is currently selected.
 */
public class TimeTextView extends AppCompatTextView implements TimeView {

    /**
     * The timeObject currently bound to this view
     */
    protected TimeObject timeObject;

    /**
     * constructor
     *
     * @param isCenterView true if the element is the centered view in the ScrollLayout
     * @param textSize     text size in dps
     */
    public TimeTextView(Context context, boolean isCenterView, int textSize) {
        super(context);
        setupView(isCenterView, textSize);
    }

    /**
     * this method should be overwritten by inheriting classes to define its own look and feel
     *
     * @param isCenterView true if the element is in the center of the scrollLayout
     * @param textSize     textSize in dps
     */
    protected void setupView(boolean isCenterView, int textSize) {
        setGravity(Gravity.CENTER);

        if (isCenterView) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, (textSize * 120) / 100);
            setTypeface(Typeface.DEFAULT_BOLD);
            setTextColor(getResources().getColor(R.color.centerTextColor));
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            setTextColor(getResources().getColor(R.color.textColor));
        }
    }

    @Override
    public boolean onPreDraw() {
        return super.onPreDraw();
//        if (isInEditMode()) {
//            long now = System.currentTimeMillis();
//            setTime(new TimeObject("test", now - 1000, now + 1000, now));
//        }
    }

    public void setTime(TimeObject timeObject) {
        this.timeObject = timeObject;
        setText(timeObject.text);
        setOutOfBounds(timeObject);
    }

    private void setOutOfBounds(TimeObject timeObject) {
        if (timeObject.outOfBounds) {
            setBackgroundResource(R.drawable.oob_background);
        } else if (timeObject.oobLeft && timeObject.oobRight) {
            setBackgroundResource(R.drawable.oob_left_right_background);
        } else if (timeObject.oobLeft) {
            setBackgroundResource(R.drawable.oob_left_background);
        } else if (timeObject.oobRight) {
            setBackgroundResource(R.drawable.oob_right_background);
        } else {
            setBackgroundResource(0);
        }
    }

    @Override
    public TimeObject getTimeObject() {
        return timeObject;
    }

}
