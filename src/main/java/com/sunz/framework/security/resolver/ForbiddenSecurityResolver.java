package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;

@Component
public class ForbiddenSecurityResolver extends BaseUriSecurityResolver{

	@Override
	public void setSetting(String setting) {
		
	}

	private static String ErrorMsg="当前服务被禁用";
	@Override
	public ValidateInfo validate(HttpServletRequest request) {		
		return new ValidateInfo(false, this.path,this.error==null||this.error.trim().length()==0?ErrorMsg:this.error);
	}

	@Override
	public String getType() {
		return "forbidden";
	}

	
}
