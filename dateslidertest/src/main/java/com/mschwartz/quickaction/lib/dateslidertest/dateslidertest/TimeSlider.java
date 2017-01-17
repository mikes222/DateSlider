/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * DateSlider which allows for an easy selection of a time if you only
 * want to offer certain minute intervals take a look at DateTimeSlider
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mschwartz.quickaction.lib.dateslidertest.dateslidertest;

import android.content.Context;

import com.slider.DateSlider.DateSlider;

import java.util.Calendar;

public class TimeSlider extends DateSlider {

    public TimeSlider(Context context, OnDateSetListener l, Calendar calendar) {
        this(context, l, calendar, null, null, 1);
    }

    public TimeSlider(Context context, OnDateSetListener l, Calendar calendar, int minuteInterval) {
        this(context, l, calendar, null, null, minuteInterval);
    }

    public TimeSlider(Context context, OnDateSetListener l, Calendar calendar,
                      Calendar minTime, Calendar maxTime, int minuteInterval) {
        super();
        setLayout(com.slider.DateSlider.R.layout.timeslider);
        setOnDateSetListener(l);
        setMinuteInterval(minuteInterval);
        if (calendar != null)
            setInitialTime(calendar.getTimeInMillis());
        if (minTime != null)
            setMinTime(minTime.getTimeInMillis());
        if (maxTime != null)
            setMaxTime(maxTime.getTimeInMillis());
    }

    /**
     * define our own title of the dialog
     */
    @Override
    protected void setTitle(Calendar time) {
        if (mTitleText != null) {
            mTitleText.setText(String.format("Selected Time: %tR", time));
        }
    }

}