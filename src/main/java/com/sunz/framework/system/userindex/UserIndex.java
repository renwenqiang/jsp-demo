package com.sunz.framework.system.userindex;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="T_S_USER_INDEX")
public class UserIndex extends com.sunz.framework.core.Entity {
	/**
	 * 关联用户id 
	 */
	private String userid;
	/**
	 * 关联部门Id 
	 */
	private String departid;
	/**
	 *路径 
	 */
	private String indexpath;
	/**
	 *名称或描述 
	 */
	private String indexname;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDepartid() {
		return departid;
	}
	public void setDepartid(String departid) {
		this.departid = departid;
	}
	public String getIndexpath() {
		return indexpath;
	}
	public void setIndexpath(String indexpath) {
		this.indexpath = indexpath;
	}
	public String getIndexname() {
		return indexname;
	}
	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}
	
}
