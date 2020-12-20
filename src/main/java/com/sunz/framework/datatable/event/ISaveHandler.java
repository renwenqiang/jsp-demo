package com.sunz.framework.datatable.event;

import java.util.Map;

/**
 * 使用datatable 保存数据【后】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface ISaveHandler extends IOrder{

	/**
	 * 
	 * @param tableName		表名
	 * @param id
	 * @param fieldValues	添加完后/有效参数
	 * @param rawValues		原始参数
	 */
	void onSave(String tableName, String id, Map<String, Object> fieldValues, Map<String, Object> rawValues);

}