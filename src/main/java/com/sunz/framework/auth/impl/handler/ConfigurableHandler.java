package com.sunz.framework.auth.impl.handler;

import java.io.IOException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.sunz.framework.auth.IControlTypeHandler;

/**
 * 可配置的处理器
 * 
 * @author Xingzhe
 *
 */
public class ConfigurableHandler implements IControlTypeHandler {

	private static final Logger logger = Logger.getLogger(ConfigurableHandler.class);

	private String formatStart="";

	private String formatEnd="";
	private boolean outputbody=true;
	private String code="";
	private String name="";
	public void setFormatStart(String formatStart) {
		this.formatStart = formatStart;
	}
	public void setFormatEnd(String formatEnd) {
		this.formatEnd = formatEnd;
	}
	public void setOutputbody(boolean outputbody) {
		this.outputbody = outputbody;
	}
	public void setCode(String type) {
		this.code = type;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ConfigurableHandler(){
		
	}
	public ConfigurableHandler(String type,String name,String formatStart,String formatEnd,boolean outputbody){
		this.code=type;
		this.name=name;
		this.formatStart=formatStart;
		this.formatEnd=formatEnd;
		this.outputbody=outputbody;
	}
	
	@Override
	public int doStartTag(String filterType,String srourceCode,String srourceName, String params,PageContext context) {
		
		try {
			context.getOut().write(String.format(formatStart, srourceCode,params));
		} catch (IOException e) {
			logger.error(e);
		}
		return TagSupport.EVAL_PAGE;
	}

	@Override
	public int doEndTag(String filterType,String srourceCode,String srourceName, String params,PageContext context,BodyContent content) {
		try {
			if(outputbody && content!=null)
				context.getOut().write(content.getString());
			
			context.getOut().write(String.format(formatEnd,srourceCode,params));
		} catch (IOException e) {
			logger.error(e);
		}
		return TagSupport.EVAL_PAGE;
	}


	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

}
