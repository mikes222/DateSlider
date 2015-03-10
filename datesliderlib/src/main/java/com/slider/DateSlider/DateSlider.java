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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slider.DateSlider.SliderContainer.OnTimeChangeListener;

import java.util.Calendar;

/**
 * A Dialog subclass that hosts a SliderContainer and a couple of buttons,
 * displays the current time in the header, and notifies an observer
 * when the user selects a time.
 */
public class DateSlider extends DialogFragment {

    private static String TAG = "DateSlider";

    protected OnDateSetListener onDateSetListener;

    /**
     * The initial time in utc to be used when creating the slider
     */
    protected long mInitialTime;

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

    protected Button jumpDecYearButton;
    protected Button jumpDecMonthButton;
    protected Button jumpDecWeekButton;
    protected Button jumpDecDayButton;
    protected Button jumpIncDayButton;
    protected Button jumpIncWeekButton;
    protected Button jumpIncMonthButton;
    protected Button jumpIncYearButton;

    /**
     * The text to display as title
     */
    private String title;

    private boolean performAnimation = true;

    private AnimatorSet animators;

    /**
     * Constructor
     */
    public DateSlider() {
        mLayoutID = R.layout.completedatetimeslider;
        setInitialTime(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Sets the id of the layout to be used. Consider R.layout.someLayout.
     *
     * @param layoutID
     * @return
     */
    public DateSlider setLayout(int layoutID) {
        mLayoutID = layoutID;
        return this;
    }

    public DateSlider setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
        return this;
    }

    /**
     * Sets the minimum allowed time in utc timezone. The Class will not return or allow to chose a time beyond the given parameter.
     *
     * @param minTime
     * @return DateSlider
     */
    public DateSlider setMinTime(long minTime) {
        tempTimeBoundaries.minTime = minTime;
        if (mContainer != null) {
            mContainer.setMinTime(tempTimeBoundaries.minTime);
        }
        return this;
    }

    /**
     * Sets the maximum allowed time in utc timezone. The Class will not return or allow to chose a time beyond the given parameter.
     *
     * @param maxTime
     * @return DateSlider
     */
    public DateSlider setMaxTime(long maxTime) {
        tempTimeBoundaries.maxTime = maxTime;
        if (mContainer != null) {
            mContainer.setMaxTime(tempTimeBoundaries.maxTime);
        }
        return this;
    }

    /**
     * Sets the interval for the "minute" part of the timeslider. Allowed range is from 1 to 120
     * whereas only certain values are reasonable. Consider the following values:
     * 1,2,5,10,15,30,60,120
     *
     * @param minuteInterval The minute interval used for the slider
     * @return DateSlider
     */
    public DateSlider setMinuteInterval(int minuteInterval) {
        assert (minuteInterval >= 1 && minuteInterval <= 120);
        assert ((minuteInterval % 60) == 0);
        tempTimeBoundaries.minuteInterval = minuteInterval;
        if (mContainer != null) {
            mContainer.setMinuteInterval(minuteInterval);
        }
        return this;
    }

    /**
     * Sets the time in utc timezone.
     *
     * @param initialTime
     * @return
     */
    public DateSlider setInitialTime(long initialTime) {
        mInitialTime = initialTime;

        if (mContainer != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(initialTime);
            mContainer.setTime(c);
        }
        return this;
    }

    /**
     * Sets the start- and end hours in locale-specific timezone. This can be used if the calendar should only accept working hours. Make sure that either both
     * are set to -1 or both are set to a value from 0 to 23 whereas starthours must be before endHours. Note that endHours
     * only specify the "hour" part of the calender but does not restrict the minutes. In other words setting the endHour to 17
     * allows times up to 17:59 (5:59pm). When setting the minuteInterval to values larger than 60 take care to set the start and endHours accordingly. For
     * example when setting minuteInterval to 120, the startHour should be dividable by 2 (8, 10, 12) whereas the endHour should be an odd number (13, 15, 17)
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
            Long c = savedInstanceState.getLong("time");
            if (c != 0) {
                mInitialTime = c;
            }
            tempTimeBoundaries = (TimeBoundaries) savedInstanceState.getSerializable("tempTimeBoundaries");
            title = savedInstanceState.getString("title");
            mLayoutID = savedInstanceState.getInt("layout");
            performAnimation = savedInstanceState.getBoolean("performAnimation");
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

        jumpDecYearButton = (Button) rootView.findViewById(R.id.decYear);
        jumpDecMonthButton = (Button) rootView.findViewById(R.id.decMonth);
        jumpDecWeekButton = (Button) rootView.findViewById(R.id.decWeek);
        jumpDecDayButton = (Button) rootView.findViewById(R.id.decDay);
        jumpIncDayButton = (Button) rootView.findViewById(R.id.incDay);
        jumpIncWeekButton = (Button) rootView.findViewById(R.id.incWeek);
        jumpIncMonthButton = (Button) rootView.findViewById(R.id.incMonth);
        jumpIncYearButton = (Button) rootView.findViewById(R.id.incYear);

        mContainer.setOnTimeChangeListener(onTimeChangeListener);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mInitialTime);
        mContainer.setTime(c, tempTimeBoundaries);

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

        if (jumpDecYearButton != null) {
            jumpDecYearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.YEAR, -1);
                    DateSlider.this.setTime(c);
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

        if (jumpDecDayButton != null) {
            jumpDecDayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.DATE, -1);
                    DateSlider.this.setTime(c);
                }
            });
        }

        if (jumpIncDayButton != null) {
            jumpIncDayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.DATE, 1);
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

        if (jumpIncYearButton != null) {
            jumpIncYearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = DateSlider.this.getTime();
                    c.add(Calendar.YEAR, 1);
                    DateSlider.this.setTime(c);
                }
            });
        }

        if (getDialog() != null && this.title != null)
            getDialog().setTitle(this.title);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final HorizontalScrollView jumpButtonsScrollView = (HorizontalScrollView) getView().findViewById(R.id.jumpButtonsScrollView);
        if (jumpButtonsScrollView != null) {
            ViewTreeObserver observer = jumpButtonsScrollView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LinearLayout layout = (LinearLayout) getView().findViewById(R.id.jumpbuttons);
                    // larger than screen size
                    int buttonsWidth = layout.getWidth();
                    // max. screen size
                    int scrollWidth = jumpButtonsScrollView.getWidth();
                    final int position = buttonsWidth / 2 - scrollWidth / 2;
                    final int max = buttonsWidth - scrollWidth;
                    Log.i(TAG, "from " + buttonsWidth + ", scrW " + scrollWidth + ", des: " + position);
                    if (performAnimation) {
                        doAnimationForJumpButtons(jumpButtonsScrollView, max, position);
                        disableAnimationAfterFirstStart();
                    } else {
                        jumpButtonsScrollView.smoothScrollTo(position, 0);
                    }
                }
            });
        }
    }


    protected void disableAnimationAfterFirstStart() {
        //performAnimation = false;
    }

    protected void doAnimationForJumpButtons(HorizontalScrollView jumpButtonsScrollView, int max, int position) {
        Log.i(TAG, "doAnim");
        if (animators != null) {
            animators.cancel();
            animators = null;
        }
        ObjectAnimator translate1 = ObjectAnimator.ofInt(jumpButtonsScrollView, "scrollX", max * 3 / 4).setDuration(1000);
        ObjectAnimator translate2 = ObjectAnimator.ofInt(jumpButtonsScrollView, "scrollX", max / 4).setDuration(1000);
        ObjectAnimator translate3 = ObjectAnimator.ofInt(jumpButtonsScrollView, "scrollX", max * 5 / 8).setDuration(1000);
        ObjectAnimator translate4 = ObjectAnimator.ofInt(jumpButtonsScrollView, "scrollX", position).setDuration(1000);

        animators = new AnimatorSet();
        animators.play(translate4);
        //animators.playSequentially(translate1, translate2, translate3, translate4);
        //animators.play(translate1).after(translate2).after(translate3).after(translate4);
        //animators.setStartDelay(1000L);
        animators.setInterpolator(new BounceInterpolator());
        animators.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                Log.i(TAG, "doAnim start");
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                Log.i(TAG, "doAnim end");
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                Log.i(TAG, "doAnim cancel");
            }
        });
        animators.start();

    }

    public void setPerformAnimation(boolean performAnimation) {
        this.performAnimation = performAnimation;
    }

    /**
     * Used by the jump-Buttons to change the time of the slider
     *
     * @param c
     */
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
        outState.putLong("time", getTime().getTimeInMillis());
        outState.putSerializable("tempTimeBoundaries", tempTimeBoundaries);
        outState.putString("title", title);
        outState.putInt("layout", mLayoutID);
        outState.putBoolean("performAnimation", performAnimation);
    }

    /**
     * @return The currently displayed time
     */
    protected Calendar getTime() {
        return mContainer.getTime();
    }

    public DateSlider setTitle(String title) {
        this.title = title;
        if (getDialog() != null)
            getDialog().setTitle(title);
        return this;
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

    /////////////////////////////////////////////////////////////////////////

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
