package com.sunz.framework.core;

import java.util.Map;

import javax.servlet.ServletRequest;

import org.jeecgframework.web.system.pojo.base.TSUser;

/**
 * 登陆验证接口
 * 		1.允许自定义验证逻辑
 * 		2.允许自定义返回值，用于一次性返回项目所需的特殊信息
 */
public interface ILoginSupport {
	
	/**
	 * 登陆用户信息(TSUser)保存在session中的名称（为兼容原来代码，使用ResourceUtil.LOCAL_CLINET_USER中的值)
	 */
	final String Session_ParameterName_User="LOCAL_CLINET_USER";
	
	/**
	 * 登陆用户扩展信息保存在session中的名称
	 */
	final String Session_ParameterName_Extend="LOGIN_USER_EXTEND";
	
	/**
	 * 
	 * @return
	 */
	//boolean isLogin(ServletRequest request);
	  
	/**
	 * 验证用户
	 * 
	 * @param params
	 * @return
	 */
	TSUser validate(Map params);
	
	/**
	 * 获取扩展信，扩展信息用于登陆后保存在session中，以方便快速使用
	 * 
	 * @remark  
	 * 1.此方法由登陆时使用，业务代码不应调用而应使用getLoginExtendInfo
	 * 2.实现类应注意：此返回值将用于JsonResult的data，因此必须要可序列化（json化）--不能包含可导致循环的引用
	 *
	 * @param user
	 * @return
	 */
	Object getExtendInfo(TSUser user);
	
	/**
	 * 统一保存登陆信息
	 * 
	 * @param request
	 * @param user
	 * @param extendInfo
	 */
	void saveLoginInfo(ServletRequest request,TSUser user,Object extendInfo);
	
	/**
	 * 统一清理登陆信息（注销登陆）
	 * 
	 * @param request
	 */
	void clearLoginInfo(ServletRequest request);
	
	/**
	 * 获取登陆用户
	 * 
	 * @param request
	 * @return
	 */
	TSUser getLoginUser(ServletRequest request);
	
	/**
	 * 获取登陆用户的扩展信息
	 * 
	 * @param request
	 * @return
	 */
	Object getLoginExtendInfo(ServletRequest request);
	
	/**
	 * 某些情况下“游客”也需要一个身份，此方法允许为未登陆用户模拟一个Guest用户
	 * 
	 * 不管是否在getLoginUser中返回此值，总应允许项目自己实现
	 * 
	 * @return
	 */
	TSUser simulateGuestUser();
}
