package com.sunz.framework.datatable.event;

/**
 * 使用datatable 删除数据【后】的事件
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
@FunctionalInterface
public interface IDeleteHandler extends IOrder{
	
	/**
	 * 
	 * @param tableName		表名
	 * @param ids
	 */
	void onDelete(String tableName, String[] ids,Integer effect);

}