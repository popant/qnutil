package com.channelsoft.qnutil.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoreDateUtils {
	private static final Logger logger = LoggerFactory.getLogger(CoreDateUtils.class.getName());
	
	public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE = "yyyy-MM-dd";
	
	public static String formatDate(Date date) {
		return formatDate(date, DATE);
	}
	
	public static String formatDateTime(Date date) {
		return formatDate(date, DATETIME);
	}

	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
		return sdf.format(date);
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, DATE);
	}
	public static Date parseLongDate(String dateStr) {
		return parseDate(dateStr, DATETIME);
	}

	public static Date parseDate(String dateStr, String pattern) {
		if (dateStr == null) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error("日期转换错误, dateStr={}", dateStr);
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	/**
	 * 两个时间相隔天数 time1-time2
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long diffDays(Date time1,Date time2){
		if(time1 == null || time2 == null){
			return 0;
		}
		return (time1.getTime() - time2.getTime()) / 1000 / 60 / 60 / 24;
	}
	
	/**
	 * 增加时间天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		if (date == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
	/**
	 * 增加时间小时数
	 * @param date
	 * @param hours
	 * @return
	 */
	public static Date addHours(Date date, int hours) {
		if (date == null) {
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}
	
	/**
	 * 
	 * 增加时间天数
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonth(Date date, int month) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}
	
	
}
