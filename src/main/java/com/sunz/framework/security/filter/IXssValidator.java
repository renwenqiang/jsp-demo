package com.sunz.framework.security.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * Xss验证
 * 
 * @author Xingzhe
 *
 */
@FunctionalInterface
public interface IXssValidator {
	/**
	 * 
	 * @param request
	 * @param field
	 * @return
	 */
	boolean isXss(HttpServletRequest request,String field);
}
