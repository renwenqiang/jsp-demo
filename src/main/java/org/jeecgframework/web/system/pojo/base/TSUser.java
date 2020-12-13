package org.jeecgframework.web.system.pojo.base;

import java.util.List;

import org.jeecgframework.core.common.entity.IdEntity;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 系统用户表
 *  @author 
 */
@Entity
@Table(name = "t_s_base_user")
public class TSUser extends IdEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Excel(name = "用户名")
	private String userName;// 用户名
	@Excel(name = "真实姓名")
	private String realName;// 真实姓名

	private String password;//用户密码
	private Short status;
	@Column(name = "status")
	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	@Column(name = "password", length = 100)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name = "username", nullable = false, length = 500)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Column(name = "realname", length = 100)
	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	@Column(name = "orderIndex")
	private int orderIndex;
	public int getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}
	
	/**
	 * 添加角色列表属性，因为角色太常用了
	 * 2017-11-08 张航宇 添加注释
	 */
	private List<TSRole> roles;
	@Transient
	public List<TSRole> getRoles() {
		return roles;
	}

	public void setRoles(List<TSRole> roles) {
		this.roles = roles;
	}
	
	/**
	 * 是否具有指定角色
	 * 
	 * @param rolecode
	 * @return
	 * 
	 * 2017-11-08 张航宇
	 */
	public boolean hasRole(String rolecode){
		if(rolecode==null||"".equals(rolecode)) return false;
		if(roles==null) return false;
		
		for(TSRole role:roles){
			if(rolecode.equals(role.getRoleCode()))
				return true;
		}
		
		return false;
	}
	@Excel(name = "组织机构编码(多个组织机构编码用逗号分隔，非必填)")
	private String departid;

	public void setDepartid(String departid){
		this.departid = departid;
	}
	@Column(name = "departid",length=32)
	public String getDepartid(){
		return departid;
	}
}