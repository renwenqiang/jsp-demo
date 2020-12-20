package com.sunz.framework.security;

/**
 * Resolver工厂接口，允许各Resolver自己决定实例的创建
 * 
 * @author Xingzhe
 *
 */
public interface IResolverFactory {
	/**
	 *  因 SecurityResolverHelper总管了所有Resolver，注入此接口以备使用
	 * 
	 * @param resolverHelper
	 */
	void setResolverHelper(ISecurityResolverHelper resolverHelper);
	/**
	 * 创建Resolver
	 * 
	 * @param path			url特征
	 * @param defaultError	默认不通过[错误]描述
	 * @param setting		Resolver配置
	 * @param id			标识，用于反查
	 * @return
	 */
	IUriSecurityResolver create(int securityType,String path,String defaultError,String setting,String id);
	
	/**
	 * 返回类型，配置为此类型的Resolver都由此Factory创建
	 * 
	 * @return
	 */
	String getType();
	
	/**
	 * 支持[缓存]刷新
	 */
	void refresh();
}
