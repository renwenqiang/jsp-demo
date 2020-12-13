package com.sunz.framework.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 	日期帮助类
 * 
 * @author Xingzhe
 *
 */
public class DateUtil {
	
	private static Pattern patternDate = Pattern.compile("[0-9]+");
	/**
	 * 	将字符串解析为日期
	 * --2020-10-13 使用全参数构造函数获得了最好的性能；
	 * 		缺点是构造函数被标记为弃用（可使用Calendar c=Calendar.getInstance();c.set(parts[0],parts[1]-1,parts[2],parts[3],parts[4],parts[5]);）
	 * 			和不支持无分隔符格式
	 * 
	 * @param dateTime
	 * @return
	 */
	public static Date parseDate(String dateTime) {
		Matcher m=patternDate.matcher(dateTime);
		int[] parts=new int[6];
		for(int i=0;m.find();i++) {
			parts[i]=Integer.parseInt(m.group(0));
		}
		return new Date(parts[0]-1900,parts[1]-1,parts[2],parts[3],parts[4],parts[5]);		
	}
}
