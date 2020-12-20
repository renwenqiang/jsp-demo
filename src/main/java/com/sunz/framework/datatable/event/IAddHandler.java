package com.sunz.framework.datatable.event;

import java.util.Map;

/**
 * 使用datatable添加数据【后】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IAddHandler extends IOrder{

	/**
	 * 
	 * @param tableName		表名
	 * @param id			新增数据时，会自动生成guid类型的id
	 * @param fieldValues	添加完后/有效参数
	 * @param rawValues		原始参数
	 */
	void onAdd(String tableName,String id, Map<String, Object> fieldValues, Map<String, Object> rawValues);

}