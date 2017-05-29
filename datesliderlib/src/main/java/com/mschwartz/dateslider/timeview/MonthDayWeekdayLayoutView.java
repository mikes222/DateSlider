package com.mschwartz.dateslider.timeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/**
 * This is a subclass of the {@link DayWeekdayLayoutView} that represents a day. It uses
 * a different color to distinguish Sundays from other days. It provides 3 labels for month, day and day of week
 */
public class MonthDayWeekdayLayoutView extends DayWeekdayLayoutView {

    protected TextView verytopView;

    /**
     * Constructor
     *
     * @param isCenterView   true if the element is the centered view in the ScrollLayout
     * @param topTextSize    text size of the top TextView in dps
     * @param bottomTextSize text size of the bottom TextView in dps
     * @param lineHeight     LineHeight of the top TextView
     */
    public MonthDayWeekdayLayoutView(Context context, boolean isCenterView, int verytopTextSize,
                                     int topTextSize, int bottomTextSize, float lineHeight) {
        super(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
        setupViewVerytop(context, isCenterView, verytopTextSize, lineHeight);
    }

    protected void setupViewVerytop(Context context, boolean isCenterView, int verytopTextSize, float lineHeight) {
        verytopView = new TextView(context);
        verytopView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        verytopView.setLineSpacing(0, lineHeight);
        if (isCenterView) {
            verytopTextSize = (verytopTextSize * 120) / 100;
            topView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, verytopTextSize);
            topView.setTypeface(Typeface.DEFAULT_BOLD);
            topView.setTextColor(0xFF333333);
            topView.setPadding(0, 8 - (int) (verytopTextSize / 12.0), 0, 0);
        } else {
            topView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, verytopTextSize);
            topView.setPadding(0, 8, 0, 0);
            topView.setTextColor(0xFF666666);
        }
        addView(verytopView, 0);

        //super.setupView(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
    }

    @Override
    protected void setText() {
        //super.setText();
        String[] splitTime = timeObject.text.toString().split(" ");
        verytopView.setText(splitTime[0]);
        topView.setText(splitTime[1]);
        bottomView.setText(splitTime[2]);
    }
}