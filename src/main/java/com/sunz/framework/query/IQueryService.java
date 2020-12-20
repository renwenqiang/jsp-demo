package com.sunz.framework.query;

import java.util.Map;

import org.jeecgframework.web.system.pojo.base.TSUser;

import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.query.entity.Query;

public interface IQueryService {

	/**
	 * 	根据sqlStatementid或Key获取sql语句内容
	 * 
	 * @param sqlStatementid
	 * @return
	 */
	String getSql(String sqlStatementid, String sqlkey);

	/**
	 *	 对未解析（带freemaker语法）的sql语句进行查询
	 * 		sqlkey为可选参数（若指定必须固定而唯一)，指定此参数在多次查询时可由缓存提升性能
	 * 
	 * @param freemarkerSql	带freemarker语法的sql
	 * @param mapParam		传递给sql的参数
	 * @param page			分页参数
	 * @param cls			sql结果（每条记录）所需要转换的java对象类型，仅支持简单的字段映射（bean字段名==sql字段名），默认为Map
	 * @param sqlkey		用于缓存freemarkerSql的key，缓存后再次查询大大提升性能
	 * 
	 * @return list类型为{cls}的结果信息
	 */
	ListJsonResult queryList(String freemarkerSql, Map mapParam,PageParameter page, Class cls, String sqlkey);

	/**
	 * 	 对未解析（带freemaker语法）的sql语句进行查询
	 *   	cls 为可选参数，未指定时返回Map
	 * 		sqlkey 为可选参数（若指定必须固定而唯一)，指定此参数在多次查询时可由缓存提升性能
	 * 
	 * @remark 要求sql返回有且仅有一条记录
	 * 
	 * @param freemarkerSql	带freemarker语法的sql，sql要求返回有且仅有一条记录
	 * @param mapParam		传递给sql的参数
	 * @param cls			sql结果（每条记录）所需要转换的java对象类型，仅支持简单的字段映射（bean字段名==sql字段名），默认为Map
	 * @param sqlkey		用于缓存freemarkerSql的key，缓存后再次查询大大提升性能
	 * 
	 * @return	{cls}类型的对象
	 */
	<T> T queryObject(String freemarkerSql, Map mapParam, Class<T> cls,String sqlkey);

	/**
	 * 	指定sqlStatementid或sqlkey进行查询，以返回list类型为Map的查询结果
	 * 
	 * @see //queryList(sqlStatementid, sqlkey,mapParam, page,Map.class);
	 * 
	 * @param sqlStatementid	sql定义的id
	 * @param sqlkey			sql定义的key，与sqlStatementid形成冗余
	 * @param mapParam			传递给sql的参数
	 * @param page				分页参数
	 * 
	 * @return	list类型为Map的查询结果
	 */
	ListJsonResult queryList(String sqlStatementid, String sqlkey,Map mapParam, PageParameter page);
	
	
	/**
	 * 	对指定sqlStatementid或sqlkey进行查询，以返回list类型为指定{cls}类型的查询结果
	 * 
	 * @param sqlStatementid	sql定义的id
	 * @param sqlkey			sql定义的key，与sqlStatementid形成冗余
	 * @param mapParam			传递给sql的参数
	 * @param page				分页参数
	 * @param cls				指定返回结果映射为的类型
	 * @return	list类型为{cls}的查询结果
	 */
	ListJsonResult queryList(String sqlStatementid, String sqlkey,Map mapParam, PageParameter page,Class cls);

	/**
	 * 	指定sqlStatementid或sqlkey进行查询，以返回单个结果Map
	 * 	
	 * @remark 要求sql返回有且仅有一条记录
	 * 
	 * @param sqlStatementid	sql定义的id
	 * @param sqlkey			sql定义的key，与sqlStatementid形成冗余
	 * @param mapParam			传递给sql的参数
	 * 
	 * @return	
	 */
	Map queryObject(String sqlStatementid, String sqlkey, Map mapParam);
	
	/**
	 * 	指定sqlStatementid或sqlkey进行查询，以返回单个结果Map
	 * 	
	 * @remark 要求sql返回有且仅有一条记录
	 * 
	 * @param sqlStatementid	sql定义的id
	 * @param sqlkey			sql定义的key，与sqlStatementid形成冗余
	 * @param mapParam			传递给sql的参数
	 * @param cls				[单个]结果需要映射到的java类型
	 * 
	 * @return {cls}类型实例
	 */
	<T> T queryObject(String sqlStatementid, String sqlkey, Map mapParam,Class<T> cls);

	/**
	 * 	对未解析（带freemaker语法）的sql语句进行查询总记录数
	 * 
	 * @param freemarkerSql
	 * @param mapParam
	 * @param sqlkey
	 * @return
	 */
	int queryCount(String freemarkerSql, Map mapParam,String sqlkey);
	
	/**
	 *	指定sqlStatementid或sqlkey进行查询总记录数
	 *  
	 * @param sqlStatementid
	 * @param sqlkey
	 * @param mapParam
	 * @return
	 */
	int queryCount(String sqlStatementid, String sqlkey,Map mapParam);
	
	/**
	 *	 获取指定用户[权限控制下]的查询定义
	 * 
	 * @param key
	 * @param user
	 * @return
	 */
	Query getUserQuery(String key,TSUser user);
	
	/**
	 * 	执行一个不返回结果集类型的sql语句，如insert、update
	 * 
	 * @param sqlStatementid
	 * @param sqlkey
	 * @param mapParam
	 * @return
	 */
	int update(String sqlStatementid, String sqlkey,Map mapParam);
	
	//int[] batchUpdate(String sqlStatementid, String sqlkey,Map mapParam);
}