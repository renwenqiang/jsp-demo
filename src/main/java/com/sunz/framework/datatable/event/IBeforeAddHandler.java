package com.sunz.framework.datatable.event;

import java.util.Map;

/**
 * 使用datatable添加数据【前】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IBeforeAddHandler extends IOrder{

	/**
	 * 
	 * @param tableName		表名
	 * @param fieldValues	参数
	 */
	void onBeforeAdd(String tableName, Map<String, Object> fieldValues);

}