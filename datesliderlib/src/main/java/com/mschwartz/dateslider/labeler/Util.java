package com.mschwartz.dateslider.labeler;

import com.mschwartz.dateslider.TimeBoundaries;
import com.mschwartz.dateslider.TimeObject;

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


    public static TimeObject addYears(long time, int years, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, years, Calendar.YEAR, timeBoundaries);
        return getYear(c, formatString, timeBoundaries);
    }

    public static TimeObject addMonths(long time, int months, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
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
        Calendar c = add(time, days, Calendar.WEEK_OF_YEAR, timeBoundaries);
        return getWeek(c, formatString, timeBoundaries);
    }

    public static TimeObject addDays(long time, int days, String formatString, TimeBoundaries timeBoundaries) {
        Calendar c = add(time, days, Calendar.DAY_OF_MONTH, timeBoundaries);
        return getDay(c, formatString, timeBoundaries);
    }

    public static TimeObject addHours(long time, int hours, String formatString, TimeBoundaries timeBoundaries) {
        int incdec = 1;
        if (hours < 0)
            incdec = -1;

        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);

        for (int i = 0; i < Math.abs(hours); ++i) {
            c.add(Calendar.HOUR_OF_DAY, incdec);
            if (timeBoundaries.startHour != -1 && c.get(Calendar.HOUR_OF_DAY) < timeBoundaries.startHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                if (timeBoundaries.minuteInterval > 60)
                    c.add(Calendar.HOUR_OF_DAY, timeBoundaries.minuteInterval / -60 + 1);
                c.add(Calendar.DATE, -1);
            } else if (timeBoundaries.endHour != -1 && c.get(Calendar.HOUR_OF_DAY) >= timeBoundaries.endHour) {

                Calendar reference = createReference(timeBoundaries, c);

                if (c.getTimeInMillis() > reference.getTimeInMillis()) {
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
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);

        for (int i = 0; i < Math.abs(minutes); ++i) {
            c.add(Calendar.MINUTE, incdec * timeBoundaries.minuteInterval);
            if (timeBoundaries.startHour != -1 && c.get(Calendar.HOUR_OF_DAY) < timeBoundaries.startHour) {
                c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
                if (timeBoundaries.minuteInterval > 60)
                    c.add(Calendar.HOUR_OF_DAY, timeBoundaries.minuteInterval / -60 + 1);
                c.add(Calendar.DATE, -1);
            } else if (timeBoundaries.endHour != -1 && c.get(Calendar.HOUR_OF_DAY) >= timeBoundaries.endHour) {

                Calendar reference = createReference(timeBoundaries, c);
                if (c.getTimeInMillis() > reference.getTimeInMillis()) {
                    c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
                    c.add(Calendar.DATE, 1);
                }
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
        if (timeBoundaries.minTime != -1 && timeBoundaries.minTime >= timeObject.getStartTime() && timeBoundaries.minTime <= timeObject.getEndTime()) {
            oobLeft = true;
            oob = false;
        }
        if (timeBoundaries.maxTime != -1 && timeBoundaries.maxTime >= timeObject.getStartTime() && timeBoundaries.maxTime <= timeObject.getEndTime()) {
            oobRight = true;
            oob = false;
        }
        if (checkHours) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(timeBoundaries.timezone);
            calendar.setTimeInMillis(timeObject.getStartTime());
            if (timeBoundaries.startHour != -1 && timeBoundaries.startHour == calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE) == 0) {
                oobLeft = true;
            } else if (timeBoundaries.endHour != -1) {
                calendar.setTimeInMillis(timeObject.getEndTime());
                Calendar reference = createReference(timeBoundaries, calendar);

                if (calendar.getTimeInMillis() >= reference.getTimeInMillis())
                    oobRight = true;
            }
        }
        timeObject.setOob(oob, oobLeft, oobRight);
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

        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();
        // set calendar to last millisecond of the year
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();
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

        if (timeBoundaries.startHour != -1) {
            // when decrementing the minuteInterval we would land at 23:45 the previous day
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
        } else {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();

        // set calendar to last millisecond of the month
        if (timeBoundaries.endHour != -1) {
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
            // maxEndTime will truncate to the last allowed time of day.
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        } else {
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
            c.add(Calendar.MILLISECOND, -1);
        }
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();

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

        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();
        // set calendar to last millisecond of the week
        c.add(Calendar.DATE, 7);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();
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
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c, c);

        if (timeBoundaries.startHour != -1) {
            // when decrementing the minuteInterval we would land at 23:45 the previous day
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
        } else {
            // decrement at the half of the minuteinterval
            c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        }
        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();

        // set calendar to last millisecond of the day
        if (timeBoundaries.endHour != -1) {
            c.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
            // maxEndTime will truncate to the last allowed time of day.
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        } else {
            c.add(Calendar.DATE, 1);
            c.add(Calendar.MILLISECOND, -1);
        }
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();

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
        //c.set(year, month, day, hour, 0, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long displayTime = c.getTimeInMillis();
        String display = String.format(formatString, c, c);

        // decrement at the half of the minuteinterval
        c.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        long temp = c.getTimeInMillis();

        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();

        // get the last millisecond of that hour
        c.setTimeInMillis(temp);
        c.add(Calendar.HOUR, 1);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();

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
        long temp = c.getTimeInMillis();
        long startTime = minStartTime(timeBoundaries, c).getTimeInMillis();

        c.setTimeInMillis(temp);
        c.add(Calendar.MINUTE, timeBoundaries.minuteInterval);
        c.add(Calendar.MILLISECOND, -1);
        long endTime = maxEndTime(timeBoundaries, c).getTimeInMillis();

        TimeObject timeObject = new TimeObject(display, startTime, endTime, displayTime);
        setOob(timeBoundaries, timeObject, true);
        return timeObject;
    }

    private static Calendar add(long time, int val, int field, TimeBoundaries timeBoundaries) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeBoundaries.timezone);
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }

    public static Calendar minStartTime(TimeBoundaries timeBoundaries, Calendar calendar) {
        if (timeBoundaries.startHour != -1 && calendar.get(Calendar.HOUR_OF_DAY) < timeBoundaries.startHour) { // - timeBoundaries.minuteInterval * 30 * 1000) {
            calendar.set(Calendar.HOUR_OF_DAY, timeBoundaries.startHour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }

    public static Calendar maxEndTime(TimeBoundaries timeBoundaries, Calendar calendar) {
        if (timeBoundaries.endHour != -1) {

            Calendar reference = createReference(timeBoundaries, calendar);
            if (calendar.getTimeInMillis() > reference.getTimeInMillis())
                return reference;
        }
        return calendar;
    }

    protected static Calendar createReference(TimeBoundaries timeBoundaries, Calendar calendar) {
        Calendar reference = Calendar.getInstance();
        reference.setTimeInMillis(calendar.getTimeInMillis());
        reference.set(Calendar.HOUR_OF_DAY, timeBoundaries.endHour);
        reference.set(Calendar.MINUTE, 59);
        reference.set(Calendar.SECOND, 59);
        reference.set(Calendar.MILLISECOND, 999);
        reference.add(Calendar.SECOND, timeBoundaries.minuteInterval * -30);
        return reference;
    }

    public static long bindToMinMax(TimeBoundaries timeBoundaries, long time) {
        if (timeBoundaries.minTime != -1 && time < timeBoundaries.minTime)
            time = timeBoundaries.minTime;
        if (timeBoundaries.maxTime != -1 && time > timeBoundaries.maxTime)
            time = timeBoundaries.maxTime;
        return time;
    }

    /**
     * aligns the given calendar time to the nearest minute interval.
     *
     * @param timeBoundaries
     * @param calendar
     * @return
     */
    public static Calendar alignMinuteInterval(TimeBoundaries timeBoundaries, Calendar calendar) {
        // set the calendar time to the display time
        int secondsOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);    // 0-3599
        int nextBoundary = secondsOfDay / timeBoundaries.minuteInterval / 60 * timeBoundaries.minuteInterval * 60; // e.g. 3300
        if (((secondsOfDay - nextBoundary) % (timeBoundaries.minuteInterval * 60)) >= timeBoundaries.minuteInterval * 30) // true
            nextBoundary += timeBoundaries.minuteInterval * 60;
        int diff = nextBoundary - secondsOfDay; // e.g. -5
        calendar.add(Calendar.SECOND, diff); // e.g. 15

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static String format(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(time);
        return sdf.format(start.getTime());
    }
}
