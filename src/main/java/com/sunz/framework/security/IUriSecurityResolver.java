package com.sunz.framework.security;

import javax.servlet.http.HttpServletRequest;

/**
 * url权限处理方案
 * 
 * @author Xingzhe
 *
 */
public interface IUriSecurityResolver {

	static final String Error_Empty="无权访问";

	String getId();
	
	void setId(String id);
	
	void init(int securityType, String path,String err);
	
	void setSetting(String setting);
	
	ValidateInfo validate(HttpServletRequest request);
	
	String getType();
}
