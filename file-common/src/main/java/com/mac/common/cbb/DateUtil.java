package com.mac.common.cbb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 获取系统当前时间
     *
     * @return 系统当前时间
     */
    public static String getCurrentTime() {
        Date date = new Date();
        String stringDate = String.format("%tF %<tT", date);
        return stringDate;
    }

    /**
     *
     * @param formatString 格式，如"yyyy-MM-dd"
     * @param stringDate 日期字符串，如"2000-03-19"
     * @return 日期
     * @throws ParseException 解析异常
     */
    public static Date getDateByFormatString(String formatString, String stringDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(formatString);
        Date date = dateFormat.parse(stringDate);
        return date;
    }

    /**
     * 格式化date
     *
     * @param formatString 格式，如"yyyy-MM-dd"
     * @param stringDate   日期字符串，如"2000-03-19"
     * @return SQL日期
     * @throws ParseException 解析异常
     */
    public static java.sql.Date getSqlDateByFormatString(String formatString, String stringDate) throws ParseException {
        long longtime = DateUtil.getDateByFormatString("yyyyMMdd", stringDate).getTime();
        return new java.sql.Date(longtime);
    }

    /**
     * 两个日期相差天数
     * @param preDate 第一个时间日期
     * @param afterDate 第二个时间十七
     * @return 相差的天数
     */
    public static int getDifferentDays(Date preDate, Date afterDate) {

        int preYear = getYear(preDate);
        int afterYear = getYear(afterDate);
        int preDayOfYear = getDayOfYear(preDate);
        int afterDayOfYear = getDayOfYear(afterDate);

        if (afterYear - preYear == 0){
            return afterDayOfYear - preDayOfYear;
        }
        else{
            int diffDay = 0;
            while (preYear < afterYear){
                if (diffDay == 0 && isLeapYear(preYear)) {
                    diffDay = 366 - preDayOfYear;
                }else if (diffDay == 0 && !isLeapYear(preYear)) {
                    diffDay = 365 - preDayOfYear;
                }else if (isLeapYear(preYear)){
                    diffDay += 366;
                }else{
                    diffDay += 365;
                }
                preYear++;
            }

            diffDay += afterDayOfYear;
            return diffDay;

        }


    }

    /**
     * 一年中的第几天
     * @param date 日期
     * @return 第几天
     */
    public static int getDayOfYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_YEAR);
        return day;
    }

    /**
     * 获取年份
     * jdk推荐写法，date.getYear()已被废弃
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 判断是否是闰年
     * @param year 年，如2010
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year){
        if ((year % 4 == 0 && year % 100 != 0)
                || year % 400 ==0){
            return true;
        }
        return false;
    }

}
