package com.sunz.framework.core.util;

/**
 * 分页帮助接口
 * 
 * @author Xingzhe
 *
 */
public interface IPagingHelper {

	/**
	 * 获取总数sql语句
	 * 
	 * @param sql
	 * @return
	 */
	String getCountSql(String sql);
	
	/**
	 * 获取分页sql语句
	 * @param sql
	 * @param start
	 * @param limit
	 * @return
	 */
	String getPagingSql(String sql, int start,int limit);
}
