package com.sunz.framework.core.util;

import java.util.List;

/**
 * 数据库元数据获取
 * 
 * @author Xingzhe
 *
 */
public interface IDbMetaHelper {

	/**
	 * 获取对象【起始】限定符（比如oracle为双引号，mysql为·，sqlserver为[）
	 * 
	 * @return
	 */
	String getObjectStartQualifier();
	/**
	 * 获取对象【结束】限定符（比如oracle为双引号，mysql为·，sqlserver为]）
	 * 
	 * @return
	 */
	String getObjectEndQualifier();
	/**
	 * 获取所有表名 列表
	 * 
	 * @return
	 */
	List getTableNames();
	/**
	 * 获取所有视图名 列表
	 * 
	 * @return
	 */
	List getViewNames();
	/**
	 * 获取所有存储过程名 列表
	 * 
	 * @return
	 */
	List getProcedureNames();
	/**
	 * 获取所有函数名 列表
	 * 
	 * @return
	 */
	List getFunctionNames();
	/**
	 * 获取所有触发器名 列表
	 * 
	 * @return
	 */
	List getTriggerNames();
	
	/**
	 * 获取表字段定义
	 * 
	 * @param tableName
	 * @return
	 */
	List getTableColumnInfos(String tableName);
	
	/**
	 * 获取指定数据类型所对应的jdbcType(java.sql.Types)
	 * 
	 * @param rawtype
	 * @return
	 */
	int getCommonDatatype(String rawtype);

	/**
	 * 获取存储过程定义
	 * 
	 * @param proceName
	 * @return
	 */
	String getProcedureDeclare(String proceName);


	/**
	 * 获取视图定义
	 * 
	 * @param viewName
	 * @return
	 */
	String getViewDeclare(String viewName);
	
	/**
	 * 获取函数定义
	 * 
	 * @param funName
	 * @return
	 */
	String getFunctionDeclare(String funName);

	/**
	 * 获取触发器定义
	 * 
	 * @param triggerName
	 * @return
	 */
	String getTriggerDeclare(String triggerName);
	
	/**
	 * 存储过程/函数/视图等 是否存在
	 * 
	 * @param objName
	 * @return
	 */
	boolean exists(String objName);
}