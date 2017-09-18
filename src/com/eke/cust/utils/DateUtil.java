package com.eke.cust.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static long minute = 1000 * 60;
	private static long hour = minute * 60;
	private static long day = hour * 24;
	private static long halfamonth = day * 15;
	private static long month = day * 30;
	private static SimpleDateFormat sf = null;
	public static String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy年MM月dd日");
		return sf.format(d);
	}
	
	public static String getDateToString(long time) {
		if(time == 0) {
			return "";
		}
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}
	public static String getYYMMDD(long time) {
		if(time == 0) {
			return "";
		}
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy.MM.dd");
		return sf.format(d);
	}

	public static String getScheduledateToString(long time) {
		if(time == 0) {
			return "";
		}
		Date d = new Date(time);
		sf = new SimpleDateFormat("MM-dd HH:mm");
		return sf.format(d);
	}
	
	public static String getDateToString1(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(d);
	}
	
	public static String getDateToString3(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy年MM月dd日");
		return sf.format(d);
	}
	
	public static String getDateToString2(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("HH 时 mm 分 ss 秒");
		return sf.format(d);
	}
	
	public static long getStringToDate(String time) {
		sf = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = new Date();
		try{
			date = sf.parse(time);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}


	public  static String getDateDiff(long dateTimeStamp){
		String result;
		long now = new Date().getTime();
		long diffValue = now - dateTimeStamp;
		if(diffValue < 0){
		}
		long monthC =diffValue/month;
		long weekC =diffValue/(7*day);
		long dayC =diffValue/day;
		long hourC =diffValue/hour;
		long minC =diffValue/minute;
		if(monthC>=1||weekC>=1){
			result="一周前更新";
			return result;
		}
//		 if(weekC>=1){
//			result="一周前更新" ;
//			return result;
//		}
		else if(dayC>=1){
			result=Integer.parseInt(dayC+"") +"天前";
			return result;
		}
//		else if(hourC>=1){
//			result=Integer.parseInt(hourC+"") +"个小时前更新";
//			return result;
//		}
//		else if(minC>=1){
//			result="更新时间:"+ Integer.parseInt(minC+"") +"分钟前";
//			return result;
//		}
		else{
			result="刚刚更新";
			return result;
		}
	}
	public  static String getUpdateDate(long dateTimeStamp){
		String result;
		long now = new Date().getTime();
		long diffValue = now - dateTimeStamp;
		if(diffValue < 0){
		}
		long monthC =diffValue/month;
		long weekC =diffValue/(7*day);
		long dayC =diffValue/day;
		long hourC =diffValue/hour;
		long minC =diffValue/minute;
		if(monthC>=1||weekC>=1){
			result="一周前更新";
			return result;
		}
		else if(dayC>=1){
			result=Integer.parseInt(dayC+"") +"天前更新";
			return result;
		}
//		else if(hourC>=1){
//			result=Integer.parseInt(hourC+"") +"个小时前更新";
//			return result;
//		}
//		else if(minC>=1){
//			result= Integer.parseInt(minC+"") +"分钟前更新";
//			return result;
//		}
		else{
			result="刚刚";
			return result;
		}
	}
}
