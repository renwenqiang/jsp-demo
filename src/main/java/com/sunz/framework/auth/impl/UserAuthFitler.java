package com.sunz.framework.auth.impl;

import javax.servlet.http.HttpServletRequest;

import com.sunz.framework.auth.IAuthFilter;


/**
 * 预计可能需要的跟 用户 挂钩的权限过滤
 * 	  @todo 预计可能需要的跟 用户 挂钩的权限过滤 未实现
 * @author Xingzhe
 *
 */
public class UserAuthFitler implements IAuthFilter{

	@Override
	public String getType() {
		return "user";
	}
	
	@Override
	public String getControlType(String resourceCode, String params,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
