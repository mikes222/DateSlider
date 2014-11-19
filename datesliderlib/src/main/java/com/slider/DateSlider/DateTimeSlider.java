/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * DateSlider which allows for selecting of a date including a time
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

package com.slider.DateSlider;

import android.content.Context;

import java.util.Calendar;

public class DateTimeSlider extends DateSlider {

    public DateTimeSlider(Context context, OnDateSetListener l, Calendar calendar,
                          Calendar minDate, Calendar maxDate) {
        super();
        setLayout(R.layout.datetimeslider);
        setOnDateSetListener(l);
        if (minDate != null)
            setMinTime(minDate);
        if (maxDate != null)
            setMaxTime(maxDate);
    }

    @Override
    protected void setTitle(Calendar time) {
        if (mTitleText != null) {
            mTitleText.setText(String.format("Selected DateTime: %te/%tm/%ty %tH:%02d",
                    time, time, time, time, time.get(Calendar.MINUTE)));
        }
    }
}
