package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.ILoginSupport;
import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;
import com.sunz.framework.util.StringUtil;

/**
 * 基于角色的控制：
 * 	  仅判断有无角色
 * 
 * @author Xingzhe
 *
 */
@Component
public class RolelySecurityResolver extends BaseUriSecurityResolver {

	private static ILoginSupport loginSupport;
	@Autowired(required=false)
	public void setLoginSupport(ILoginSupport loginSupport) {
		RolelySecurityResolver.loginSupport = loginSupport;
	}
	
	private String[] excudeRoles;
	public void setSetting(String jsonArrayString) {
		excudeRoles=StringUtil.parseToArray(jsonArrayString);
	}

	public ValidateInfo validate(HttpServletRequest request) {
		if(excudeRoles!=null){
			TSUser user=loginSupport.getLoginUser(request);
			if(user!=null){
				for(String role:excudeRoles){
					if(user.hasRole(role)) 
						return new ValidateInfo(true, this.path);
				}
			}
		}
		return new ValidateInfo(false, request.getRequestURI(), this.error==null||"".equals(this.error)?Error_Empty:this.error);
		//return this.error==null||"".equals(this.error)?Error_Empty:this.error;
	}

	public String getType(){
		return "role";
	}
}
