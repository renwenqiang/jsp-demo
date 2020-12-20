package com.sunz.framework.core.util;

import java.util.List;

/**
 * 不同数据库存储过程辅助接口
 * 
 *    预留[未完成]接口，后续可能需要补充相关存储过程调用的方法
 * 
 * @author Xingzhe
 *
 */
public interface IProcedureHelper {
	/**
	 * 
	 * @param procName
	 * @param procDeclare
	 * @return List<Object[{parameterName:String,parameterType:String,isOut:boolean}]>
	 */
	List parseParameter(String procName,String procDeclare);
	
	/**
	 * 保留：
	 * 	     调用存储过程sql语句
	 * @param procName
	 * @return
	 */
	//String getCallSql(String procName);
}
