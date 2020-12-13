package org.jeecgframework.web.system.pojo.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jeecgframework.core.common.entity.IdEntity;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * : Entity
 *  数据日志
 * @author onlineGenerator
 *  2015-06-28 14:02:26
 * @version V1.0
 *
 */
@Entity
@Table(name = "t_s_data_log", schema = "")
@SuppressWarnings("serial")
public class TSDatalogEntity extends IdEntity implements java.io.Serializable {
	/**所属部门*/
	private java.lang.String sysOrgCode;
	/**所属公司*/
	private java.lang.String sysCompanyCode;
	/**表名*/
	@Excel(name="表名")
	private java.lang.String tableName;
	/**数据ID*/
	@Excel(name="数据ID")
	private java.lang.String dataId;
	/**数据内容*/
	@Excel(name="数据内容")
	private java.lang.String dataContent;
	/**版本号*/
	@Excel(name="版本号")
	private java.lang.Integer versionNumber;

	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  所属部门
	 */
	@Column(name ="SYS_ORG_CODE",nullable=true,length=50)
	public java.lang.String getSysOrgCode(){
		return this.sysOrgCode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  所属部门
	 */
	public void setSysOrgCode(java.lang.String sysOrgCode){
		this.sysOrgCode = sysOrgCode;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  所属公司
	 */
	@Column(name ="SYS_COMPANY_CODE",nullable=true,length=50)
	public java.lang.String getSysCompanyCode(){
		return this.sysCompanyCode;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  所属公司
	 */
	public void setSysCompanyCode(java.lang.String sysCompanyCode){
		this.sysCompanyCode = sysCompanyCode;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  表名
	 */
	@Column(name ="TABLE_NAME",nullable=true,length=32)
	public java.lang.String getTableName(){
		return this.tableName;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  表名
	 */
	public void setTableName(java.lang.String tableName){
		this.tableName = tableName;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  数据ID
	 */
	@Column(name ="DATA_ID",nullable=true,length=32)
	public java.lang.String getDataId(){
		return this.dataId;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  数据ID
	 */
	public void setDataId(java.lang.String dataId){
		this.dataId = dataId;
	}
	/**
	 *方法: 取得java.lang.String
	 *@return: java.lang.String  数据内容
	 */
	@Column(name ="DATA_CONTENT",nullable=true,length=32)
	public java.lang.String getDataContent(){
		return this.dataContent;
	}

	/**
	 *方法: 设置java.lang.String
	 *@param: java.lang.String  数据内容
	 */
	public void setDataContent(java.lang.String dataContent){
		this.dataContent = dataContent;
	}
	/**
	 *方法: 取得java.lang.Integer
	 *@return: java.lang.Integer  版本号
	 */
	@Column(name ="VERSION_NUMBER",nullable=true,length=4)
	public java.lang.Integer getVersionNumber(){
		return this.versionNumber;
	}

	/**
	 *方法: 设置java.lang.Integer
	 *@param: java.lang.Integer  版本号
	 */
	public void setVersionNumber(java.lang.Integer versionNumber){
		this.versionNumber = versionNumber;
	}
}
