package com.sunz.framework.datatable;

import java.util.List;
import java.util.Map;

import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.dbsupport.TableColumnInfo;

/**
 * 基于Map参数的单表的增删改查操作
 * 
 * @author Xingzhe
 *
 */
public interface IDatatableService  {

	/**
	 * 获取库中所有表名
	 * 
	 * @return
	 * /
	ListJsonResult tableNames();

	/**
	 * 获取指定表的字段信息
	 * 
	 * @param tablename
	 * @return
	 * /
	ListJsonResult columnInfos(String tablename);
	*/

	/***
	 * 向表中添加一条数据
	 * 
	 * @param tablename
	 * @param request
	 * @return
	 */
	JsonResult add(String tablename, Map<String, Object> fieldValues);

	/**
	 * 更新表的指定数据
	 * 
	 * @param tablename
	 * @param request
	 * @return
	 */
	JsonResult save(String tablename,String id,Map<String, Object> fieldValues);

	/***
	 * 获取表的指定记录
	 * 
	 * @param tablename
	 * @param id
	 * @param keepCase 保留数据字段本身大小写，false则统一转换【为大写】
	 * @return
	 */
	JsonResult getById(String tablename, String id, Boolean keepCase);

	/**
	 * 删除表的指定记录
	 * 
	 * @param tablename
	 * @param id
	 * @return
	 */
	JsonResult delete(String tablename, String id);
	
	/**
	 * 通用查询
	 * 主要做了一些判断和控制
	 * 
	 * @param tablename
	 * @param clause
	 * @param order
	 * @param params
	 * @param page
	 * @param keepCase  保留数据字段本身大小写，false则统一转换【为大写】
	 * @return
	 */
	ListJsonResult query(String tablename,String clause, String order, Map params,PageParameter page,boolean keepCase);

	/**
	 * 获取新增sql语句相关参数
	 *   本为内部使用，已被项目使用，新项目最好不要使用
	 *   
	 * @param tablename
	 * @param data
	 * @param infos
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	PrepareStatementInfo getInsertStatementInfo(String tablename, Map data, List<TableColumnInfo> infos)
			throws Exception;

	/**
	 * 获取新增sql语句相关参数
	 * 		本为内部使用，已被项目使用，新项目最好不要使用
	 * @param tablename
	 * @param data
	 * @param infos
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	PrepareStatementInfo getUpdateStatementInfo(String tablename, Map data, List<TableColumnInfo> infos)
			throws Exception;
	
	/**
	 * 批量删除表的指定记录
	 * @param tableName
	 * @param ids
	 * @return
	 */
	JsonResult batchDelete(String tableName,String[] ids);
	
}