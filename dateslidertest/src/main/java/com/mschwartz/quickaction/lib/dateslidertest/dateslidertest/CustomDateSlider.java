/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * DateSlider which demonstrates the features of the DateSlider ond how
 * to adapt most parameters
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

public class CustomDateSlider extends DateSlider {

    public CustomDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
        super();
        setLayout(com.slider.DateSlider.R.layout.customdateslider);
        setOnDateSetListener(l);
        setInitialTime(calendar);
    }

    /**
     * define our own title of the dialog
     */
    @Override
    protected void setTitle(Calendar time) {
        if (mTitleText != null) {
            mTitleText.setText(getString(com.slider.DateSlider.R.string.dateSliderTitle) +
                    String.format(": %tA, %te/%tm/%ty", time, time, time, time));
        }
    }
}
