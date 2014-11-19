/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * Class for setting up the dialog and initialising the underlying
 * ScrollLayouts
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

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.slider.DateSlider.SliderContainer.OnTimeChangeListener;

import java.util.Calendar;

/**
 * A Dialog subclass that hosts a SliderContainer and a couple of buttons,
 * displays the current time in the header, and notifies an observer
 * when the user selects a time.
 */
public class DateSlider extends DialogFragment {

//	private static String TAG = "DATESLIDER";

    protected OnDateSetListener onDateSetListener;

    protected Calendar mInitialTime;

    protected int mLayoutID;

    protected TimeBoundaries tempTimeBoundaries = new TimeBoundaries();

    /**
     * Optional text containing the currently selected date/time
     */
    protected TextView mTitleText;
    protected Button dateSliderOkButton;
    protected Button dateSliderCancelButton;
    protected Button dateSliderClearButton;

    /**
     * The main container which holds all components belonging to this library. It must not be null.
     */
    protected SliderContainer mContainer;

    protected Button jumpDecMonthButton;
    protected Button jumpDecWeekButton;
    protected Button jumpIncWeekButton;
    protected Button jumpIncMonthButton;

    public DateSlider() {
        mLayoutID = R.layout.completedateslider;
        setInitialTime(Calendar.getInstance());
    }

    public DateSlider setLayout(int layoutID) {
        mLayoutID = layoutID;
        return this;
    }

    public DateSlider setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
        return this;
    }

    public DateSlider setMinTime(Calendar minTime) {
        tempTimeBoundaries.minTime = minTime.getTimeInMillis();
        if (mContainer != null) {
            mContainer.setMinTime(tempTimeBoundaries.minTime);
        }
        return this;
    }

    public DateSlider setMaxTime(Calendar maxTime) {
        tempTimeBoundaries.maxTime = maxTime.getTimeInMillis();
        if (mContainer != null) {
            mContainer.setMaxTime(tempTimeBoundaries.maxTime);
        }
        return this;
    }

    public DateSlider setMinuteInterval(int minuteInterval) {
        assert (minuteInterval >= 1 && minuteInterval < 60);
        tempTimeBoundaries.minuteInterval = minuteInterval;
        if (mContainer != null) {
            mContainer.setMinuteInterval(minuteInterval);
        }
        return this;
    }

    public DateSlider setInitialTime(Calendar initialTime) {
        mInitialTime = Calendar.getInstance(initialTime.getTimeZone());
        mInitialTime.setTimeInMillis(initialTime.getTimeInMillis());

        if (mContainer != null) {
            mContainer.setTime(mInitialTime);
        }
        return this;
    }

    /**
     * Sets the start- and end hours. This can be used if the calendar should only accept working hours. Make sure that either both
     * are set to -1 or both are set to a value from 0 to 23 whereas starthours must be before endHours. Note that endHours
     * only specify the "hour" part of the calender but does not restrict the minutes. In other words setting the endHour to 17
     * allows times up to 17:59 (5:59pm).
     *
     * @param startHour
     * @param endHour
     * @return
     */
    public DateSlider setHours(int startHour, int endHour) {
        assert (startHour == -1 || (startHour >= 0 && startHour <= 23));
        assert (endHour == -1 || (endHour >= 0 && endHour <= 23));
        assert ((startHour == -1 && endHour == -1) || (startHour < endHour));
        tempTimeBoundaries.startHour = startHour;
        tempTimeBoundaries.endHour = endHour;
        if (mContainer != null) {
            mContainer.setHours(startHour, endHour);
        }
        return this;
    }

    /**
     * Set up the dialog with all the views and their listeners
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Calendar c = (Calendar) savedInstanceState.getSerializable("time");
            if (c != null) {
                mInitialTime = c;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayoutID, container, false);
        if (null == rootView) {
            return null;
        }
        mTitleText = (TextView) rootView.findViewById(R.id.dateSliderTitleText);
        mContainer = (SliderContainer) rootView.findViewById(R.id.dateSliderContainer);

        dateSliderOkButton = (Button) rootView.findViewById(R.id.dateSliderOkButton);
        dateSliderCancelButton = (Button) rootView.findViewById(R.id.dateSliderCancelButton);
        dateSliderClearButton = (Button) rootView.findViewById(R.id.dateSliderClearButton);
        jumpDecMonthButton = (Button) rootView.findViewById(R.id.decMonth);
        jumpDecWeekButton = (Button) rootView.findViewById(R.id.decWeek);
        jumpIncWeekButton = (Button) rootView.findViewById(R.id.incWeek);
        jumpIncMonthButton = (Button) rootView.findViewById(R.id.incMonth);

        mContainer.setOnTimeChangeListener(onTimeChangeListener);
        mContainer.setTime(mInitialTime, tempTimeBoundaries);

        if (dateSliderOkButton != null) {
            dateSliderOkButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onDateSetListener.onDateSet(DateSlider.this, getTime());
                    DateSlider.this.dismiss();
                }
            });
        }

        if (dateSliderCancelButton != null) {
            dateSliderCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateSlider.this.dismiss();
                }
            });
        }

        if (dateSliderClearButton != null) {
            dateSliderClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // set null instead of the time
                    onDateSetListener.onDateSet(DateSlider.this, null);
                    DateSlider.this.dismiss();
                }
            });
        }

        if (jumpDecMonthButton != null) {
            jumpDecMonthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.MONTH, -1);
                    DateSlider.this.setTime(c);
                }
            });
        }

        if (jumpDecWeekButton != null) {
            jumpDecWeekButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.DATE, -7);
                    DateSlider.this.setTime(c);
                }
            });
        }

        if (jumpIncWeekButton != null) {
            jumpIncWeekButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.DATE, 7);
                    DateSlider.this.setTime(c);
                }
            });
        }

        if (jumpIncMonthButton != null) {
            jumpIncMonthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.MONTH, 1);
                    DateSlider.this.setTime(c);
                }
            });
        }

        return rootView;
    }

    protected void setTime(Calendar c) {
        mContainer.setTime(c);
    }


    private OnTimeChangeListener onTimeChangeListener = new OnTimeChangeListener() {

        public void onTimeChange(Calendar time) {

            if (onDateSetListener != null && dateSliderOkButton == null)
                onDateSetListener.onDateSet(DateSlider.this, time);
            setTitle(time);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) outState = new Bundle();
        outState.putSerializable("time", getTime());
    }

    /**
     * @return The currently displayed time
     */
    protected Calendar getTime() {
        return mContainer.getTime();
    }

    /**
     * This method sets the title of the dialog
     */
    protected void setTitle(Calendar time) {
        if (mTitleText != null) {
            mTitleText.setText(
                    String.format("%te. %tB %tY %tH:%02d", time, time, time, time, time.get(Calendar.MINUTE)));
        }
    }


    /**
     * Defines the interface which defines the methods of the OnDateSetListener
     */
    public interface OnDateSetListener {
        /**
         * this method is called when a date was selected by the user
         *
         * @param view the caller of the method
         */
        public void onDateSet(DateSlider view, Calendar selectedDate);
    }
}
