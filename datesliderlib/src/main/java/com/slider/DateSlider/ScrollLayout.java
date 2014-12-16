/*
 * Copyright (C) 2013 Adam Colclough - Axial Exchange
 *
 * This class contains all the scrolling logic of the sliding elements
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
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.slider.DateSlider.labeler.Labeler;
import com.slider.DateSlider.labeler.Util;
import com.slider.DateSlider.timeview.TimeView;

import java.lang.reflect.Constructor;

/**
 * This is where most of the magic happens. This is a subclass of LinearLayout
 * that display a collection of TimeViews and handles the scrolling, shuffling
 * the TimeViews around to keep the display up-to-date, and managing the Labelers
 * to populate the TimeViews with the correct data.
 * <p/>
 * This class is configured via xml attributes that specify the class of the
 * labeler to use to generate views, the format string for the labeler to use
 * to populate the views, and optionally width and height values to override
 * the default width and height of the views.
 */
public class ScrollLayout extends LinearLayout {

    private static String TAG = "ScrollLayout";

    private Scroller mScroller;
    /**
     * Indicates if we are currently tracking touch events that are dragging
     * (scrolling) us.
     */
    private boolean mDragMode;

    /**
     * Indicates if we are dragging or clicking. dragging = true
     */
    private boolean moveMode;
    /**
     * The aggregate width of all of our children
     */
    private int childrenWidth;

    /**
     * The aggregate width of our children is very likely to be wider than the
     * bounds of our view. Since we keep everything centered, we need to keep
     * our view scrolled by enough to center our children, rather than
     * left-aligning them. This variable tracks how much to scroll to achieve this.
     */
    private int mInitialOffset;
    private int mLastX, mLastScroll, mScrollX;
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    /**
     * The time that we are currently displaying
     */
    private long currentTime = System.currentTimeMillis();

    private TimeBoundaries timeBoundaries;

    /**
     * The width of each child. All children must have the same width.
     */
    private int objWidth;

    /**
     * The height of each child. All children must have the same height.
     */
    private int objHeight;


    private Labeler mLabeler;

    private OnScrollListener listener;

    /**
     * The centered view. We make sure that we always have an odd number of views.
     */
    private TimeView mCenterView;

    /**
     * The index of the centered view which is getChilds().size() / 2
     */
    private int centerIndex;

    /**
     * The name of the labeler class
     */
    private String className;

    private String labelerFormat;

    private int childCount;

    private ScrollLayout parent;

    private ScrollLayout child;

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        mScroller = new Scroller(context);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        // as mMaximumVelocity does not exist in API<4
        //float density = context.getResources().getDisplayMetrics().density;
        //mMaximumVelocity = (int) (4000 * 0.5f * density);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollLayout,
                0, 0);

        // Get the labeler class and construct an instance - in the API resulting 'a' cannot be null
        //noinspection ConstantConditions
        className = a.getNonResourceString(R.styleable.ScrollLayout_labelerClass);
        if (className == null) {
            throw new RuntimeException("Must specify labeler class at " + a.getPositionDescription());
        }

        labelerFormat = a.getString(R.styleable.ScrollLayout_labelerFormat);
        if (labelerFormat == null) {
            throw new RuntimeException("Must specify labelerFormat at " + a.getPositionDescription());
        }

        // Determine the width and height of our children, using the labelers preferred
        // values as defaults
        objWidth = a.getDimensionPixelSize(R.styleable.ScrollLayout_childWidth,
                (int) (50 * context.getResources().getDisplayMetrics().density));
        objHeight = a.getDimensionPixelSize(R.styleable.ScrollLayout_childHeight,
                (int) (50 * context.getResources().getDisplayMetrics().density));

        a.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //
        // We need to generate enough children to fill all of our desired space, and
        // it needs to be an odd number of children because we treat the center view
        // specially. So, first compute how many children we will need.
        //
        Context context = getContext();
        Object systemService = null;
        if (context != null) {
            systemService = context.getSystemService(Context.WINDOW_SERVICE);
        }
        if (null == systemService) {
            return;
        }
        final Display display = ((WindowManager) systemService).getDefaultDisplay();
        Point displayPoint = new Point();
        display.getSize(displayPoint);
        final int displayWidth = displayPoint.x;
        childCount = displayWidth / objWidth;
        // Make sure to round up
        if (displayWidth % objWidth != 0) {
            childCount++;
        }
        // Now make sure we have an odd number of children
        if (childCount % 2 == 0) {
            childCount++;
        }

        // We have an odd number of children, so childCount / 2 will round down to the
        // index just before the center in 1-based indexing, meaning that it will be the
        // center index in 0-based indexing.
        centerIndex = (childCount / 2);

        // Make sure we weren't inflated with any views for some odd reason
        removeAllViews();

        // Finally, set our actual children width
        childrenWidth = childCount * objWidth;
    }

    @Override
    public void onSizeChanged(int w, int h, int old_w, int old_h) {
        super.onSizeChanged(w, h, old_w, old_h);
        // In order to keep our children centered, the initial offset has to
        // be half the difference between our children's width and our width.
        mInitialOffset = (childrenWidth - w) / 2;
        // Now scroll to that offset
        super.scrollTo(mInitialOffset, 0);
        mScrollX = mInitialOffset;
        mLastScroll = mInitialOffset;
        if (mCenterView != null) {
            // already initialized, allow this method.
            setTimeInternal(currentTime, 0);
        }
    }

    public void setChild(ScrollLayout child) {
        this.child = child;
    }

    public void setParent(ScrollLayout parent) {
        this.parent = parent;
    }

    public ScrollLayout getChild() {
        return child;
    }

    public TimeBoundaries getTimeBoundaries() {
        return timeBoundaries;
    }

    public void setTimeBoundaries(TimeBoundaries timeBoundaries) {
        this.timeBoundaries = timeBoundaries;

        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor(String.class, TimeBoundaries.class);
            mLabeler = (Labeler) ctor.newInstance(labelerFormat, timeBoundaries);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct labeler " + className, e);
        }

        // Now add all of the child views, making sure to make the center view as such.
        for (int i = 0; i < childCount; i++) {
            LayoutParams lp = new LayoutParams(objWidth, objHeight);
            TimeView ttv = mLabeler.createView(getContext(), i == centerIndex);
            addView((View) ttv, lp);
        }

        // Now we need to set the times on all of the TimeViews. We start with the center
        // view, work our way to the end, then starting from the center again, work our
        // way back to the beginning.
        mCenterView = (TimeView) getChildAt(centerIndex);

        setTime(currentTime);

    }

    public void setTimeByChild(long time) {
        if (mCenterView.getTimeObject().startTime < time && mCenterView.getTimeObject().endTime > time) {
            // the time has not changed so much, we still show the correct item
            return;
        }

        setTime(time);

        if (parent != null) {
            parent.setTimeByChild(time);
        }
    }

    public void setTimeByParent(long time) {
        if (mCenterView.getTimeObject().startTime < time && mCenterView.getTimeObject().endTime > time) {
            // the time has not changed so much, we still show the correct item
            if (child != null) {
                child.setTimeByParent(time);
            }
            return;
        }

        setTime(time);

        if (child != null) {
            child.setTimeByParent(time);
        } else {
            listener.onScroll(getTime());
        }
    }

    /**
     * Sets the time. This method always repopulate all views with new labels.
     *
     * @param time
     */
    public void setTime(long time) {
        currentTime = time;
        //Log.i(TAG, "  setTime " + new java.util.Date(currentTime).toString() + ", " + time.getTime().toString());
        mCenterView.setTime(mLabeler.getElem(currentTime));
        //Log.i(TAG, "time " + mLabeler.getClass().getCanonicalName() + ": " + mLabeler.getElem(currentTime).toString());
        for (int i = centerIndex + 1; i < getChildCount(); i++) {
            TimeView lastView = (TimeView) getChildAt(i - 1);
            TimeView thisView = (TimeView) getChildAt(i);
            if (thisView != null && lastView != null) {
                thisView.setTime(mLabeler.add(lastView.getTimeObject().getDisplayTime(), 1));
            }
        }
        for (int i = centerIndex - 1; i >= 0; i--) {
            TimeView lastView = (TimeView) getChildAt(i + 1);
            TimeView thisView = (TimeView) getChildAt(i);
            if (thisView != null && lastView != null) {
                thisView.setTime(mLabeler.add(lastView.getTimeObject().getDisplayTime(), -1));
            }
        }
    }


    /**
     * this element will position the TimeTextViews such that they correspond to the given time
     *
     * @param loops prevents setTime getting called too often, if loop is > 2 the procedure will be
     *              stopped
     */
    private void setTimeInternal(long time, int loops) {
        if (loops > 2) {
            Log.d(TAG, String.format("time: %s, start: %s, end: %s", Util.format(currentTime), Util.format(mCenterView.getTimeObject().getStartTime()), Util.format(mCenterView.getTimeObject().getEndTime())));
            return;
        }
        currentTime = time;
        if (!mScroller.isFinished()) mScroller.abortAnimation();
        // time duration of centerview
        double diff = mCenterView.getTimeObject().getEndTime() - mCenterView.getTimeObject().getStartTime();
        if (mCenterView.getTimeObject().getStartTime() <= currentTime && mCenterView.getTimeObject().getEndTime() >= currentTime) {
            // current time is now in the centerview
            double center = getWidth() / 2.0;
            int left = (getChildCount() / 2) * objWidth - getScrollX();
            double curr_per = (center - left) / objWidth;
            double goal_per = (currentTime - mCenterView.getTimeObject().getStartTime()) / diff;
            int shift = (int) Math.round((curr_per - goal_per) * objWidth);
            mScrollX -= shift;
            reScrollTo(mScrollX, 0, false);
        } else {
            int steps = (int) Math.round(((currentTime - (mCenterView.getTimeObject().getStartTime() + diff / 2)) / diff));
            Log.d(TAG, steps + " steps to " + Util.format(currentTime) + " from (" + Util.format(mCenterView.getTimeObject().getStartTime()) + "-" + Util.format(mCenterView.getTimeObject().getEndTime()) + "), " + ((diff + 1) / 1000 / 60) + " min");
            moveElements(-steps);
            setTimeInternal(time, loops + 1);
        }
    }

    public long getTime() {
        return mCenterView.getTimeObject().getDisplayTime();
//        if (timeBoundaries.minuteInterval == 1)
//            return mCenterView.getTimeObject().startTime;
//        return (mCenterView.getTimeObject().startTime + mCenterView.getTimeObject().endTime + 1) / 2;
    }

    /**
     * scroll the element when the mScroller is still scrolling
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            reScrollTo(mScrollX, 0, true);
            // Keep on drawing until the animation has finished.
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        reScrollTo(x, y, true);
    }

    /**
     * core scroll function which will replace and move TimeTextViews so that they don't get
     * scrolled out of the layout
     *
     * @param notify if false, the listeners won't be called
     */
    protected void reScrollTo(int x, int y, boolean notify) {
        if (notify) Log.d(TAG, String.format("reScrollTo " + x));
        int scrollX = getScrollX();
        int scrollDiff = x - mLastScroll;

        // estimate whether we are going to reach the lower limit
        if (timeBoundaries.minTime != -1 && notify && scrollDiff < 0) {
            double center = getWidth() / 2.0;
            int left = (getChildCount() / 2) * objWidth - scrollX;
            double f = (center - left) / objWidth;

            long esp_time = (long) (mCenterView.getTimeObject().getStartTime() + (f - ((double) -scrollDiff) / objWidth) * (mCenterView.getTimeObject().getEndTime() - mCenterView.getTimeObject().getStartTime()));

            // if we reach it, prevent surpassing it
            if (esp_time < timeBoundaries.minTime) {
                int deviation = scrollDiff - (int) Math.round(((double) (currentTime - timeBoundaries.minTime)) / (currentTime - esp_time) * scrollDiff);
                mScrollX -= deviation;
                x -= deviation;
                scrollDiff -= deviation;
                if (!mScroller.isFinished()) mScroller.abortAnimation();
            }
        }
        // estimate whether we are going to reach the upper limit
        else if (timeBoundaries.maxTime != -1 && notify && scrollDiff > 0) {
            double center = getWidth() / 2.0;
            int left = (getChildCount() / 2) * objWidth - scrollX;
            double f = (center - left) / objWidth;

            long esp_time = (long) (mCenterView.getTimeObject().getStartTime() + (f - ((double) -scrollDiff) / objWidth) * (mCenterView.getTimeObject().getEndTime() - mCenterView.getTimeObject().getStartTime()));

            // if we reach it, prevent surpassing it
            if (esp_time > timeBoundaries.maxTime) {
                int deviation = scrollDiff - (int) Math.round(((double) (currentTime - timeBoundaries.maxTime)) / (currentTime - esp_time) * scrollDiff);
                mScrollX -= deviation;
                x -= deviation;
                scrollDiff -= deviation;
                if (!mScroller.isFinished()) mScroller.abortAnimation();
            }
        }

        if (childCount > 0) {
            // Determine the absolute x-value for where we are being asked to scroll
            scrollX += scrollDiff;
            // If we've scrolled more than half of a view width in either direction, then
            // a different time is the "current" time, and we need to shuffle our views around.
            // Each additional full view's width on top of the initial half view's width is
            // another position that we need to move our elements. So, we need to add half the
            // width to the amount we've scrolled and then compute how many full multiples of
            // the view width that encompasses to determine how far to move our elements.
            if (scrollX - mInitialOffset > objWidth / 2) {
                // Our scroll target relative to our initial offset
                int relativeScroll = scrollX - mInitialOffset;
                int stepsRight = (relativeScroll + (objWidth / 2)) / objWidth;
                moveElements(-stepsRight);
                // Now modify the scroll target based on our view shuffling.
                scrollX = ((relativeScroll - objWidth / 2) % objWidth) + mInitialOffset - objWidth / 2;
            } else if (mInitialOffset - scrollX > objWidth / 2) {
                int relativeScroll = mInitialOffset - scrollX;
                int stepsLeft = (relativeScroll + (objWidth / 2)) / objWidth;
                moveElements(stepsLeft);
                scrollX = (mInitialOffset + objWidth / 2 - ((mInitialOffset + objWidth / 2 - scrollX) % objWidth));
            }
        }
        super.scrollTo(scrollX, y);
        if (notify) {
            double center = getWidth() / 2.0;
            int left = (getChildCount() / 2) * objWidth - scrollX;
            double f = (center - left) / objWidth;
            currentTime = (long) (mCenterView.getTimeObject().getStartTime() + (mCenterView.getTimeObject().getEndTime() - mCenterView.getTimeObject().getStartTime()) * f);
            currentTime = Util.minStartTime(timeBoundaries, currentTime);
            currentTime = Util.maxEndTime(timeBoundaries, currentTime);
            //Log.i(TAG, "time " + mLabeler.getClass().getCanonicalName() + ": " + mCenterView.getTimeObject().toString());
            if (parent != null)
                parent.setTimeByChild(currentTime);
            if (child != null)
                child.setTimeByParent(currentTime);
            else {
                listener.onScroll(getTime());
            }

        }
        mLastScroll = x;
    }

    /**
     * when the scrolling procedure causes "steps" elements to fall out of the visible layout,
     * all TimeTextViews swap their contents so that it appears that there happens an endless
     * scrolling with a very limited amount of views
     */
    protected void moveElements(int steps) {
        if (steps == 0) {
            return;
        }

        // We need to make each TimeView reflect a value that is -steps units
        // from its current value. As an optimization, we will see if this
        // value is already present in another child (by looking to see if there
        // is a child at an index -steps offset from the target child's index).
        // Since this method is most often called with steps equal to 1 or -1,
        // this is a valuable optimization. However, when doing this we need to
        // make sure that we don't overwrite the value of the other child before
        // we copy the value out. So, when steps is negative, we will be pulling
        // values from children with larger indexes and we want to iterate forwards.
        // When steps is positive, we will be pulling values from children with
        // smaller indexes, and we want to iterate backwards.

        int start;
        int end;
        int direction;
        if (steps < 0) {
            start = 0;
            end = getChildCount();
            direction = 1;
        } else {
            start = getChildCount() - 1;
            end = -1;
            direction = -1;
        }
        for (int i = start; i != end; i += direction) {
            TimeView tv = (TimeView) getChildAt(i);
            if (null == tv) {
                continue;
            }
            int index = i - steps;
            if (index >= 0 && index < getChildCount()) {
                tv.setTime(((TimeView) getChildAt(index)).getTimeObject());
            } else {
                tv.setTime(mLabeler.add(tv.getTimeObject().getDisplayTime(), -steps));
            }
        }
    }

    /**
     * finding whether to scroll or not
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int x = (int) event.getX();
        if (action == MotionEvent.ACTION_DOWN) {
            mDragMode = true;
            moveMode = false;
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }

        if (!mDragMode)
            return super.onTouchEvent(event);

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        //noinspection ConstantConditions
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLastX != x) {
                    // only if moved since the last call
                    mScrollX += mLastX - x;
                    reScrollTo(mScrollX, 0, true);
                    moveMode = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (moveMode) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int initialVelocity = (int) velocityTracker.getXVelocity();
                    if (initialVelocity > mMaximumVelocity)
                        initialVelocity = mMaximumVelocity;
                    if (initialVelocity < mMaximumVelocity * -1)
                        initialVelocity = mMaximumVelocity * -1;

                    if (getChildCount() > 0 && Math.abs(initialVelocity) > mMinimumVelocity) {
                        Log.i(TAG, "fling " + initialVelocity);
                        fling(-initialVelocity);
                    }
                } else {
                    Log.i(TAG, "click " + mScrollX + ", " + mLastX + ", " + x);
                    mScrollX += x - getWidth() / 2;
                    reScrollTo(mScrollX, 0, true);
                }
            case MotionEvent.ACTION_CANCEL:
            default:
                mDragMode = false;

        }
        mLastX = x;

        return true;
    }

    /**
     * causes the underlying mScroller to do a fling action which will be recovered in the
     * computeScroll method
     */
    private void fling(int velocityX) {
        if (getChildCount() > 0) {
            mScroller.fling(mScrollX, 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            invalidate();
        }
    }

    public void setOnScrollListener(OnScrollListener l) {
        listener = l;
    }

    public interface OnScrollListener {
        public void onScroll(long x);
    }
}
