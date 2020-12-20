package com.sunz.framework.datatable.event;

import java.util.Map;

/**
 * 使用datatable 保存数据【前】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IBeforeSaveHandler extends IOrder{
	/**
	 * 
	 * @param tableName		表名
	 * @param id			id
	 * @param fieldValues	参数
	 */
	void onBeforeSave(String tableName, String id, Map<String, Object> fieldValues);

}