package com.ch.frame.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

    protected static Log log = LogFactory.getLog(DateUtils.class);

    public static String getFormatDate(Date dateTime) {
        String formaStr = "";
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = dfs.parse(dfs.format(getNowDateTime()));
            Date temp = dfs.parse(dfs.format(dateTime));
            long between = (now.getTime() - temp.getTime()) / 1000;// 除以1000是为了转换成秒

            long dayDiff = between / (24 * 3600);
            long monthDiff = between / (24 * 3600 * 30);
            long hourDiff = between % (24 * 3600) / 3600;
            long minuteDiff = between % 3600 / 60;
            // long secondDiff = between%60/60;
            long yearDiff = between / (24 * 3600 * 30 * 10);
            if ((dayDiff == 0) && (monthDiff == 0) && (hourDiff == 0) && (minuteDiff == 0) && (between != 0)) {
                formaStr = String.valueOf(minuteDiff) + "秒钟前";
            } else if ((dayDiff == 0) && (monthDiff == 0) && (hourDiff == 0) && (minuteDiff != 0)) {
                formaStr = String.valueOf(minuteDiff) + "分钟前";
            } else if ((dayDiff == 0) && (monthDiff == 0) && (hourDiff != 0)) {
                formaStr = String.valueOf(hourDiff) + "小时前";
            } else if ((dayDiff != 0) && (monthDiff != 0) && (monthDiff < 12)) {
                formaStr = String.valueOf(monthDiff) + "个月前";
            } else if ((monthDiff == 0) && (dayDiff != 0)) {
                formaStr = String.valueOf(dayDiff) + "天前";
            } else if (monthDiff >= 12) {
                formaStr = String.valueOf(yearDiff) + "年前";
            } else {
                formaStr = "当前";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return formaStr;
    }

    public static String getFormatDiffDate(Date dateTime) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
        String str = dfs.format(dateTime);
        try {
            SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String nowStr = dfs.format(now) + " 23:59:59";
            String dateStr = dfs.format(dateTime) + " 23:59:59";
            long between = (sdfs.parse(dateStr).getTime() - sdfs.parse(nowStr).getTime()) / 1000;// 除以1000是为了转换成秒
            long dayDiff = between / (24 * 3600);
            if (dayDiff > 0) {
                str += "(还剩" + dayDiff + "天)";
            } else if (dayDiff == 0) {
                str += "(今天)";
            } else if (dayDiff < 0) {
                str += "(超期" + (-dayDiff) + "天)";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return str;
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * java计算两个时间相差（天、小时、分钟、秒）
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return
     */
    public static String getDiffDate(Timestamp begin, Timestamp end) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数    
        long nh = 1000 * 60 * 60;// 一小时的毫秒数    
        long nm = 1000 * 60;// 一分钟的毫秒数    
        long ns = 1000;// 一秒钟的毫秒数    
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异    
        diff = end.getTime() - begin.getTime();
        day = diff / nd;// 计算差多少天    
        hour = diff % nd / nh + day * 24;// 计算差多少小时    
        min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟    
        sec = diff % nd % nh % nm / ns;// 计算差多少秒    
        StringBuffer result = new StringBuffer();
        if (day != 0) {
            result.append(day).append('天');
        }
        if (hour != 0) {
            result.append(hour).append("小时");
        }
        if (min != 0) {
            result.append(min).append("分钟");
        }
        if (sec != 0) {
            result.append(sec).append('秒');
        }
        return result.toString();
    }

    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dfs.parse("2014-12-27");
        System.out.println(getFormatDiffDate(date));
    }

    /**
     * 取得当前日期(yyyy-MM-dd HH:mm:ss)
     *
     * @return Date
     * @throws ParseException
     */
    public static Date getNowDateTime() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime = formatter.parse(getDayTime());
        return dateTime;
    }

    /**
     * 获取当前时间 HH一定要大写，小写的话，变成12小时日期制 format: yyyy-MM-dd HH:mm:ss
     *
     * @return String 格式:yyyy-MM-dd HH:mm:ss
     */
    public static String getDayTime() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setLenient(false);
        String datetime = format.format(calender.getTime());
        return datetime;
    }

    /**
     * 时间格式转换
     *
     * @param time
     * @param dateFormat
     * @return
     */
    public static String changeDateStr(Timestamp time, String dateFormat) {
        if (dateFormat == null || "".equals(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        format.setLenient(false);
        Date d = new Date();
        d.setTime(time.getTime());
        String datetime = format.format(d);
        return datetime;
    }

    public static String formatSimpleDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String formatSimpleTimestamp(Timestamp timestamp) {
        return formatSimpleTimestamp(timestamp,"yyyy-MM-dd");
    }
    public static String formatSimpleTimestamp(Timestamp timestamp,String formatStr) {
        if (timestamp == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);//yyyy-MM-dd HH:mm:ss
        Date date = new Date(timestamp.getTime());
        return format.format(date);
    }
    /**
     * @param time
     * @param today
     * @return today.compareTo(time + 1天)
     */
    public static int checkTimeNextDay(Timestamp time, Date today) throws ParseException {
        if (today == null) today = new Date();
        today = DateUtils.parseSimpleDateStr(DateUtils.formatSimpleDate(today));
        Date valday = new Date(time.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(valday);
        cal.add(Calendar.DATE, 1);
        Date vday = cal.getTime();
        return today.compareTo(vday);
    }

    public static Date parseSimpleDateStr(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(dateStr);
    }

    public static Timestamp parse(String dateStr, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Long date = null;
        try {
            date = format.parse(dateStr).getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return new Timestamp(date);
    }

    public static Timestamp addTimestampDay(Timestamp tsm, int d) {
        Date date = new Date();
        date.setTime(tsm.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, d);
        Date vday = cal.getTime();
        return new Timestamp(vday.getTime());
    }

    public static Date addDay(Date date, int d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, d);
        return cal.getTime();
    }

    public static Long getDiffDay(Timestamp dateTime) {
        long dayDiff = 0;
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
        try {
            SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String nowStr = dfs.format(now) + " 23:59:59";
            String dateStr = dfs.format(dateTime) + " 23:59:59";
            long between = (sdfs.parse(dateStr).getTime() - sdfs.parse(nowStr).getTime()) / 1000;// 除以1000是为了转换成秒
            dayDiff = between / (24 * 3600);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return dayDiff;
    }


    public static Date add(Date date, int field, int value) {
        Calendar calendar = calendar(date);
        calendar.add(field, value);
        return calendar.getTime();
    }

    private static Calendar calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date truncateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date strToDate(String strInput, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(strInput);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param date
     * @param dayOfWeek @See Calendar.DAY_OF_WEEK
     * @return
     */
    public static boolean isDayOfWeek(Date date, int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
    }

    /**
     * 根据年份 月份获得 月份第一天跟最后一天
     *
     * @param year
     * @param month
     */
    public static List<Timestamp> getFirstLastDays(Integer year, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, cal.getMinimum(Calendar.DATE), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp firstDate = new Timestamp(cal.getTimeInMillis());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Timestamp lastDate = new Timestamp(cal.getTimeInMillis());
        List dates = new ArrayList();
        dates.add(firstDate);
        dates.add(lastDate);
        return dates;
    }

    /**
     * 获取当前上个月的第一天
     * @param date
     * @return
     */
    public static Timestamp getFirstDayOfLastMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH,-1);
        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * 校验时间字符串格式是否符合要求
     * @param timeStr
     * @param pattern
     * @return
     */
    public static Boolean validateTimeStrFormat(String timeStr,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            sdf.parse(timeStr);
            return true;
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取指定间隔的开始结束时间区间
     *
     * @param intervalMinutes
     */
    public static List<Timestamp> getIntervalStartEndTime(Timestamp timestamp, Integer intervalMinutes) {
        Calendar calendar=Calendar.getInstance();
        if (timestamp != null){
            try {
                String formatStr = "yyyy-MM-dd HH:mm:ss.SSS";
                calendar.setTime(new SimpleDateFormat(formatStr).parse(formatSimpleTimestamp(timestamp,formatStr)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        int minute = calendar.get(Calendar.MINUTE);
        minute = (minute / intervalMinutes) * intervalMinutes;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -intervalMinutes);
        Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

        calendar.add(Calendar.MINUTE, +intervalMinutes);
        calendar.add(Calendar.MILLISECOND, -1);
        Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

        List dates = new ArrayList();
        dates.add(startTime);
        dates.add(endTime);
        return dates;
    }

    public static List<Timestamp> getIntervalStartEndTime(Integer intervalMinutes) {
        return getIntervalStartEndTime(null, intervalMinutes);
    }


}