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

    private static final long MILLISECONDSPERDAY = 24 * 60 * 60 * 1000;

    public static TimeObject addYears(long time, int years, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, years, Calendar.YEAR);
        return getYear(c, formatString, timeBoundaries);
    }

    public static TimeObject addMonths(long time, int months, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        boolean last = false;
        if (c.get(Calendar.DATE) > 15)
            last = true;

        c.add(Calendar.MONTH, months);

        // if the original time represents end of month, return also a time representing the end of month.
        // this is necessary when using minuteInterval since the time is half way before the displayed time (e.g. minuteInterval is 30, the time we get here will be 30th, nov, 23:45:00)
        if (last)
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));

        return getMonth(c, formatString, timeBoundaries);
    }

    public static TimeObject addWeeks(long time, int days, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, days, Calendar.WEEK_OF_YEAR);
        return getWeek(c, formatString, timeBoundaries);
    }

    public static TimeObject addDays(long time, int days, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, days, Calendar.DAY_OF_MONTH);
        return getDay(c, formatString, timeBoundaries);
    }

    public static TimeObject addHours(long time, int hours, String formatString, TimeBoundaries timeBoundaries) {
        int incdec = 1;
        if (hours < 0)
            incdec = -1;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        for (int i = 0; i < Math.abs(hours); ++i) {
            c.add(Calendar.HOUR_OF_DAY, incdec);
            if (timeBoundaries.startHour != -1 && timeBoundaries.endHour != -1) {
                if (timeBoundaries.startHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * 60 * 60 * 1000 - timeBoundaries.minuteInterval * 30 * 1000) {
                    c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                    c.add(Calendar.DATE, -1);
                }
                if (timeBoundaries.endHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) > (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1 - timeBoundaries.minuteInterval * 30 * 1000) {
                    if (timeBoundaries.minuteInterval > 1)
                        c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour - 1);
                    else
                        c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                    c.add(Calendar.DATE, 1);
                }
            }
        }
        return getHour(c, formatString, timeBoundaries);
    }

    public static TimeObject addMinutes(long time, int minutes, String formatString, TimeBoundaries timeBoundaries) {
        int incdec = 1;
        if (minutes < 0)
            incdec = -1;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        for (int i = 0; i < Math.abs(minutes); ++i) {
            c.add(Calendar.MINUTE, incdec * timeBoundaries.minuteInterval);
            if (timeBoundaries.startHour != -1 && timeBoundaries.endHour != -1) {
                if (timeBoundaries.startHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * 60 * 60 * 1000 - timeBoundaries.minuteInterval * 30 * 1000) {
                    c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                    c.add(Calendar.DATE, -1);
                }
                if (timeBoundaries.endHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) > (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1 - timeBoundaries.minuteInterval * 30 * 1000) {
                    if (timeBoundaries.minuteInterval > 1)
                        c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour - 1);
                    else
                        c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                    c.add(Calendar.DATE, 1);
                }
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

        if (timeBoundaries.minuteInterval > 1) {
            int seconds = c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);    // 0-3599
            int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g.
            if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // false
                nextBoundary += timeBoundaries.minuteInterval * 60;
            int diff = nextBoundary - seconds; // e.g. -5
            c.add(Calendar.SECOND, diff); // e.g. 15
        }

        int year = c.get(Calendar.YEAR);
        // set calendar to first millisecond of the year
        c.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the year
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getMonth(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

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
        // set calendar to first millisecond of the month
        //noinspection ResourceType
        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the month
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    public static TimeObject getWeek(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        if (timeBoundaries.minuteInterval > 1) {
            int seconds = c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);    // 0-3599
            int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g.
            if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // false
                nextBoundary += timeBoundaries.minuteInterval * 60;
            int diff = nextBoundary - seconds; // e.g. -5
            c.add(Calendar.SECOND, diff); // e.g. 15
        }
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        // set calendar to first millisecond of the week
        c.add(Calendar.DAY_OF_MONTH, -day_of_week);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, week);

        if (timeBoundaries.minuteInterval > 1) {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the week
        c.add(Calendar.DATE, 7);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = c.getTimeInMillis();
        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));

    }

    public static TimeObject getDay(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

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
        long displayTime = c.getTimeInMillis();
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

        //noinspection ResourceType
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (timeBoundaries.startHour != -1 && startTime < c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000) {
            startTime = c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000;
        }
        if (timeBoundaries.endHour != -1 && endTime > c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1) {
            endTime = c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1;
        }

        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));
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
        long displayTime = c.getTimeInMillis();
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
//        if (timeBoundaries.minuteInterval > 1) {
//            // increment at the half of the minuteinterval
//            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * 30);
//        }
        long endTime = c.getTimeInMillis();

        //noinspection ResourceType
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (timeBoundaries.startHour != -1 && startTime < c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000) {
            startTime = c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000;
        }
        if (timeBoundaries.endHour != -1 && endTime > c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1) {
            endTime = c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1;
        }

        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));
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
        long displayTime = c.getTimeInMillis();
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

        //noinspection ResourceType
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (timeBoundaries.startHour != -1 && startTime < c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000) {
            startTime = c.getTimeInMillis() + timeBoundaries.startHour * 60 * 60 * 1000;
        }
        if (timeBoundaries.endHour != -1 && endTime > c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1) {
            endTime = c.getTimeInMillis() + (timeBoundaries.endHour + 1) * 60 * 60 * 1000 - 1;
        }

        return new TimeObject(display, startTime, endTime, displayTime, isOutOfBounds(startTime, endTime, timeBoundaries));
    }

    private static Calendar add(long time, int val, int field) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }
}
