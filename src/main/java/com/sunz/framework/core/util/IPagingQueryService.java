package com.sunz.framework.core.util;

import java.sql.ResultSet;
import java.util.Map;

import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;

/**
 * 分页查询Service
 * 	  因实际代码中会有比较多的场合需要进行分页查询
 * 
 * 注：sql格式为JdbcTemplate支持的格式：参数以问号和（冒号+参数名)形式，如要做JdbcTemplate以外的实现，请处理好此兼容问题
 * 
 * @author Xingzhe
 *
 */
public interface IPagingQueryService {
	/**
	 * 查询以List Bean 方式返回结果
	 * 
	 * @param sql 	sql语句，带参数
	 * @param param sql语句中的参数值，以Map形式
	 * @param page  分页参数
	 * @param cls  	Bean类型
	 * @return
	 */
	ListJsonResult query(String sql,Map param,PageParameter page,Class cls);
	
	/**
	 * 不指定返回List的Bean类型，即返回Map，特意区分于query(String sql,Map param,PageParameter page,Class<T> cls)
	 * 
	 * @param sql 	sql语句，带参数
	 * @param param sql语句中的参数值，以Map形式
	 * @param page  分页参数
	 * @return
	 */
	ListJsonResult query(String sql,Map param,PageParameter page);
	
	/**
	 * 仅查询记录数
	 * 
	 * @param sql	sql语句，带参数
	 * @param param	sql语句中的参数值，以Map形式
	 * @return
	 */
	int queryCount(String sql,Map param);
	
	
	/**
	 * 分页查询，结果不一次加载到内存，而是直接返回原生ResultSet--这种情况不关心total
	 * 
	 * @param sql	sql语句，带参数
	 * @param param	sql语句中的参数值，以Map形式
	 * @param start	
	 * @param limit
	 * @return
	 */
	void query(String sql,Map param,int start,int limit,IDataRowHandler rsExtractor);
}
