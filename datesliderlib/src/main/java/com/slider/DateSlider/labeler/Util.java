package com.slider.DateSlider.labeler;

import com.slider.DateSlider.TimeBoundaries;
import com.slider.DateSlider.TimeObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A bunch of static helpers for manipulating dates and times. There are two
 * types of methods -- add*() methods that add a number of units to a time
 * and return the result as a Calendar, and get*() objects that take a
 * Calendar object and a format string and produce the appropriate TimeObject.
 */
public class Util {

    private static String TAG = "Util";

    private static final long MILLISECONDSPERDAY = 24 * 60 * 60 * 1000;

    private static final long MILLISECONDSPERHOUR = 60 * 60 * 1000;


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
            if (timeBoundaries.startHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * MILLISECONDSPERHOUR) { // - timeBoundaries.minuteInterval * 30 * 1000) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                c.add(Calendar.DATE, -1);
            } else if (timeBoundaries.endHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) >= (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR) { // - 1 - timeBoundaries.minuteInterval * 30 * 1000) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                c.add(Calendar.DATE, 1);
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
            if (timeBoundaries.startHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * MILLISECONDSPERHOUR) { // - timeBoundaries.minuteInterval * 30 * 1000) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                c.add(Calendar.DATE, -1);
            } else if (timeBoundaries.endHour != -1 && (c.getTimeInMillis() % MILLISECONDSPERDAY) >= (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR) { // - 1 - timeBoundaries.minuteInterval * 30 * 1000) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                c.add(Calendar.DATE, 1);
            }
        }
        return getMinute(c, formatString, timeBoundaries);
    }

    public static void setOob(TimeBoundaries timeBoundaries, TimeObject timeObject, boolean checkHours) {
        boolean oob = false;
        boolean oobLeft = false;
        boolean oobRight = false;
        if (timeBoundaries.minTime != -1 && timeBoundaries.minTime > timeObject.getStartTime()) {
            oob = true;
        }
        if (timeBoundaries.maxTime != -1 && timeBoundaries.maxTime < timeObject.getEndTime()) {
            oob = true;
        }
        if (timeBoundaries.minTime != -1 && timeBoundaries.minTime > timeObject.getStartTime() && timeBoundaries.minTime < timeObject.getEndTime()) {
            oobLeft = true;
            oob = false;
        }
        if (timeBoundaries.maxTime != -1 && timeBoundaries.maxTime < timeObject.getEndTime() && timeBoundaries.maxTime > timeObject.getStartTime()) {
            oobRight = true;
            oob = false;
        }
        if (checkHours) {
            if (timeBoundaries.startHour != -1 && timeBoundaries.startHour * MILLISECONDSPERHOUR == (timeObject.getStartTime() % MILLISECONDSPERDAY)) {
                oobLeft = true;
            } else if (timeBoundaries.endHour != -1 && (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR - 1 - timeBoundaries.minuteInterval * 30 * 1000 == (timeObject.getEndTime() % MILLISECONDSPERDAY)) {
                oobRight = true;
            }
        }
        timeObject.setOob(oob, oobLeft, oobRight);
    }

    public static boolean isOutOfBounds(long startTime, long endTime, TimeBoundaries timeBoundaries) {
        if (timeBoundaries.minTime != -1 && timeBoundaries.minTime > startTime)
            return true;
        if (timeBoundaries.maxTime != -1 && timeBoundaries.maxTime < endTime)
            return true;
        return false;
    }

    public static TimeObject getYear(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);

        int year = c.get(Calendar.YEAR);
        // set calendar to first millisecond of the year
        c.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);

        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());
        // set calendar to last millisecond of the year
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());
        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, false);
        return timeObject;
    }

    public static TimeObject getMonth(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        // set calendar to first millisecond of the month
        //noinspection ResourceType
        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);

        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());
        // set calendar to last millisecond of the month
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());
        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, false);
        return timeObject;
    }

    public static TimeObject getWeek(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);

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

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);

        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());
        // set calendar to last millisecond of the week
        c.add(Calendar.DATE, 7);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());
        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, false);
        return timeObject;
    }

    public static TimeObject getDay(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // set calendar to first millisecond of the day
        //noinspection ResourceType
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        if (timeBoundaries.startHour != -1) {
            // when decrementing the minuteInterval we would land at 23:45 the previous day
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
        } else {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());

        // set calendar to last millisecond of the day
        if (timeBoundaries.endHour != -1) {
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
            // maxEndTime will truncate to the last alllowed time of day.
            c.set(Calendar.MINUTE, 59);
        } else {
            //noinspection ResourceType
            c.add(Calendar.DATE, 1);
            c.add(Calendar.MILLISECOND, -1);
        }
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());

        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, false);
        return timeObject;
    }

    public static TimeObject getHour(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // get the first millisecond of that hour
        //noinspection ResourceType
        c.set(year, month, day, hour, 0, 0);

        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);

        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());
        // get the last millisecond of that hour
        //noinspection ResourceType
        c.add(Calendar.HOUR, 1);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());

        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, true);
        return timeObject;
    }

    public static TimeObject getMinute(Calendar c, String formatString, TimeBoundaries timeBoundaries) {

        c = alignMinuteInterval(timeBoundaries, c);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        long startTime = minStartTime(timeBoundaries, c.getTimeInMillis());

        c.setTimeInMillis(startTime);
        //noinspection ResourceType
        c.add(Calendar.MINUTE, timeBoundaries.minuteInterval);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c.getTimeInMillis());

        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, true);
        return timeObject;
    }

    private static Calendar add(long time, int val, int field) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }

    public static Calendar minStartTime(TimeBoundaries timeBoundaries, Calendar calendar) {
        if (timeBoundaries.startHour != -1 && (calendar.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * MILLISECONDSPERHOUR) { // - timeBoundaries.minuteInterval * 30 * 1000) {
            calendar.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }

    public static long minStartTime(TimeBoundaries timeBoundaries, long startTime) {
        if (timeBoundaries.startHour != -1 && (startTime % MILLISECONDSPERDAY) < timeBoundaries.startHour * MILLISECONDSPERHOUR) {
            startTime = startTime - (startTime % MILLISECONDSPERDAY) + timeBoundaries.startHour * MILLISECONDSPERHOUR;
        }
        return startTime;
    }

    public static Calendar maxEndTime(TimeBoundaries timeBoundaries, Calendar calendar) {
        if (timeBoundaries.endHour != -1 && (calendar.getTimeInMillis() % MILLISECONDSPERDAY) >= (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR - timeBoundaries.minuteInterval * 30 * 1000) {
            calendar.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        return calendar;
    }

    public static long maxEndTime(TimeBoundaries timeBoundaries, long endTime) {
        if (timeBoundaries.endHour != -1 && (endTime % MILLISECONDSPERDAY) >= (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR - timeBoundaries.minuteInterval * 30 * 1000) {
            endTime = endTime - (endTime % MILLISECONDSPERDAY) + (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR - 1 - timeBoundaries.minuteInterval * 30 * 1000;
        }
        return endTime;
    }

    public static Calendar alignMinuteInterval(TimeBoundaries timeBoundaries, Calendar calendar) {
        // set the calendar time to the display time
        int seconds = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);    // 0-3599
        int nextBoundary = seconds / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g. 3300
        if (((seconds - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // true
            nextBoundary += timeBoundaries.minuteInterval * 60;
        int diff = nextBoundary - seconds; // e.g. -5
        calendar.add(Calendar.SECOND, diff); // e.g. 15

        calendar.set(Calendar.MILLISECOND, 0);

        if (timeBoundaries.startHour != -1 && (calendar.getTimeInMillis() % MILLISECONDSPERDAY) < timeBoundaries.startHour * MILLISECONDSPERHOUR) { // - timeBoundaries.minuteInterval * 30 * 1000) {
            calendar.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
        }
        // if we have endHour set we need to change to endHour - minuteInterval
        if (timeBoundaries.endHour != -1 && (calendar.getTimeInMillis() % MILLISECONDSPERDAY) >= (timeBoundaries.endHour + 1) * MILLISECONDSPERHOUR) { // - timeBoundaries.minuteInterval * 30 * 1000) {
//            calendar.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
            calendar.add(Calendar.MINUTE, timeBoundaries.minuteInterval * -1);
//            calendar.set(Calendar.SECOND, 59);
//            calendar.set(Calendar.MILLISECOND, 999);
        }
        return calendar;
    }

    public static String format(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(time);
        return sdf.format(start.getTime());
    }
}
