package pro.wuan.common.core.utils;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author zf:
 * @version 创建时间：2018年11月1日 下午3:08:44 类说明 :处理时间工具类
 */
public class DateUtil {
    private static final DateTimeFormatter sdfYear = DateTimeFormatter.ofPattern("yyyy");
    private final static DateTimeFormatter sdfDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter sdfDays = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final static DateTimeFormatter sdfTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

    /**
     * 获取当前日期是星期几
     **/
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取当年年份，字符串形式 "2016"
     **/
    public static String getYear() {
        return sdfYear.format(LocalDateTime.now());
    }

    /**
     * 获取当前年月日,字符串形式 "2016-12-05"
     **/
    public static String getDay() {
        return sdfDay.format(LocalDateTime.now());
    }

    /**
     * 获取当前年月日 ,字符串形式 "20161205"
     **/
    public static String getDays() {
        return sdfDays.format(LocalDateTime.now());
    }

    /**
     * 获取当前年月日时分秒,字符串形式 "2016-12-05 17:10:38"
     **/
    public static String getTime() {
        return sdfTime.format(LocalDateTime.now());
    }

    /**
     * 把输入的日期格式的时间转换为字符串型 "2016-12-05 17:10:38"
     **/
    public static String toStringTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 把输入的日期格式的时间转换为字符串型 "2016-12-05"
     **/
    public static String toStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 比较两个字符串类型的日期数据，如果前者比后者大，返回true
     **/
    public static boolean compareDate(String s, String e) {
        if (fomatDate(s) == null || fomatDate(e) == null) {
            return false;
        }
        return fomatDate(s).getTime() >= fomatDate(e).getTime();
    }

    /**
     * 字符串形式的日期数据，转换为date形式"2016-12-05">>>>Mon Dec 05 00:00:00 CST 2016
     **/
    public static Date fomatDate(String date) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断输入的字符串形式的日期，转换后是否真的是有效的日期
     **/
    public static boolean isValidDate(String s) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fmt.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取两个字符串形式的日期之间间隔年数 2015-12-04 2016-12-04
     **/
    public static int getDiffYear(String startTime, String endTime) {
        DateFormat fmt = new SimpleDateFormat("yyyy");
        try {
            int years = (int) (((fmt.parse(endTime).getTime() - fmt.parse(startTime).getTime()) / (1000 * 60 * 60 * 24))
                    / 365);
            return years;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取两个字符串形式的日期之间间隔天数 2015-12-04 2016-12-06
     **/
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 12.当前日期加上传进来的字符日期得到 2016-12-28 08:28:51
     **/
    public static String getAfterDayDate(String days) {
        int daysInt = Integer.parseInt(days);
        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.DATE, daysInt);
        Date date = canlendar.getTime();
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdfd.format(date);
        return dateStr;
    }

    /**
     * 获取多少天后是星期几
     **/
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);
        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.DATE, daysInt);
        Date date = canlendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 传进来的日期格式时间转为字符串格�?2016-12-06 08:38:54
     **/
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(date);
    }


    /**
     * 2016-12-06 08:38:54 转 2016-12-06
     **/
    public static Date dateTimeToDate(Date dateTime) throws ParseException {
        return newFomatDate(dateToStr(dateTime,"yyyy-MM-dd"),"yyyy-MM-dd");
    }


    /**
     * "yyyy-MM-dd"字符串型的日期格式数据加HH:mm:ss",再转化成Date
     **/
    public static Date fomatDateAddDay(String date) {
        if ("".equals(date) || date == null) {
            return null;
        }
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return fmt.parse(date + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串形式的日期数据，转换为date�?"2016-12-05">>>>Mon Dec 05 00:00:00 CST 2016
     **/
    public static Date string2Date(String date) {
        if ("".equals(date) || date == null) {
            return null;
        }
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取前三个月
     **/
    public static String getThreeMonth() {
        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime toDate = ldt.plus(-3, ChronoUnit.MONTHS);
        String mon3 = sdfDay.format(toDate);
        return mon3;
    }

    /**
     * 获取两个日期之间的所有日期
     **/
    public static List<Date> getBetweenDates(Date begin, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(begin);
        while (begin.getTime() <= end.getTime()) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            begin = tempStart.getTime();
        }
        return result;
    }

    /**
     * 根据起始日期和结束日期获取每一天日期
     **/
    public static List<String> getDayList(String beginTime, String endTime) {
        DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
        List<String> list = null;
        try {
            Calendar startDay = Calendar.getInstance();
            Calendar endDay = Calendar.getInstance();
            startDay.setTime(FORMATTER.parse(beginTime));
            endDay.setTime(FORMATTER.parse(endTime));
            // 给出的日期开始日比终了日大则不执行打印
            list = new ArrayList<String>();
            list.add(beginTime);
            if (!beginTime.equals(endTime)) {
                if (startDay.compareTo(endDay) <= 0) {
                    //现在打印中的日期
                    Calendar currentPrintDay = startDay;
                    while (true) {
                        // 日期加一
                        currentPrintDay.add(Calendar.DATE, 1);
                        // 日期加一后判断是否达到终了日，达到则终止打印
                        if (currentPrintDay.compareTo(endDay) == 0) {
                            break;
                        }
                        list.add(FORMATTER.format(currentPrintDay.getTime()));
                    }
                    list.add(endTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取传进来日期前N天日期
     **/
    public static Date getDateBefore(Date d, int day) {
        Calendar no = Calendar.getInstance();
        no.setTime(d);
        no.set(Calendar.DATE, no.get(Calendar.DATE) - day);
        return no.getTime();
    }

    /**
     * 获取传进来日期后N天日期
     **/
    public static Date getDateAfter(Date d, int day) {
        Calendar no = Calendar.getInstance();
        no.setTime(d);
        no.set(Calendar.DATE, no.get(Calendar.DATE) + day);
        return no.getTime();
    }

    /**
     * 获取一段时间内,有多少组周
     **/
    public static Map<String, List<String>> getWeekTeam(Date startDate, Date endDate) {
        Map<String, List<String>> week = new HashMap<>(12);
        List<String> weekTeam = new ArrayList<>();
        List<String> weekTeam2 = new ArrayList<>();
        List<Date> dates = getBetweenDates(startDate, endDate);
        String sunday = "星期日";
        for (int i = 0; i < dates.size(); i++) {
            if (getWeekOfDate(dates.get(i)).equals(sunday)) {
                // 开始
                weekTeam2.add(DateUtil.toStringDate(getDateBefore(dates.get(i), 6)));
                // 结束
                weekTeam.add(DateUtil.toStringDate(dates.get(i)));
            }
        }
        // 如果结束的日期不是星期日,那就得再加一周
        if (!getWeekOfDate(endDate).equals(sunday) && (weekTeam.size() != 0)) {
            Date date = string2Date(weekTeam.get(weekTeam.size() - 1));
            weekTeam.add(toStringDate(getDateBefore(date, -7)));
            Date date2 = string2Date(weekTeam2.get(weekTeam2.size() - 1));
            weekTeam2.add(toStringDate(getDateBefore(date2, -7)));
        } else {
            int size = 8;
            for (int i = 1; i < size; i++) {
                if (getWeekOfDate(getDateBefore(endDate, -i)).equals(sunday)) {
                    weekTeam2.add(toStringDate(getDateBefore(getDateBefore(endDate, -i), 7)));
                    weekTeam.add(toStringDate(getDateBefore(endDate, -i)));
                }
            }
        }
        week.put("mondayList", weekTeam2);
        week.put("sundayList", weekTeam);
        return week;
    }

    /**
     * 获取当前年所有月份
     **/
    public static List<String> getMonthInYear() {
        List<String> monthList = new ArrayList<>();
        monthList.add(getYear() + "-01");
        monthList.add(getYear() + "-02");
        monthList.add(getYear() + "-03");
        monthList.add(getYear() + "-04");
        monthList.add(getYear() + "-05");
        monthList.add(getYear() + "-06");
        monthList.add(getYear() + "-07");
        monthList.add(getYear() + "-08");
        monthList.add(getYear() + "-09");
        monthList.add(getYear() + "-10");
        monthList.add(getYear() + "-11");
        monthList.add(getYear() + "-12");
        return monthList;
    }

    /**
     * 获取当前月的第一天
     **/
    public static String getFirstDateInMonth() {

        LocalDate currentDay = LocalDate.now();
        currentDay.with(TemporalAdjusters.firstDayOfMonth());

        String firstDay = sdfDay.format(currentDay);
        return firstDay;
    }

    /**
     * 获取当前月的最后一天
     **/
    public static String getLastDateInMonth() {
        LocalDate currentDay = LocalDate.now();
        currentDay.with(TemporalAdjusters.lastDayOfMonth());
        String lastDay = sdfDay.format(currentDay);
        return lastDay;
    }

    /**
     * 获取当前日期所在周的周一和周日
     **/
    public static Map<String, String> getWeekDate() {
        Map<String, String> map = new HashMap(12);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            dayWeek = 8;
        }
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);
        cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        map.put("mondayDate", weekBegin);
        map.put("sundayDate", weekEnd);
        return map;
    }

    /**
     * 获取当前月日格式 7月5日
     **/
    public static String getMonthDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        return month + "月" + day + "日";
    }

    /**
     * 获取当前年月日格式 2019年7月5日
     **/
    public static String getYearMonthDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return year + "年" + month + "月" + day + "日";
    }


    /**
     * 自定义时间格式Date转String
     **/
    public static String dateToStr(Date date, String formatStr) {
        if (formatStr == null || "".equals(formatStr)) {
            formatStr = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 自定义时间格式String转Date
     **/
    public static Date strToDate(String dateString, String formatStr) {
        if (formatStr == null || "".equals(formatStr)) {
            formatStr = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat formatter = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *      * 判断时间是否在时间段内
     *      * @param nowTime
     *      * @param beginTime
     *      * @param endTime
     *      * @return
     *      
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //设置当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        //设置开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        //设置结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        //处于开始时间之后，和结束时间之前的判断
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串形式的日期数据，转换为date形式"2016-12-05">>>>Mon Dec 05 00:00:00 CST 2016
     **/
    public static Date newFomatDate(String date, String fomat) {
        DateFormat fmt = new SimpleDateFormat(fomat);
        try {
            return fmt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 比较两个字符串类型的日期数据，如果前者比后者大，返回true
     **/
    public static boolean newCompareDate(String s, String e, String fomat) {
        if (newFomatDate(s, fomat) == null || newFomatDate(e, fomat) == null) {
            return false;
        }
        return newFomatDate(s, fomat).getTime() >= newFomatDate(e, fomat).getTime();
    }

    /**
     * 获取某年某月的第一天
     */
    public static String getFisrtDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取某年某月的最后一天
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }


    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date 日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    public static void main(String[] args) {
         dateToStr(new Date(),"yyyy-MM-dd");

        System.out.println(newFomatDate(dateToStr(new Date(),"yyyy-MM-dd"),"yyyy-MM-dd"));

    }



}
