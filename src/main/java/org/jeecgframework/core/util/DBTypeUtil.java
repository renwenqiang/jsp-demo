package org.jeecgframework.core.util;

import com.sunz.framework.core.Config;

/**
 * 
 * @author  jeecg
 *
 */
public class DBTypeUtil {
	
	private static String dbType=get();
	private static String get(){
		String dbdialect = Config.get("hibernate.dialect");
		if (dbdialect.equals("org.hibernate.dialect.MySQLDialect")) {
			return "mysql";
		}else if (dbdialect.contains("Oracle")) {//oracle有多个版本的方言
			return  "oracle";
		}else if (dbdialect.equals("org.hibernate.dialect.SQLServerDialect")) {
			return  "sqlserver";
		}else if (dbdialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
			return  "postgres";
		}
		return "unknown";
	}
	/**
	 * 获取数据库类型
	 * @return
	 */
	public static String getDBType(){
		return dbType;
	}
}
