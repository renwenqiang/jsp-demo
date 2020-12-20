package com.sunz.framework.auth.impl.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.sunz.framework.auth.IControlTypeHandler;

@Component
public class HiddenHandler implements IControlTypeHandler {

	private static final Logger logger = Logger.getLogger(HiddenHandler.class);
	@Override
	public int doStartTag(String filterType,String srourceCode,String srourceName, String params,PageContext context) {
		try {
			context.getOut().write("<div style=\"display:none !important\">");
		} catch (IOException e) {
			logger.error(e);
		}
		return TagSupport.EVAL_PAGE;
	}


	//private static Pattern tagPattern = Pattern.compile("^\\s<\\w[^>]*");
	@Override
	public int doEndTag(String filterType,String srourceCode,String srourceName, String params,PageContext context,BodyContent content) {
		try {
			if(content!=null)
				context.getOut().write(content.getString());
			context.getOut().write("</div>");
		} catch (IOException e) {
			logger.error(e);
		}
		/*String html=content.getString();
		if(html!=null){
			Matcher matcher= tagPattern.matcher(html);
			if(matcher.find()){
				html=matcher.replaceFirst("$0"+"style=\"display:none !important\"");
			}else{
				html="<div style=\"display:none\">"+html+"</div>";
			}
			try {
				content.getEnclosingWriter().print(html);
			} catch (IOException e) {
				logger.error(e);
			}
		}*/
		return TagSupport.EVAL_PAGE;
	}


	@Override
	public String getCode() {
		return "hidden";
	}


	@Override
	public String getName() {
		return "隐藏";
	}

}
