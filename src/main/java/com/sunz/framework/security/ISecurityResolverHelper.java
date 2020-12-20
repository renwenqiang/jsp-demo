package com.sunz.framework.security;

import javax.servlet.http.HttpServletRequest;

/**
 * SecurityResolver 总管接口
 * 
 * @author Xingzhe
 *
 */
public interface ISecurityResolverHelper {

	/**
	 * 开放的安全类型--即尚未登陆
	 */
	public static final int SecurityType_Open=0;
	
	/**
	 * 要求登陆的安全类型--已经登陆的
	 */
	public static final int SecurityType_LoginRequired=1;
	
	/**
	 * 指定路径、类型等配置创建 Resolver
	 * 
	 * @param path
	 * @param resolverType
	 * @param setting
	 * @param defaultError
	 * @param id
	 * @return
	 */
	IUriSecurityResolver createResolver(int securityType,String resolverType, String path, String setting, String defaultError,String id);

	/**
	 * 向总管类注册
	 * 
	 * @param securityType
	 * @param resolverKey
	 * @param resolver
	 */
	void addMapping(int securityType, String resolverKey, IUriSecurityResolver resolver);

	/***
	 * 获取指定类型和路径的处理器
	 * 
	 * @param securityType
	 * @param path
	 * @return
	 */
	IUriSecurityResolver getResolver(int securityType, String path);

	/**
	 * 获取指定请求的安全类型（已登陆、未登陆)
	 * 
	 * @param request
	 * @return
	 */
	int getSecurityType(HttpServletRequest request);

}