package com.sunz.framework.core;

/**
 * 缓存刷新接口，用于统一管理
 * 
 * @author Xingzhe
 *
 */
public interface ICacheRefreshable {
	/**
	 * 刷新[缓存]
	 * 
	 * @param item 刷新项--缓存的刷新无非两种：一种是刷新某一项缓存，一种是本类缓存全部刷新
	 */
	void refresh(String item);
	
	/**
	 * 缓存类型名
	 *    同一类型名字当相同，且只允许一种实现被Spring实例化
	 * 
	 * @return
	 */
	String getCategory();
	
	/**
	 * 描述
	 * @return
	 */
	String getDescription();	
}
