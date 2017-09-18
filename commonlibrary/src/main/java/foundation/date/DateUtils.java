package foundation.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import foundation.util.StringUtil;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
    //默认日期格式
    public static final String sDEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //默认日期格式
    public static final String sCHINA_DATE_FORMAT = "yyyy年MM月dd日";
    //默认时间格式
    public static final String sDEFAULT_TIME_FORMAT = "HH:mm:ss";
    //判断两条消息之间是否该显示时间戳的最小时间间隔
    private static final long INTERVAL_IN_MILLISECONDS = 15 * 60 * 1000;
    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为date
     *
     * @param strDate
     * @return
     */
    public static Date toDate(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String millisecs2DateString(long timestamp) {
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = toYYYYMMDD(timestamp);
            //return s.replace(" ", "");
            return s;
        } else {
            return getDate(new Date(timestamp));
        }
    }

    /**
     * yyyy-MM-dd T HH:mm:ss字符串转换为date
     *
     * @param strDate
     * @return
     */
    public static Date toTDate(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return sf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 dd
     *
     * @param strDate
     * @return
     */
    public static String toTMMDDHHMM(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("MM/dd HH:mm");
        return sf.format(toTDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 dd
     *
     * @param strDate
     * @return
     */
    public static String toDD(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("dd");
        return sf.format(toDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 HH:mm
     *
     * @param strDate
     * @return
     */
    public static String toHHMM(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(toDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 HH:mm
     *
     * @param strDate
     * @return
     */
    public static String toHHMMSS(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        return sf.format(toDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 yyyy-MM-dd HH:mm
     *
     * @param strDate
     * @return
     */
    public static String toYYYYMMDDHHMM(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(toDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 yy-MM-dd HH:mm
     *
     * @param strDate
     * @return
     */
    public static String toYYMMDDHHMM(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd HH:mm");
        return sf.format(toDate(strDate));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static String toYYYYMMDD(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(toDate(strDate));
    }

    /**
     * 转换为 HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String toHHMM(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    /**
     * 转换为 HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String toHHMMSS(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    public static String toYYYYMMDD(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    // 时间戳转字符串
    public static String getTime(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    public static String getMTime(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("MM/dd HH:mm");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }


    public static String getWithDrawalistTime(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    public static String toYYYYMMDDHHMMSS(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    public static String toYYYYMMDDHHMM(long milliseconds) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(milliseconds);
        return sf.format(date);
    }

    public static String getTime_difference(String str_date1, String str_date2) {
        Date date1 = new Date();
        Date date2 = new Date();
        String time = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");
        try {
            date1 = format.parse(str_date1);
            date2 = format.parse(str_date2);
            long time1 = date1.getTime();
            long time2 = date2.getTime();
            long test = Math.abs(time2 - time1);
            Date result = new Date();
            result.setTime(test);

            time = timeformat.format(result);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return time;

    }

    public static String getCurrentDay(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("dd");
        String time = sf.format(date);
        return time;

    }

    public static String getCurrentMonth(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("MM");
        String time = sf.format(date);
        return time;

    }

    public static String getCurrentYear(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy");
        String time = sf.format(date);
        return time;

    }

    public static long dateDiff(String startTime, String endTime) {
        long day = 0;
        long time = 0;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            time = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            System.out.println(day + "天" + hour + "小时" + min + "分钟" + sec
                    + "秒。");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    /**
     * 获取两个日期的时间差
     */
    public static int getTimeInterval(String data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        int interval = 0;
        try {
            Date currentTime = new Date();// 获取现在的时间
            Date beginTime = dateFormat.parse(data);
            interval = (int) ((beginTime.getTime() - currentTime.getTime()) / (1000));// 时间差
            // 单位秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return interval;
    }

    /**
     * 设定显示文字
     */
    public static String getInterval(int interval) {
        String txt = null;

        long day = interval / (24 * 3600);// 天
        long hour = interval % (24 * 3600) / 3600;// 小时
        long minute = interval % 3600 / 60;// 分钟
        long second = interval % 60;// 秒

        txt = +day + "天" + hour + "小时" + minute + "分" + second + "秒";

        return txt;
    }

    /**
     * 获得当前的时间戳
     */
    public static String getCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sf.format(new Date());
        return time;

    }

    /**
     * 用于返回指定日期格式的日期增加指定天数的日期
     *
     * @param days   指定天数
     * @return 指定日期格式的日期增加指定天数的日期
     */
    public static String getFutureDay(int days) {
        String future = "";
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(getCurrentTime());
            calendar.setTime(date);
            calendar.add(Calendar.DATE, days);// 指定天数
            date = calendar.getTime();
            future = simpleDateFormat.format(date);
        } catch (Exception e) {

        }
        return future;
    }

    public static String getTime(String endTime, String startTime) {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long between = 0;
        try {
            Date begin = dfs.parse(startTime);
            Date end = dfs.parse(endTime);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        // StringBuffer sb = new StringBuffer();
        // sb.setLength(0);
        // if (day > 0) {
        // sb.append(day + "天");
        // }
        // if (hour > 0) {
        // sb.append(hour + "小时");
        // }
        // if (min > 0) {
        // sb.append(min + "分");
        // }
        // if (s > 0) {
        // sb.append(min + "秒");
        // }

        // return sb.toString();
        String str_day = "";
        if (day > 0) {

            str_day = day + "天";
        }
        String str_hour = "";
        if (hour > 0) {

            str_hour = hour + "小时";
        }
        String str_min = "";
        if (min > 0) {

            str_min = min + "分钟";
        }
        String str_s = "";
        if (s >= 0) {

            str_s = s + "秒";
        }
        return str_day + str_hour + str_min + str_s;

    }

    public static String getLotteryTime(String endTime, String startTime) {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long between = 0;
        try {
            Date begin = dfs.parse(startTime);
            Date end = dfs.parse(endTime);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        // long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min
        // * 60);
        // StringBuffer sb = new StringBuffer();
        // sb.setLength(0);
        // if (day > 0) {
        // sb.append(day + "天");
        // }
        // if (hour > 0) {
        // sb.append(hour + "小时");
        // }
        // if (min > 0) {
        // sb.append(min + "分");
        // }
        // if (s > 0) {
        // sb.append(min + "秒");
        // }

        // return sb.toString();
        String str_day = "";
        if (day > 0) {

            str_day = day + "天";
        }
        String str_hour = "";
        if (hour > 0) {

            str_hour = hour + "小时";
        }
        String str_min = "";
        if (min > 0) {

            str_min = min + "分钟";
        }

        return str_day + str_hour + str_min;

    }

    /**
     * 比较两个时间
     *
     * @param end
     * @param start
     * @return
     * @throws Exception
     */
    public static boolean DateCompare(String end, String start) {
        // 设定时间的模板
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 得到指定模范的时间
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(end);
            d2 = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 比较
        return ((d1.getTime() - d2.getTime()) / (24 * 3600 * 1000)) > 0;
    }

    public static boolean haveTimeGap(long lastTime, long time) {
        int gap = 1000 * 60 * 3;
        return time - lastTime > gap;
    }

    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(date);
    }

    public static String CaculateWeekDay(int y, int m, int d) {

        if (m == 1) m = 13;
        if (m == 2) m = 14;
        int week = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7;
        String weekstr = "";
        switch (week) {
            case 0:
                weekstr = "周一";
                break;
            case 1:
                weekstr = "周二";
                break;
            case 2:
                weekstr = "周三";
                break;
            case 3:
                weekstr = "周四";
                break;
            case 4:
                weekstr = "周五";
                break;
            case 5:
                weekstr = "周六";
                break;
            case 6:
                weekstr = "周日";
                break;
        }
        return weekstr;
    }


    /**
     * 服务端给的时间，经常会以.0结尾，所以去除之 * @param datetime * @return
     */
    public static String RemoveLastZero(String datetime) {
        if (TextUtils.isEmpty(datetime))
            return "";

        if (datetime.length() > 19)
            return datetime.substring(0, 19);
        else
            return datetime;
    }

    //-------------------------------------------------------------------------------------------------

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的时间，与当前时间相比，时间差转换为口头上的术语，如几天几小时几分几秒 * * @return
     */
    public static String convert_between(String datetime) {
        try {
            long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime).getTime();
            return convert_between((int) ((time - System.currentTimeMillis()) / 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return "未知";
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的时间，两个时间相比，时间差转换为口头上的术语，如几天几小时几分几秒 * * @return
     */
    public static String convert_between(String starttime, String endtime) {
        try {
            long ttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starttime).getTime();
            long etime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endtime).getTime();
            return convert_between((int) ((etime - ttime) / 1000));
        } catch (ParseException e) {
            e.printStackTrace();
            return "未知";
        }
    }

    /**
     * 将时长秒，转换为口头上的术语，如几天几小时几分几秒 1天：86400s 1时：3600s 1分：60s * * @param sec 相差的间隔，单位为秒 * @return
     */
    public static String convert_between(long sec) {
        if (sec < 0)
            return "时间超了";
        StringBuffer buf = new StringBuffer();
        if (sec >= 86400) {
            int day = (int) (sec / 86400);
            int hour = (int) ((sec % 86400) / 3600);
            int min = (int) ((sec % 86400 % 3600) / 60);
            int second = (int) (sec % 86400 % 3600 % 60);
            buf.append(day).append("天").append(hour).append("小时").append(min).append("分").append(second).append("秒");
        } else if (sec > 3600) {
            int hour = (int) (sec / 3600);
            int min = (int) ((sec % 3600) / 60);
            int second = (int) (sec % 3600 % 60);
            buf.append(hour).append("小时").append(min).append("分").append(second).append("秒");
        } else if (sec > 60) {
            int min = (int) (sec / 60);
            int second = (int) (sec % 60);
            buf.append(min).append("分").append(second).append("秒");
        } else {
            buf.append(sec).append("秒");
        }

        return buf.toString();
    }

    /**
     * 将时长秒，转换为几分几秒，适用于通话时长之类的，如2'30'' * @param sec * @return
     */
    public static String convert_between_len(long sec) {
        if (sec < 0)
            return String.valueOf(sec);

        StringBuffer buf = new StringBuffer();
        if (sec > 60) {
            int min = (int) (sec / 60);
            int second = (int) (sec % 60);
            buf.append(min).append("'").append(second).append("''");
        } else {
            buf.append(sec).append("''");
        }

        return buf.toString();
    }

    //-------------------------------------------------------------------------------------------------

    /**
     * 将EEE MMM dd HH:mm:ss zzz yyyy格式的时间，同当前时间相比，格式化为：xx分钟前，xx小时前和日期 * * @param datetime * @return
     */
    public static String convert_before_timezone(String datetime) {
        Log.v("info", datetime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        dateFormat.setLenient(false);
        Date created = null;
        try {
            created = dateFormat.parse(datetime);
        } catch (Exception e) {
            return "";
        }

        return convert_before(created.getTime());
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的时间，同当前时间比对，格式化为：xx分钟前，xx小时前和日期 * * @param datetime 需比对的时间 * @return
     */
    public static String convert_before(String datetime) {
        if (TextUtils.isEmpty(datetime)) {
            return "";
        }

        try {
            long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime).getTime();
            return convert_before(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 将对比后的时间，格式化为：xx分钟前，xx小时前和日期 * @param time 需比对的时间 * @return
     */
    public static String convert_before(long time) {
        if (time < 0)
            return String.valueOf(time);
        int difftime = (int) ((System.currentTimeMillis() - time) / 1000);
        if (difftime < 86400 && difftime > 0) {
            if (difftime < 3600) {
                int min = (int) (difftime / 60);
                if (min == 0)
                    return "刚刚";
                else
                    return (int) (difftime / 60) + "分钟前";
            } else {
                return (int) (difftime / 3600) + "小时前";
            }
        } else {
            Calendar now = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                return new SimpleDateFormat("HH:mm").format(c.getTime());
            }
            if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                return new SimpleDateFormat("昨天 HH:mm").format(c.getTime());
            } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                    && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                    && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 2) {
                return new SimpleDateFormat("前天 HH:mm").format(c.getTime());
            } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                return new SimpleDateFormat("M月d日 HH:mm").format(c.getTime());
            } else {
                return new SimpleDateFormat("yy年M月d日").format(c.getTime());
            }
        }
    }

    /**
     * 指定的时间，在时间条件范围内的，返回true，不在该时间范围内，返回false * * @param sDate 开始日期，yyyy-MM-dd hh:mm:ss * @param eDate 结束时间，yyyy-MM-dd hh:mm:ss * @param checkTime 检查时间，yyyy-MM-dd hh:mm:ss * @return
     */
    public static boolean timeCompare(String sDate, String eDate, String checkTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            long sTime = sdf.parse(sDate).getTime();
            long eTime = sdf.parse(eDate).getTime();
            long sec = sdf.parse(checkTime).getTime();
            if (sec > sTime && sec < eTime)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 当前时间，在时间条件范围内的，返回true，不在该时间范围内，返回false * * @param sDate 开始日期，hh:mm * @param eDate 结束时间，hh:mm * @return
     */
    public static boolean timeCompa(String sDate, String eDate) {
        try {
            long sec = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            long sTime = sdf.parse(df.format(sec) + " " + sDate).getTime();
            long eTime = sdf.parse(df.format(sec) + " " + eDate).getTime();
            if (sec > sTime && sec < eTime)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断两个时间的大小 * * @param sDate 开始日期，yyyy-MM-dd hh:mm:ss * @param eDate 结束时间，yyyy-MM-dd hh:mm:ss * @return
     */
    public static boolean timeCompare(String sDate, String eDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long sTime = sdf.parse(sDate).getTime();
            long eTime = sdf.parse(eDate).getTime();
            if (sTime > eTime)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将传入时间添加秒钟数 * * @param date 时间 * @param sec 秒数，正数为添加秒，负数是减少秒 * @return
     */
    public static String addSec(String date, int sec) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long reminTime = sdf.parse(date).getTime() + 1000 * sec;
            return sdf.format(reminTime);
        } catch (Exception e) {
            return "";
        }
    }

    public static String parseTime(String date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(date));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(c.getTime());

    }

    /**
     * 格式化取当前时间 * @return
     */
    public static String getThisDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

    /**
     * 将本地时间戳转为服务器时间戳【即转为秒级的】
     */
    public static String getServerStampByLocal() {
        long curStamp = System.currentTimeMillis();
        return String.valueOf(curStamp / 1000);
    }

    /**
     * 将格式化日期字符串转为Calendar对象
     *
     * @param dateStr 格式化日期字符串【格式："yyyy-MM-dd"】
     * @return Calendar对象
     */
    public static Calendar getCalendarByDateStr(String dateStr) {
        if (StringUtil.isEmpty(dateStr))
            dateStr = "1990-01-01";

        Calendar calendar = Calendar.getInstance();

        String time[] = dateStr.split("-");
        int year = Integer.valueOf(time[0]);
        int month = Integer.valueOf(time[1]) - 1;
        int day = Integer.valueOf(time[2]);

        calendar.set(year, month, day);
        return calendar;
    }

    /**
     * 将Date转为时间描述
     *
     * @param context     上下文
     * @param messageDate Date对象
     * @return 时间描述
     */
    public static String getTimeDescribe(Context context, java.util.Date messageDate) {
        String format;
        boolean isChinese = context.getResources().getConfiguration().locale.getCountry().equals("CN");

        long messageTime = messageDate.getTime();
        if (isSameDay(messageTime)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(messageDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            format = "HH:mm";

            if (hour > 17) {
                if (isChinese)
                    format = "晚上 hh:mm";
            } else if (hour >= 0 && hour <= 6) {
                if (isChinese)
                    format = "凌晨 hh:mm";
            } else if (hour > 11 && hour <= 17) {
                if (isChinese)
                    format = "下午 hh:mm";
            } else {
                if (isChinese)
                    format = "上午 hh:mm";
            }
        } else if (isYesterday(messageTime)) {
            if (isChinese)
                format = "昨天 HH:mm";
            else
                format = "MM-dd HH:mm";
        } else {
            if (isChinese)
                format = "M月d日 HH:mm";
            else
                format = "MM-dd HH:mm";
        }

        if (isChinese)
            return new SimpleDateFormat(format, Locale.CHINA).format(messageDate);
        else
            return new SimpleDateFormat(format, Locale.US).format(messageDate);
    }

    /**
     * 判断两条消息的时间间隔是否大于最小时间间隔，以便判断是否需要显示两消息间的间隔描述
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isCloseEnough(long time1, long time2) {
        long delta = time1 - time2;
        if (delta < 0)
            delta = -delta;
        return delta > INTERVAL_IN_MILLISECONDS;
    }

    /**
     * 判断时间是否在今天
     *
     * @param inputTime
     * @return
     */
    public static boolean isSameDay(long inputTime) {
        TimeInfo tStartAndEndTime = getTodayStartAndEndTime();
        if (inputTime > tStartAndEndTime.getStartTime() && inputTime < tStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    /**
     * 判断时间是否在昨天
     *
     * @param inputTime
     * @return
     */
    public static boolean isYesterday(long inputTime) {
        TimeInfo yStartAndEndTime = getYesterdayStartAndEndTime();
        if (inputTime > yStartAndEndTime.getStartTime() && inputTime < yStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    /**
     * 获取今天时间的始点和终点
     *
     * @return
     */
    public static TimeInfo getTodayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        java.util.Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        java.util.Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * 获取昨天时间的始点和终点
     *
     * @return
     */
    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        java.util.Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        java.util.Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * 根据小时点和分钟点获取格式化的时间String
     *
     * @param hour 小时点
     * @param min  分组点
     * @return 时间String
     */
    public static String formatTimeText(int hour, int min) {
        String format = "HH:mm";
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, hour);
        calendar1.set(Calendar.MINUTE, min);
        java.util.Date date = calendar1.getTime();
        return new SimpleDateFormat(format).format(date);
    }
}
