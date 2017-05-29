/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * This is a small demo application which demonstrates the use of the
 * dateSelector
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

package com.mschwartz.dateslider.dateslidertest;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mschwartz.dateslider.DateSlider;

import java.util.Calendar;

/**
 * Small Demo activity which demonstrates the use of the DateSlideSelector
 *
 * @author Daniel Berndt - Codeus Ltd
 */
public class Demo extends AppCompatActivity implements OnClickListener {

    private TextView dateText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // load and initialise the Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dateText = (TextView) this.findViewById(R.id.selectedDateLabel);

        this.findViewById(R.id.defaultDateSelectButton).setOnClickListener(this);
        this.findViewById(R.id.defaultDateLimitSelectButton).setOnClickListener(this);
        this.findViewById(R.id.defaultDateHourSelectButton).setOnClickListener(this);
        this.findViewById(R.id.defaultDateTimeLimitStartEndSelectButton).setOnClickListener(this);
        this.findViewById(R.id.alternativeDateSelectButton).setOnClickListener(this);
        this.findViewById(R.id.customDateSelectButton).setOnClickListener(this);
        this.findViewById(R.id.monthYearDateSelectButton).setOnClickListener(this);
        this.findViewById(R.id.timeSelectButton).setOnClickListener(this);
        this.findViewById(R.id.timeLimitSelectButton).setOnClickListener(this);
        this.findViewById(R.id.dateTimeSelectButton).setOnClickListener(this);
        this.findViewById(R.id.monthdayminuteSelectButton).setOnClickListener(this);

        if (savedInstanceState != null) {
            DateSlider dateSlider = (DateSlider) getSupportFragmentManager().findFragmentByTag("dialogFragment");
            if (dateSlider != null) {
                dateSlider.setOnDateSetListener(mDateTimeSetListener);
            }
        }
    }

    // define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    if (selectedDate == null) {
                        dateText.setText("Date was cleared");
                        return;
                    }
                    // update the dateText view with the corresponding date
                    dateText.setText(String.format("The chosen date:%n%te. %tB %tY", selectedDate, selectedDate, selectedDate));
                }
            };

    private DateSlider.OnDateSetListener mMonthYearSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    if (selectedDate == null) {
                        dateText.setText("Date was cleared");
                        return;
                    }
                    // update the dateText view with the corresponding date
                    dateText.setText(String.format("The chosen date:%n%tB %tY", selectedDate, selectedDate));
                }
            };

    private DateSlider.OnDateSetListener mTimeSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    if (selectedDate == null) {
                        dateText.setText("Date was cleared");
                        return;
                    }
                    // update the dateText view with the corresponding date
                    dateText.setText(String.format("The chosen time:%n%tR", selectedDate));
                }
            };

    private DateSlider.OnDateSetListener mDateTimeSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    if (selectedDate == null) {
                        dateText.setText("Date was cleared");
                        return;
                    }
                    // update the dateText view with the corresponding date
                    dateText.setText(String.format("The chosen date and time:%n%te. %tB %tY%n%tH:%02d",
                            selectedDate, selectedDate, selectedDate, selectedDate, selectedDate.get(Calendar.MINUTE)));
                }
            };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        DialogFragment dialogFragment = getDemoView(id);
        dialogFragment.show(getSupportFragmentManager(), "dialogFragment");
    }

    private DialogFragment getDemoView(int id) {
        final Calendar c = Calendar.getInstance();
        if (id == R.id.defaultDateSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_MINUTEINTERVAL, 15);
            args.putString(DateSlider.ARG_TITLE, "DefaultDate");
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.defaultDateLimitSelectButton) {
            final Calendar maxTime = Calendar.getInstance();
            maxTime.add(Calendar.DAY_OF_MONTH, 14);
            Calendar minTime = Calendar.getInstance();
            minTime.add(Calendar.DAY_OF_MONTH, -7);

            Bundle args = new Bundle();
            args.putLong(DateSlider.ARG_MINTIME, minTime.getTimeInMillis());
            args.putLong(DateSlider.ARG_MAXTIME, maxTime.getTimeInMillis());
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.defaultDateHourSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_STARTHOUR, 9);
            args.putInt(DateSlider.ARG_ENDHOUR, 14);
            args.putInt(DateSlider.ARG_MINUTEINTERVAL, 5);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.defaultDateTimeLimitStartEndSelectButton) {
            final Calendar maxTime = Calendar.getInstance();
            maxTime.add(Calendar.DAY_OF_MONTH, 14);
            Calendar minTime = Calendar.getInstance();
            minTime.add(Calendar.DAY_OF_MONTH, -7);

            Bundle args = new Bundle();
            args.putLong(DateSlider.ARG_MINTIME, minTime.getTimeInMillis());
            args.putLong(DateSlider.ARG_MAXTIME, maxTime.getTimeInMillis());
            args.putInt(DateSlider.ARG_STARTHOUR, 3);
            args.putInt(DateSlider.ARG_ENDHOUR, 11);
            args.putInt(DateSlider.ARG_MINUTEINTERVAL, 120);
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.completedatetimeslider2);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.alternativeDateSelectButton) {
            Bundle args = new Bundle();
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.customDateSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.customdateslider);
            args.putString(DateSlider.ARG_TITLE, getString(com.mschwartz.dateslider.R.string.dateSliderTitle) +
                    ": %tA, %te/%tm/%ty");
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.monthYearDateSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.monthyeardateslider);
            args.putString(DateSlider.ARG_TITLE, getString(com.mschwartz.dateslider.R.string.dateSliderTitle) +
                    String.format(": %tB %tY", c, c));
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.timeSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.timeslider);
            args.putString(DateSlider.ARG_TITLE, "Select time");
            args.putInt(DateSlider.ARG_MINUTEINTERVAL, 15);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.timeLimitSelectButton) {
            final Calendar minTime = Calendar.getInstance();
            minTime.add(Calendar.HOUR, -2);

            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.timeslider);
            args.putString(DateSlider.ARG_TITLE, "Select Time with limit");
            args.putLong(DateSlider.ARG_MINTIME, minTime.getTimeInMillis());
            args.putInt(DateSlider.ARG_MINUTEINTERVAL, 5);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.dateTimeSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.datetimeslider);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        } else if (id == R.id.monthdayminuteSelectButton) {
            Bundle args = new Bundle();
            args.putInt(DateSlider.ARG_LAYOUTID, R.layout.monthdayminuteslider);
            return DateSlider.newInstance(args).setOnDateSetListener(mDateSetListener);
        }
        return null;
    }
}