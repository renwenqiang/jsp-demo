package org.jeecgframework.web.system.pojo.base;
// default package

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jeecgframework.core.common.entity.IdEntity;

/**
 * 通用类型字典表
 *  @author  jeecg
 */
@Entity
@Table(name = "t_s_type")
public class TSType extends IdEntity implements java.io.Serializable {

	private TSTypegroup TSTypegroup;//类型分组
	private String typename;//类型名称
	private String typecode;//类型编码
//	private List<TPProcess> TSProcesses = new ArrayList();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typegroupid")
	public TSTypegroup getTSTypegroup() {
		return this.TSTypegroup;
	}

	public void setTSTypegroup(TSTypegroup TSTypegroup) {
		this.TSTypegroup = TSTypegroup;
	}

	

	@Column(name = "typename", length = 50)
	public String getTypename() {
		return this.typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	@Column(name = "typecode", length = 50)
	public String getTypecode() {
		return this.typecode;
	}

	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}
	
}