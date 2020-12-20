package com.sunz.framework.calendar;

import java.util.Date;
import java.util.List;

/**
 * 日期服务
 * 
 * @author Xingzhe
 *
 */
public interface ICalendarService {

	/**
	 * 初始化整年的日期
	 * 
	 * @param year
	 */
	void initYear(int year);
	
	/**
	 * 返回指定年的所有日期
	 * 
	 * @return
	 */
	List yearDays(int year);
	
	/**
	 * 今天 是否假期
	 * 
	 * @return
	 */
	boolean isHoliday();
	/**
	 * 指定日期是否假日
	 * 
	 * @param date
	 * @return
	 */
	boolean isHoliday(Date date);
	
	/**
	 * 指定日期（年月日）是否假日
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	boolean isHoliday(int year,int month,int day);
	
	/**
	 * 返回指定日期间的自然天数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	int dayCount(Date from,Date to);
	
	/**
	 * 返回指定日期间的节假日天数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	int holidayCount(Date from ,Date to);	
	
	/**
	 * 返回指定日期间的工作日天数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	int workdayCount(Date from ,Date to);
	
	/**
	 * 设置指定日期为假日
	 * 
	 * @param date
	 * @param remark
	 */
	void setHoliday(Date date,String remark);
	
	/**
	 * 设置指定日期（年月日）为假日
	 * @param year
	 * @param month
	 * @param day
	 * @param remark
	 */
	void setHoliday(int year,int month,int day,String remark);
	/**
	 * 设置指定日期为工作日
	 * 
	 * @param date
	 * @param remark
	 */
	void setWorkday(Date date,String remark);

	/**
	 * 设置指定日期（年月日）为工作日
	 * @param year
	 * @param month
	 * @param day
	 * @param remark
	 */
	void setWorkday(int year,int month,int day,String remark);
	
	/**
	 * 计算指定日期指定工作日天数的日期
	 * 
	 * @param date
	 * @param workdays
	 * @return
	 */
	Date dueWorkDate(Date date,int workdays);
	
	/**
	 * 计算指定日期指定自然天数的日期
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	Date dueDate(Date date,int days);
}
