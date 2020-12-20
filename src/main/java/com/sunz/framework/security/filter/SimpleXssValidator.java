package com.sunz.framework.security.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;

@Component
public class SimpleXssValidator implements IXssValidator{
	final String CONFIGKEY_XSS="xss.regExp";
	Pattern Pattern_Tag ;
	void initPattern() {
		String reg=Config.get(CONFIGKEY_XSS);
		if(StringUtil.isEmpty(reg))
			reg="<(/?[a-z]+)";
		
		Pattern_Tag = Pattern.compile(reg);
	}
	
	{
		Config.ChangeHandler handler=(key)->{
			if(key==null||CONFIGKEY_XSS.equals(key))
				initPattern();
		};
		Config.addChangeListener(handler);
		handler.onChange(null);
	}
	
	Logger logger = Logger.getLogger(this.getClass());
	@Override
	public boolean isXss(HttpServletRequest request, String field) {
		String[] vs=request.getParameterValues(field);
		if(vs==null) return false;
		for(String v:vs) {
			if(logger.isDebugEnabled())
				logger.debug("XSS分析："+field+"="+v);
			
			if(Pattern_Tag.matcher(v).find()){
				logger.warn("发现Xss攻击：【"+request.getServletPath()+"】参数"+field+"="+v); // 日志
				return true;
			}
		}
		return false;
	}
}
