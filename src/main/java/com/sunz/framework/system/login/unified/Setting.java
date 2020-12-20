package com.sunz.framework.system.login.unified;

import java.util.Date;

import com.sunz.framework.core.Entity;

/**
 * 用于统一认证登录的配置信息
 * 
 * @author Xingzhe
 *
 */
@javax.persistence.Entity
@javax.persistence.Table(name="t_s_unified")
public class Setting extends Entity implements Cloneable{
	String domain;
	String uidField;
	byte[] privateKeys;
	byte[] publicKeys;
	String remark;
	
	/**
	 * 域名/项目地址
	 * @return
	 */
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * 用户标识字段 （用于从TSUser上取值的表达式）
	 * @return
	 */
	public String getUidField() {
		return uidField;
	}
	public void setUidField(String uidField) {
		this.uidField = uidField;
	}
	
	/**
	 * 服务器端（本项目持有）的密钥
	 * @return
	 */
	public byte[] getPrivateKeys() {
		return privateKeys;
	}
	public void setPrivateKeys(byte[] privateKeys) {
		this.privateKeys = privateKeys;
	}
	
	/***
	 * 客户端（接入统一认证登录的项目）的密钥
	 * @return
	 */
	public byte[] getPublicKeys() {
		return publicKeys;
	}
	public void setPublicKeys(byte[] publicKeys) {
		this.publicKeys = publicKeys;
	}
	
	/**
	 * 备注说明
	 * @return
	 */
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	private Date addTime;
	/**
	 * 创建时间
	 * 
	 * @return
	 */
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	public Setting clone() {
		try {
			return (Setting)super.clone();
		} catch (CloneNotSupportedException e) {			
			throw new RuntimeException("不可能的事", e);
		}
	}
}
