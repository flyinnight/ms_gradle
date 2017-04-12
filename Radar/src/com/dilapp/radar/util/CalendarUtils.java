package com.dilapp.radar.util;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    public static final long NS = 1000;// 一秒钟的毫秒数long diff;try {
    public static final long NM = NS * 60;// 一分钟的毫秒数
    public static final long NH = NM * 60;// 一小时的毫秒数
    public static final long ND = NH * 24;// 一天的毫秒数
    /**
     * 一周的毫秒数
     */
    public static final long NW = ND * 7;

    public static int diffHour(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(Math.min(time1, time2));
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(Math.max(time1, time2));

        int hours = diffDay(time1, time2) * 24;

        return hours - c1.get(Calendar.HOUR_OF_DAY) + c2.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取两个时间相差多少周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int diffDay(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(Math.min(time1, time2));
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(Math.max(time1, time2));
        final int c1y = c1.get(Calendar.YEAR), c2y = c2.get(Calendar.YEAR);
        int yearOfDay = 0;
        for (int i = c1y; i < c2y; i++) {
            int year = i;
            boolean isR = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
            yearOfDay += (365 + (isR ? 1 : 0)) * Math.abs(c1y - c2y);
        }
        return yearOfDay - c1.get(Calendar.DAY_OF_YEAR) + c2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取两个时间相差多少周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int diffWeek(long time1, long time2) {
        long abs = Math.abs(time2 - time1);
        int r = (int) (abs / (NW));
        return r;
    }

    /**
     * 获取两个时间相差多少月
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int diffMonth(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        int c1y = c1.get(Calendar.YEAR);
        int c2y = c2.get(Calendar.YEAR);
        int c1m = c1.get(Calendar.MONTH) + 1;
        int c2m = c2.get(Calendar.MONTH) + 1;
        // c1 2014-05   c2 2015-01

        int r = Math.abs(c1y - c2y) * 12 + Math.abs(c1m - c2m);
        if (c2y > c1y && c2m < c1m) {
            r = Math.abs(c1y - c2y) * 12 + (12 - c1m + c2m) - 12;
        } else if (c1y > c2y && c1m < c2m) {
            r = Math.abs(c2y - c1y) * 12 + (12 - c2m + c1m) - 12;
        }
        return r;
    }

    /**
     * 获取两个时间相差多少年
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int diffYear(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        int r = Math.abs(c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR));
        return r;
    }

    // 判断是否是同一个小时
    public static boolean sameHour(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        boolean r = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DATE) == c2.get(Calendar.DATE)
                && c1.get(Calendar.HOUR) == c2.get(Calendar.HOUR);
        return r;
    }

    // 判断是否是同�?��
    public static boolean sameDay(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        boolean r = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
        return r;
    }

    public static boolean sameWeek(long time1, long time2) {
        Date mon = new Date();
        calcMondayToWeekend(time1, mon, null);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(mon);
        boolean r = time2 >= c1.getTimeInMillis();
        c1.add(Calendar.DATE, 7);
        c1.add(Calendar.MILLISECOND, -1);
        r = r && time2 <= c1.getTimeInMillis();
        return r;// c3.get(Calendar.DAY);
    }

    // 判断是否是同�?��
    public static boolean sameMonth(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        boolean r = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
        return r;
    }

    // 判断是否是同�?��
    public static boolean sameYear(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);

        boolean r = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
        return r;
    }

    /**
     * 指定时间月份的第�?��星期�?
     *
     * @return 是多少号
     */
    public static int calcMonthOneMonday(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        // 首先定位到这个月�?�?
        c.add(Calendar.DATE, (-c.get(Calendar.DATE) + 1));
        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        week = week == 0 ? 7 : week;
        c.add(Calendar.DATE, week == 1 ? 0 : 7 - week + 1);
        int result = c.get(Calendar.DATE);
        return result;
    }

    /**
     * 这个月有几个星期�?
     *
     * @param time
     * @return
     */
    public static int calcMonthWhatMonday(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int count = c.getActualMaximum(Calendar.DATE);
        c.add(Calendar.DATE, (-c.get(Calendar.DATE) + 1));
        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        week = week == 0 ? 7 : week;
        // System.out.println(count);
        int a = count - (7 - week + 1);
        a = week == 1 ? a + 7 : a;

        int result = a % 7f == 0 ? a / 7 : a / 7 + 1;
        return result;
    }

    public static long[] calcFirstAndLastMilltsForHour(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time);

        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        long[] times = new long[2];
        times[0] = cd.getTimeInMillis();

        cd.add(Calendar.HOUR_OF_DAY, 1);
        times[1] = cd.getTimeInMillis() - 1;
        return times;
    }

    public static long[] calcFirstAndLastMilltsForDay(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time);

        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        long[] times = new long[2];
        times[0] = cd.getTimeInMillis();

        cd.add(Calendar.DATE, 1);
        times[1] = cd.getTimeInMillis() - 1;
        return times;
    }

    public static long[] calcFirstAndLastMilltsForMonth(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time);

        cd.set(Calendar.DAY_OF_MONTH, 1);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        long[] times = new long[2];
        times[0] = cd.getTimeInMillis();
        cd.add(Calendar.MONTH, 1);
        times[1] = cd.getTimeInMillis() - 1;
        return times;
    }

    /**
     * 给出这一年的第一毫秒和最后一毫秒的具体值
     *
     * @param time
     * @return
     */
    public static long[] calcFirstAndLastMilltsForYear(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time);
        // cd.set(Calendar.MONTH, 0);
        cd.set(Calendar.DAY_OF_YEAR, 1);
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        long[] times = new long[2];
        times[0] = cd.getTimeInMillis();
        cd.add(Calendar.YEAR, 1);
        times[1] = cd.getTimeInMillis() - 1;
        return times;
    }

    /**
     * 计算这一天的星期�?��星期�?
     *
     * @param time    时间
     * @param monday  周一
     * @param weekend 周日
     */
    public static void calcMondayToWeekend(long time, Date monday, Date weekend) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(time);

        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        int dc = cd.get(Calendar.DAY_OF_WEEK) - 1;
        cd.add(Calendar.DATE, -((dc == 0 ? 7 : dc) - 1));
        if (monday != null) {
            monday.setTime(cd.getTimeInMillis());
        }
        cd.add(Calendar.DATE, 6);
        if (weekend != null) {
            weekend.setTime(cd.getTimeInMillis());
        }
    }
}
