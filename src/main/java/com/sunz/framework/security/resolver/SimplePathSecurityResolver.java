package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;

/**
 * 简单路径控制：凡使用此解决器的直接放行
 * 
 * @author Xingzhe
 *
 */
@Component
public class SimplePathSecurityResolver extends BaseUriSecurityResolver {

	public void setSetting(String paramString) {
		
	}

	public ValidateInfo validate(HttpServletRequest request) {
		return new ValidateInfo(true, this.path);
	}

	public String getType(){
		return "simplepath";
	}
}
