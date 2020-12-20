package com.sunz.framework.security.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartResolver;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.ILoginSupport;
import com.sunz.framework.util.StringUtil;

/*
	<!-- Xss拦截过滤  -->
	<filter>
		<filter-name>xssFilter</filter-name>
		<filter-class>com.sunz.framework.security.filter.XssFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>xssFilter</filter-name>
		<url-pattern>/*.do</url-pattern>
	</filter-mapping>
*/
public class XssFilter implements Filter {
	Logger logger = Logger.getLogger(this.getClass());
	MultipartResolver multipartResolver;
	@Override
	public void init(FilterConfig config) throws ServletException {
	}	
	
	static String[] excludeRoles;
	static {
		String config_Key_excludeRole="xss.excludeRoles";
		Config.ChangeHandler handler=(key)->{
			if(key==null || config_Key_excludeRole.equals(key)) {
				excludeRoles=StringUtil.parseToArray(Config.get("xss.excludeRoles"));
			}
		};
		Config.addChangeListener(handler);
		handler.onChange(null);
	}
	static boolean hasRole(TSUser user) {
		for(String role:excludeRoles) {
			if(user.hasRole(role))
				return true;
		}
		return false;
	}
	private class XssResult {
		public boolean isXss=false;
	}
	//"{\"success\":false,\"msg\":\"请勿非法访问\"}".getBytes("UTF-8");
	byte[] errBytes=new byte[] { 123, 34, 115, 117, 99, 99, 101, 115, 115, 34, 58, 102, 97, 108, 115, 101, 44, 34, 109, 115, 103, 34, 58, 34, -24, -81, -73, -27, -117, -65, -23, -99, -98, -26, -77, -107, -24, -82, -65, -23, -105, -82, 34, 125};
	public void doFilter(ServletRequest rawRequest, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		final XssResult xr=new XssResult();
		
		TSUser user=loginSupport.getLoginUser(rawRequest);
		if(user!=null && excludeRoles.length>0 && hasRole(user)) {
			 chain.doFilter(rawRequest, response);
			 return;
		}
		
		HttpServletRequest request=(HttpServletRequest) rawRequest;
		if(multipartResolver==null) {
			multipartResolver=springmvcContext.getBean(MultipartResolver.class);
		}
		final HttpServletRequest httpRequest=multipartResolver.isMultipart(request)?multipartResolver.resolveMultipart(request):request;
		
		XssHelper.filter(httpRequest,(req, field)-> {
			return xr.isXss=xssValidator.isXss(req, field);
		});
		if(xr.isXss) {
			try {
				boolean isAjax = httpRequest.getHeader("X-Requested-With")!=null;
				if(isAjax) { 
					OutputStream  out = response.getOutputStream();
					out.write(errBytes);
					response.setContentType("text/html;charset=UTF-8");
				}else {
					String origUrl=httpRequest.getRequestURI()+"?"+ httpRequest.getQueryString();
					((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath()+"/noauth.jsp?err="+URLEncoder.encode("非法访问","utf-8")+"&origUrl="+URLEncoder.encode(origUrl,"UTF-8"));
				}
			} catch (IOException e) {
				
			}
		}else{
			chain.doFilter(httpRequest, response);
		}
		
	}
	@Override
	public void destroy() {
	}

	/**
	 * 需要使用Spring MVC的上下文来获取MultipartResolver（否则Filter之后就无法获取文件流），但目前没有静态（解耦）方法直接取到MVC的ApplicationContext
	 */
	private static ApplicationContext springmvcContext;
	@Controller
	public static class SpringMvcContextInitor implements ApplicationContextAware{
		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			XssFilter.springmvcContext=applicationContext;
		}
		
		@Autowired
		public void setLoginSupport(ILoginSupport loginSupport) {
			XssFilter.loginSupport = loginSupport;
		}
		@Autowired
		public void setXssValidator(IXssValidator xssValidator) {
			XssFilter.xssValidator = xssValidator;
		}
	}	
	private static ILoginSupport loginSupport;
	private static IXssValidator xssValidator;
	
}
