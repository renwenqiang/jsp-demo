package com.sunz.framework.auth.impl.handler;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.stereotype.Component;

import com.sunz.framework.auth.IControlTypeHandler;

@Component
public class NoOutputHandler implements IControlTypeHandler {

	@Override
	public int doStartTag(String filterType,String srourceCode,String srourceName, String params,PageContext context) {		
		return TagSupport.SKIP_BODY;
	}

	@Override
	public int doEndTag(String filterType,String srourceCode,String srourceName, String params,PageContext context,BodyContent content) {		
		return TagSupport.EVAL_PAGE;
	}

	@Override
	public String getCode() {
		return "noOutput";
	}

	@Override
	public String getName() {
		return "不输出";
	}
}
