package com.sunz.framework.security.filter;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.ILoginSupport;
import com.sunz.framework.query.helper.SqlParser;
import com.sunz.framework.util.StringUtil;

/**
 *	拦截jsp（Interceptor是基于Spring MVC的，仅拦截得到controller
 *
 *	按角色-菜单配置拦截页面--直接路径匹配--严格的Interceptor拦截无法对全项目细致配置
 *
 * @author Xingzhe
 *
 */
@WebFilter(urlPatterns= {"*.jsp","*.do"})
public class SecurityFilter implements javax.servlet.Filter {
	static Logger logger = Logger.getLogger("SecurityFilter");	
	
	static Set<String> openUrlSet;
	static Map<String,Set<String>> urlRolesMap;
	static Map<String,String> urlNameMap;
	static ILoginSupport loginSupport;
	
	
	private void forwardError(HttpServletRequest request, ServletResponse response,String urlHolder) throws ServletException, IOException {
		request.setAttribute("err", "无权访问");
		request.setAttribute("origUrl",urlHolder);
		request.getRequestDispatcher("/noauth.jsp").forward(request, response);
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest) servletRequest;
		TSUser loginUser=loginSupport.getLoginUser(request);
		boolean userless=loginUser==null;
		String url=request.getServletPath();
		while(url.startsWith("/")&&url.length()>0) {
			url=url.substring(1);
		}
		
		// jsp暂时不管参数
		if(userless) {
			if(request.getServletPath().endsWith("jsp")) {
				if(openUrlSet.contains(url)) {
					chain.doFilter(request, response);
					return;
				}else {
					forwardError(request, response,request.getServletPath());
					return;
				}
			}
			chain.doFilter(request, response);
			return;
		}else {
			String fullUrl=request.getQueryString()==null?url:(url+"?"+request.getQueryString());
			
			Set<String> validRoles=urlRolesMap.get(fullUrl);
			if(validRoles ==null) {					// 未在菜单权限中配置，jsp就要拦一下，其它不管
				if(request.getServletPath().endsWith("jsp")) {
					if(openUrlSet.contains(url)) {
						chain.doFilter(request, response);
						return;
					}else {
						forwardError(request, response,request.getServletPath());
						return;
					}
				}
				chain.doFilter(request, response);
				return;
			}else {
				for(TSRole role:loginUser.getRoles()) {
					if(validRoles.contains(role.getId())||validRoles.contains(role.getRoleCode())) {
						chain.doFilter(request, response);
						return;
					}
				}
				forwardError(request, response,urlNameMap.get(fullUrl));
				return;
			}
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}	
	@Override
	public void destroy() {
		
	}

	
	@Component
	public static class Innitor implements ICacheRefreshable{
		private NamedParameterJdbcTemplate jdbcTemplate;
		@Autowired
		public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
		@PostConstruct
		public void init() {
			urlRolesMap=new HashMap<String, Set<String>>();
			urlNameMap=new HashMap<String, String>();
			String sql= SqlParser.getSql(null, "sys_queryUrlRoleMap");
			jdbcTemplate.query(sql,(ResultSet rs)->{
				String url = rs.getString(1),role=rs.getString(2);
				if(StringUtil.isEmpty(url)||StringUtil.isEmpty(role))
					return;
				
				Set<String> roleSet=urlRolesMap.get(url);
				if(roleSet==null) {
					urlRolesMap.put(url,roleSet=new HashSet<String>());
				}
				roleSet.add(role);
				
				urlNameMap.put(url, rs.getString(3));
			});
			
			openUrlSet=new HashSet<String>();
			String key="security.openUrls";
			Config.ChangeHandler handler=(k)->{
				if(k==null || key.equals(k)) {
					for(String jsp:Config.get(key).split("\\s*(\r|\n)\\s*")) {
						if(StringUtil.isEmpty(jsp))
							continue;
						openUrlSet.add(jsp);
					}
				}
			};
			handler.onChange(key);
			Config.addChangeListener(handler);
		}
		@Override
		public void refresh(String item) {
			init();
		}
		@Override
		public String getCategory() {
			return "securityX";
		}
		@Override
		public String getDescription() {
			return "页面权限（与角色-菜单配套）拦截器，不支持单项刷新";
		}
		
		@Autowired
		public void setLoginSupport(ILoginSupport support) {
			loginSupport = support;
		}
	}
}
