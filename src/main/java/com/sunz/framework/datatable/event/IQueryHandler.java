package com.sunz.framework.datatable.event;

import java.util.List;
import java.util.Map;

/**
 * 使用datatable 查询数据【后】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IQueryHandler extends IOrder{

	/**
	 * 
	 * @param tableName		表名
	 * @param type			类型，参考DatatableEventHelper.QUERY_TYPE_? 定义
	 * @param arg			参数，getbyid为id值，其它情况为整个clause
	 * @param keepCase		保留数据库字段本身大小写，false则统一转换【为大写】
	 * @param results		结果
	 */
	void onQuery(String tableName, String type, String arg,boolean keepCase, List<Map<String, Object>> results);

}