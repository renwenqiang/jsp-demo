package com.sunz.framework.datatable.event;

/**
 * 使用datatable 查询数据【前】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IBeforeQueryHandler extends IOrder{
	/**
	 * 
	 * @param tableName		表名
	 * @param type			类型，参考DatatableEventHelper.QUERY_TYPE_? 定义
	 * @param arg			参数，getbyid为id值，其它情况为整个clause
	 * @param keepCase		保留数据库字段本身大小写，false则统一转换【为大写】
	 */
	void onBeforeQuery(String tableName, String type, String arg, boolean keepCase);
	

}