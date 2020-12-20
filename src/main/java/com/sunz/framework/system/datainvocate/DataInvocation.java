package com.sunz.framework.system.datainvocate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据调取
 *    概念来源：调档
 * @author Xingzhe
 *
 */
@Entity
@Table(name="T_S_DataInvocation")
public class DataInvocation  extends com.sunz.framework.core.Entity{
	/**
	 * 自定义用于使用的id
	 */
	private String key;
	/**
	 * 名称或描述
	 */
	private String description;
	/**
	 * 所指向的query
	 */
	private String queryKey;
	
	/**
	 * （结果的）主键字段，用于传递给调档存储过程
	 */
	private String idField;
	
	/**
	 * 是否允许多选
	 */
	//private boolean multiSelect;
	
	/**
	 * 调用逻辑的存储过程
	 */
	private String invocateProcedure;
	
	/**
	 * 取消时的查询
	 */
	private String uninvocateQueryKey;
	
	/**
	 * 取消时的主键字段
	 */
	private String uninvocateIdField;
	/**
	 * 取消调用逻辑的存储过程
	 */
	private String uninvocateProcedure;


	public String getDescription() {
		return description;
	}
	public String getUninvocateQueryKey() {
		return uninvocateQueryKey;
	}
	public String getUninvocateIdField() {
		return uninvocateIdField;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setUninvocateQueryKey(String uninvocateQueryKey) {
		this.uninvocateQueryKey = uninvocateQueryKey;
	}
	public void setUninvocateIdField(String uninvocateIdField) {
		this.uninvocateIdField = uninvocateIdField;
	}
	public String getIdField() {
		return idField;
	}
	/*public boolean isMultiSelect() {
		return multiSelect;
	}
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}*/
	public void setIdField(String idField) {
		this.idField = idField;
	}
	public String getKey() {
		return key;
	}
	public String getQueryKey() {
		return queryKey;
	}
	public String getInvocateProcedure() {
		return invocateProcedure;
	}
	public String getUninvocateProcedure() {
		return uninvocateProcedure;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}
	public void setInvocateProcedure(String invocateProcedure) {
		this.invocateProcedure = invocateProcedure;
	}
	public void setUninvocateProcedure(String uninvocateProcedure) {
		this.uninvocateProcedure = uninvocateProcedure;
	}
}
