package com.sunz.framework.datatable;

import com.sunz.framework.core.Entity;
import com.sunz.framework.core.JsonResult;

/**
 * 作为IDatatableService接口的扩展，支持直接对Entity进行操作
 * 	@remark Entity必须有Table（@see javax.persistence.Table）注解
 * 			字段名支持下划线去除（如field_映射为field，@see com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher::toNamedParameterMap）以及javax.persistence.Column注解，且注解优先
 * 
 * @author Xingzhe
 *
 */
public interface IDatatableServiceEx {

	/**
	 * 像Hibernate一样添加bean
	 * @param entity 打了Table（javax.persistence.Table）注解的对象
	 * @return
	 */
	JsonResult add(Entity entity);

	/**
	 * 像Hibernate一样保存bean
	 * @param entity 打了Table（javax.persistence.Table）注解的对象
	 * @return
	 */
	JsonResult save(Entity entity);

}