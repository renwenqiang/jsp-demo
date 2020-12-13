package org.jeecgframework.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sunz.framework.core.ILoginSupport;
/**
* @ClassName: ContextHolderUtils 
*  TODO(上下文工具类) 
*
 */
@Component
public class ContextHolderUtils {
	/**
	 * SpringMvc下获取request
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;

	}
	/**
	 * SpringMvc下获取session
	 * 
	 * @return
	 */
	public static HttpSession getSession() {
		HttpSession session = getRequest().getSession();
		return session;

	}

	private static ILoginSupport loginSupport;
	@Autowired
	public void setLoginSupport(ILoginSupport loginSupport) {
		ContextHolderUtils.loginSupport = loginSupport;
	}
	public static TSUser getLoginUser() {
		return loginSupport.getLoginUser(getRequest());
	}
}
