package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.sunz.framework.query.helper.SqlParser;
import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;

/**
 * 简单路径控制：凡使用此解决器的直接放行
 * 
 * @author Xingzhe
 *
 */
@Component
public class FreemarkerlySecurityResolver extends BaseUriSecurityResolver {
	
	private String freemarker;
	public void setSetting(String freemarkerCode) {
		freemarker=freemarkerCode;
	}

	public ValidateInfo validate(HttpServletRequest request) {
		if(freemarker==null)
			return new ValidateInfo(true, this.path);	//
					
		try {			
			String msg= SqlParser.parseFreemarkerSql(freemarker, toMap(request), this.id);
			return new ValidateInfo(msg==null||"".equals(msg), this.path, msg);			
		} catch (Exception e) {
			logger.error("freemarker类型的安全控制运算出错，"+this.path+"被限制访问",e);
			return new ValidateInfo(false, this.path,  "内部（安全控制）错误");
		}
		
	}

	public String getType(){
		return "freemarker";
	}
}
