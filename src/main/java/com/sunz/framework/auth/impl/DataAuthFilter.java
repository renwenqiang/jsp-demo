package com.sunz.framework.auth.impl;

import javax.servlet.http.HttpServletRequest;

import com.sunz.framework.auth.IAuthFilter;


/**
 * 预计可能需要的跟 数据本身 挂钩的权限过滤
 * 	  @todo 预计可能需要的跟 数据本身 挂钩的权限过滤 未实现
 * 
 * @author Xingzhe
 *
 */
public class DataAuthFilter implements IAuthFilter{

	@Override
	public String getControlType(String resourceCode, String params,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "data";
	}

}
