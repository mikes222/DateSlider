package com.mschwartz.dateslider.timeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mschwartz.dateslider.R;
import com.mschwartz.dateslider.TimeObject;

/**
 * This is a more complex implementation of the TimeView consisting of a LinearLayout with
 * two TimeViews. This allows primary text and sub-text, such as the name of the day
 * and the day of the month. This class expects the text that it is passed via
 * {@link #setTime(TimeObject)} to contain the primary
 * string followed by a space and then the secondary string.
 */
public class TwoItemsLayoutView extends LinearLayout implements TimeView {

    /**
     * The timeObject currently bound to this view
     */
    protected TimeObject timeObject;

    protected boolean isCenter = false;

    protected TextView topView;
    protected TextView bottomView;

    /**
     * constructor
     *
     * @param isCenterView   true if the element is the centered view in the ScrollLayout
     * @param topTextSize    text size of the top TextView in dps
     * @param bottomTextSize text size of the bottom TextView in dps
     * @param lineHeight     LineHeight of the top TextView
     */
    public TwoItemsLayoutView(Context context, boolean isCenterView, int topTextSize, int bottomTextSize, float lineHeight) {
        super(context);
        setupView(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
    }

    /**
     * Setting up the top TextView and bottom TextVew
     *
     * @param isCenterView   true if the element is the centered view in the ScrollLayout
     * @param topTextSize    text size of the top TextView in dps
     * @param bottomTextSize text size of the bottom TextView in dps
     * @param lineHeight     LineHeight of the top TextView
     */
    protected void setupView(Context context, boolean isCenterView, int topTextSize, int bottomTextSize, float lineHeight) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        topView = new TextView(context);
        topView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        topView.setLineSpacing(0, lineHeight);

        bottomView = new TextView(context);
        bottomView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        bottomView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bottomTextSize);
        if (isCenterView) {
            isCenter = true;
            topTextSize = (topTextSize * 120) / 100;
            topView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, topTextSize);
            topView.setTypeface(Typeface.DEFAULT_BOLD);
            topView.setTextColor(0xFF333333);
            topView.setPadding(0, 8 - (int) (topTextSize / 12.0), 0, 0);
            bottomView.setTypeface(Typeface.DEFAULT_BOLD);
            bottomView.setTextColor(0xFF444444);
        } else {
            topView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, topTextSize);
            topView.setPadding(0, 8, 0, 0);
            topView.setTextColor(0xFF666666);
            bottomView.setTextColor(0xFF666666);
        }
        addView(topView);
        addView(bottomView);

    }


    public void setTime(TimeObject timeObject) {
        this.timeObject = timeObject;
        setText();
        setOutOfBounds(timeObject);
    }

    @Override
    public TimeObject getTimeObject() {
        return timeObject;
    }

    /**
     * sets the TextView texts by splitting the text into two
     */
    protected void setText() {
        String[] splitTime = timeObject.text.toString().split(" ");
        topView.setText(splitTime[0]);
        bottomView.setText(splitTime[1]);
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


}