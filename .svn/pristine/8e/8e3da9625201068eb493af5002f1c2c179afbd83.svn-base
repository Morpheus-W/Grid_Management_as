package com.cn7782.management.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat();

	/***
	 * 得到yyyy-MM的字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getFormatDate_yyyy_MM(Date date) {
		dateFormat.applyPattern("yyyy-MM");
		return dateFormat.format(date);
	}

	/***
	 * 得到yyyy-MM-dd的字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getFormatDate_yyyy_MM_dd(Date date) {
		dateFormat.applyPattern("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/***
	 * 得到yyyy-MM-dd HH:mm:ss的字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getFormatDate_yyyy_MM_dd_HH_mm_ss(Date date) {
		dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	/***
	 * 得到yyyy-MM-dd HH:mm的字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getFormatDate_yyyy_MM_dd_HH_mm(Date date) {
		if (date == null) {
			return "";
		}
		dateFormat.applyPattern("yyyy-MM-dd HH:mm");
		return dateFormat.format(date);
	}

	/***
	 * 得到MM-dd HH:mm的字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getFormatDate_MM_dd_HH_mm(Date date) {
		dateFormat.applyPattern("MM-dd HH:mm");
		return dateFormat.format(date);
	}

	/**
	 * 根据字串获得Date对象
	 * 
	 * @param timeStr
	 *            yyyy-MM-dd HH:mm:ss或者yyyy-MM-dd类型字串或者yyyy-MM-dd HH:mm
	 * @return
	 */
	public static Date getDate(String timeStr) {
		try {
			if (timeStr.length() == "yyyy-MM-dd HH:mm:ss".length()) {
				dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
			} else if (timeStr.length() == "yyyy-MM-dd HH:mm".length()) {
				dateFormat.applyPattern("yyyy-MM-dd HH:mm");
			} else if (timeStr.length() == "yyyy-MM-dd".length()) {
				dateFormat.applyPattern("yyyy-MM-dd");
			}
			return dateFormat.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到上班的时间戳
	 * 
	 * @param time
	 *            08:55
	 * @return
	 */
	public static long getBeginTime(String time) {
		String str = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		Date date2 = null;
		try {
			date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(str + " "
					+ time + ":00");

			date2 = new Date(date2.getTime() - 300000);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date2.getTime();
	}

	/**
	 * 得到下班的时间戳
	 * 
	 * @param time
	 *            17：55
	 * @return
	 */
	public static long getEndTime(String time) {
		String str = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		Date date2 = null;
		try {
			date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(str + " "
					+ time + ":00");

			date2 = new Date(date2.getTime() + 300000);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date2.getTime();
	}
}
