package com.cn7782.management.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cn7782.management.android.activity.bean.CustomDate;

public class DateUtil {

	public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五","周六" };

	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}
	
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static int getCurrentMonthDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}
	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	public static CustomDate getNextSunday() {
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDay()+1);
		CustomDate date = new CustomDate(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH )+1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;
	}
	//根据年月日获取第几周
	public static int getWeekFromDate(int year, int month, int day){
		Calendar c = Calendar.getInstance();
		c.setTime(getDateFromString2(year, month,day));
		int week_index = c.get(Calendar.DAY_OF_WEEK);
		
		return week_index;
		
	}

	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(int year, int month) {
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-01";
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}
	public static Date getDateFromString2(int year, int month ,int day) {
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-"+day;
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}
	public static boolean isToday(CustomDate date){
		return(date.year == DateUtil.getYear() &&
				date.month == DateUtil.getMonth() 
				&& date.day == DateUtil.getCurrentMonthDay());
	}
	
	public static boolean isCurrentMonth(CustomDate date){
		return(date.year == DateUtil.getYear() &&
				date.month == DateUtil.getMonth());
	}
	//获取滑动后与滑动前对应的日期（某日），不满足则往前推进，如当前选中31日，上（下）月没有则默认选中30日（30日没有则29）
	public static int getCurrentSameDay(int year,int month,int preDay){
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days>preDay?preDay:days;
	}
	 public static String[] getFirstLastdayMonth(Date date) {
		 
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        calendar.add(Calendar.MONTH, -1);
	        Date theDate = calendar.getTime();
	        
	        //上个月第一天
	        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
	        gcLast.setTime(theDate);
	        gcLast.set(Calendar.DAY_OF_MONTH, 1);
	        String day_first = df.format(gcLast.getTime());
	        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
	        day_first = str.toString();

	        //上个月最后一天
	        calendar.add(Calendar.MONTH, 1);    //加一个月
	        calendar.set(Calendar.DATE, 1);        //设置为该月第一天
	        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
	        String day_last = df.format(calendar.getTime());
	        StringBuffer endStr = new StringBuffer().append(day_last).append(" 23:59:59");
	        day_last = endStr.toString();

	        String[] days = new String[2];
	        days[0] = day_first;
	        days[1] = day_last;
	        return days;
	    }
}

