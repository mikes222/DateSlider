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
    protected Calendar minTime;
    protected Calendar maxTime;
    protected int mLayoutID;
    protected TextView mTitleText;
    protected Button dateSliderOkButton;
    protected Button dateSliderCancelButton;
    protected Button dateSliderClearButton;
    protected SliderContainer mContainer;
    protected int minuteInterval;

    protected Button jumpDecMonthButton;
    protected Button jumpDecWeekButton;
    protected Button jumpIncWeekButton;
    protected Button jumpIncMonthButton;

    public DateSlider() {
        mLayoutID = R.layout.completedateslider;
        setInitialTime(Calendar.getInstance(), 15);
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
        this.minTime = minTime;
        return this;
    }

    public DateSlider setMaxTime(Calendar maxTime) {
        this.maxTime = maxTime;
        return this;
    }

    public int getMinuteInterval() {
        return minuteInterval;
    }

    public DateSlider setInitialTime(Calendar initialTime, int minInterval) {
        assert (minuteInterval >= 1);
        mInitialTime = Calendar.getInstance(initialTime.getTimeZone());
        mInitialTime.setTimeInMillis(initialTime.getTimeInMillis());

        this.minuteInterval = minInterval;
        if (minInterval > 1) {
            int minutes = mInitialTime.get(Calendar.MINUTE);
            int diff = ((minutes + minuteInterval / 2) / minuteInterval) * minuteInterval - minutes;
            mInitialTime.add(Calendar.MINUTE, diff);
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
        mContainer.setMinuteInterval(minuteInterval);
        mContainer.setTime(mInitialTime);
        if (minTime != null) mContainer.setMinTime(minTime);
        if (maxTime != null) mContainer.setMaxTime(maxTime);

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
                onDateSetListener.onDateSet(DateSlider.this, getTime());
            setTitle();
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
    protected void setTitle() {
        if (mTitleText != null) {
            final Calendar c = getTime();
            mTitleText.setText(
                    String.format("%te. %tB %tY %tH:%02d", c, c, c, c, c.get(Calendar.MINUTE)));
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
