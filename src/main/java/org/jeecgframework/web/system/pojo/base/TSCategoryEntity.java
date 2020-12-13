package org.jeecgframework.web.system.pojo.base;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.jeecgframework.core.common.entity.IdEntity;

/**
 * : Entity
 *  分类管理
 * @author JueYue
 *  2014-09-16 21:50:55
 * @version V1.0
 * 
 */
@Entity
@Table(name = "t_s_category", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
public class TSCategoryEntity extends IdEntity  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/** 类型名称 */
	private java.lang.String name;
	/** 类型编码 */
	private java.lang.String code;
	/** 分类图标 */
	private TSIcon icon;	
	/** 组织 */
	private java.lang.String sysOrgCode;
	/** 公司 */
	private java.lang.String sysCompanyCode;
	/** 上级 */
	private TSCategoryEntity parent;

	private List<TSCategoryEntity> list;

	/**
	 * 方法: 取得java.lang.String
	 * 
	 * @return: java.lang.String 类型名称
	 */
	@Column(name = "NAME", nullable = true, length = 32)
	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * 方法: 设置java.lang.String
	 * 
	 * @param: java.lang.String 类型名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 方法: 取得java.lang.String
	 * 
	 * @return: java.lang.String 类型编码
	 */
	@Column(name = "CODE", nullable = true, length = 32)
	public java.lang.String getCode() {
		return this.code;
	}

	/**
	 * 方法: 设置java.lang.String
	 * 
	 * @param: java.lang.String 类型编码
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * 方法: 取得TSCategoryEntity
	 * 
	 * @return: TSCategoryEntity 上级code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_CODE",referencedColumnName = "code")
	public TSCategoryEntity getParent() {
		return this.parent;
	}

	/**
	 * 方法: 设置TSCategoryEntity
	 * 
	 * @param: TSCategoryEntity 上级
	 */
	public void setParent(TSCategoryEntity parent) {
		this.parent = parent;
	}

	@Column(name = "SYS_ORG_CODE", nullable = true, length = 15)
	public java.lang.String getSysOrgCode() {
		return sysOrgCode;
	}

	public void setSysOrgCode(java.lang.String sysOrgCode) {
		this.sysOrgCode = sysOrgCode;
	}

	@Column(name = "SYS_COMPANY_CODE", nullable = true, length = 15)
	public java.lang.String getSysCompanyCode() {
		return sysCompanyCode;
	}

	public void setSysCompanyCode(java.lang.String sysCompanyCode) {
		this.sysCompanyCode = sysCompanyCode;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parent")
	public List<TSCategoryEntity> getList() {
		return list;
	}

	public void setList(List<TSCategoryEntity> list) {
		this.list = list;
	}

	@ManyToOne()
	@JoinColumn(name = "ICON_ID")
	public TSIcon getIcon() {
		return icon;
	}

	public void setIcon(TSIcon icon) {
		this.icon = icon;
	}
}
