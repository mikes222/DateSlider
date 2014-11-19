package com.slider.DateSlider.labeler;

import com.slider.DateSlider.TimeBoundaries;
import com.slider.DateSlider.TimeObject;

import java.util.Calendar;

/**
 * A bunch of static helpers for manipulating dates and times. There are two
 * types of methods -- add*() methods that add a number of units to a time
 * and return the result as a Calendar, and get*() objects that take a
 * Calendar object and a format string and produce the appropriate TimeObject.
 */
class Util {

    private static String TAG = "Util";

    public static TimeObject addYears(long time, int years, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, years, Calendar.YEAR);
        return getYear(c, formatString, timeBoundaries);
    }

    public static TimeObject addMonths(long time, int months, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, months, Calendar.MONTH);
        return getMonth(c, formatString, timeBoundaries);
    }

    public static TimeObject addWeeks(long time, int days, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c =  add(time, days, Calendar.WEEK_OF_YEAR);
        return getWeek(c, formatString, timeBoundaries);
    }

    public static TimeObject addDays(long time, int days, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, days, Calendar.DAY_OF_MONTH);
        return getDay(c, formatString, timeBoundaries);
    }

    public static TimeObject addHours(long time, int hours, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, hours, Calendar.HOUR_OF_DAY);
        if (timeBoundaries.startHour != -1) {
            if (c.get(Calendar.HOUR_OF_DAY) > timeBoundaries.endHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                c.add(Calendar.DATE, 1);
            }
            if (c.get(Calendar.HOUR_OF_DAY) < timeBoundaries.startHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                c.add(Calendar.DATE, -1);
            }
        }
        return getHour(c, formatString, timeBoundaries);
    }

    public static TimeObject addMinutes(long time, int minutes, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, minutes * timeBoundaries.minuteInterval, Calendar.MINUTE);
        if (timeBoundaries.startHour != -1) {
            if (c.get(Calendar.HOUR_OF_DAY) > timeBoundaries.endHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                c.add(Calendar.DATE, 1);
            }
            if (c.get(Calendar.HOUR_OF_DAY) < timeBoundaries.startHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                c.add(Calendar.DATE, -1);
            }
        }
        return getMinute(c, formatString, timeBoundaries);
    }

    public static boolean isOutOfBounds(long startTime, long endTime, TimeBoundaries timeBoundaries) {
        if (timeBoundaries.minTime != -1 && timeBoundaries.minTime > startTime)
            return true;
        if (timeBoundaries.maxTime != -1 && timeBoundaries.maxTime < endTime)
            return true;
        return false;
    }

    public static TimeObject getYear(Calendar c, String formatString, TimeBoundaries timeBoundaries) {
        int year = c.get(Calendar.YEAR);
        // set calendar to first millisecond of the year
        c.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the year
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, c, c), startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getMonth(Calendar c, String formatString, TimeBoundaries timeBoundaries) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        // set calendar to first millisecond of the month
        //noinspection ResourceType
        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        String display = String.format(formatString, c, c);

        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the month
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getWeek(Calendar c, String formatString, TimeBoundaries timeBoundaries) {
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        // set calendar to first millisecond of the week
        c.add(Calendar.DAY_OF_MONTH, -day_of_week);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the week
        c.add(Calendar.DAY_OF_WEEK, 6);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, week), startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));

    }

    public static TimeObject getDay(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        // set the calendar time to the display time
        if (timeBoundaries.minuteInterval > 1) {
            int seconds = c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);    // 0-3599
            int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g.
            if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // false
                nextBoundary += timeBoundaries.minuteInterval * 60;
            int diff = nextBoundary - seconds; // e.g. -5
            c.add(Calendar.SECOND, diff); // e.g. 15
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // set calendar to first millisecond of the day
        //noinspection ResourceType
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        String display = String.format(formatString, c, c);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the day
        //noinspection ResourceType
        c.add(Calendar.DATE, 1);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getHour(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        // set the calendar time to the display time
        if (timeBoundaries.minuteInterval > 1) {
            int seconds = c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);    // 0-3599
            int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g.
            if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // false
                nextBoundary += timeBoundaries.minuteInterval * 60;
            int diff = nextBoundary - seconds; // e.g. -5
            c.add(Calendar.SECOND, diff); // e.g. 15
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // get the first millisecond of that hour
        //noinspection ResourceType
        c.set(year, month, day, hour, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        String display = String.format(formatString, c, c);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // get the last millisecond of that hour
        //noinspection ResourceType
        c.add(Calendar.HOUR, 1);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getMinute(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        // set the calendar time to the display time
        if (timeBoundaries.minuteInterval > 1) {
            int seconds = c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);    // 0-3599
            int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g.
            if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // false
                nextBoundary += timeBoundaries.minuteInterval * 60;
            int diff = nextBoundary - seconds; // e.g. -5
            c.add(Calendar.SECOND, diff); // e.g. 15
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //noinspection ResourceType
        c.set(year, month, day, hour, minute, 0);
        c.set(Calendar.MILLISECOND, 0);
        String display = String.format(formatString, c, c);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // get the first millisecond of that minute interval
        //noinspection ResourceType
        c.add(Calendar.MINUTE, timeBoundaries.minuteInterval);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    private static Calendar add(long time, int val, int field) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }
}
