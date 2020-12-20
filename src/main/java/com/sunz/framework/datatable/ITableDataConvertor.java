package com.sunz.framework.datatable;

import com.sunz.framework.core.dbsupport.TableColumnInfo;

/**
 * 数据表[参数]数据转换接口，允许自定义转换常见数据类型以外的数据类型，甚至根据不同表字段进行特殊转换
 * 
 * @author Xingzhe
 *
 */
public interface ITableDataConvertor {
	
	/***
	 * 获取可转换的数据类型
	 * 
	 * @return
	 */
	int getAbilityType();
	
	/**
	 * （尝试)转换（到目标类型）
	 * 
	 * @param origValue 原值，最可能的取值为String、String[]--从前台传递过来，其它要看MappedSupport实现及显式对IDatatableService的调用
	 * @param cInfo 字段定义
	 * @param tableName 表名
	 * @param refHandled 传入true，若当前转换器无法处理，须置为false
	 * @param refType 传入字段本身的数据类型SqlType，若返回类型与此不一致，须设置此参数为相应类型
	 * @return
	 */
	Object convert(Object origValue,TableColumnInfo cInfo,String tableName,Boolean refHandled,Integer refType);
}
