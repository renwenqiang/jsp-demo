package com.sunz.framework.system.login.unified;

/**
 * 统一认证登录票据信息
 * 	包含时间和用户标识（用户标识具体意义由项目协商）
 * 
 * @author Xingzhe
 *
 */
public class Ticket {
	public Ticket() {
	}
	public Ticket(Long time, String uid) {
		this.time = time;
		this.uid = uid;
	}

	Long time;
	/**
	 * 票据创建时间
	 * 
	 * @return
	 */
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
	String uid;
	/**
	 * 用户标识（具体意义由项目自己协商）
	 * 
	 * @return
	 */
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
