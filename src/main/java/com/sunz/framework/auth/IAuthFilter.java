package com.sunz.framework.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限过滤接口
 *    设计目的：支持扩展过滤（默认提供环节权限）
 *    使用方法：实现getControlType，返回一个“控制类型字符串”，此“控制类型”将交由IControlTypeHanlder处理
 *    扩展和使用方法：为减轻配置，接口设计为自描述；实现类必须打上Spring的Componet注解或在spring的xml中配置
 *    
 * @author Xingzhe
 *
 */
public interface IAuthFilter {
	/**
	 * 计算控制类型
	 * 
	 * @param resourceCode 资源code
	 * @param params 扩展参数，设计认为此值在jsp中必须为el表达式（即动态）才有意义
	 * @param request 当前请求（上下文)
	 * @return
	 */
	String getControlType(String resourceCode,String params,HttpServletRequest request);
	
	/**
	 * 返回分类字符，此方法被设计为自描述(无功能上的意义)，返回值必须唯一，与标签中的type一一对应
	 * 
	 * @return
	 */
	String getType();
}
