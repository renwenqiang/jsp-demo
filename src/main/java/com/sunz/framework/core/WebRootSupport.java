package com.sunz.framework.core;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 	需要使用webRoot（部署后项目根目录的绝对路径）的基类
 * 
 * @remark 需要打Component注解
 * 
 * @author Xingzhe
 *
 */
public abstract class WebRootSupport implements ApplicationContextAware {

	/**
	 * static，除setApplicationContext方法外，子类不允许写此值
	 */
	protected static String webRoot;
	protected static String getWebRoot(){
		return webRoot;
	}
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		try {
			if(webRoot==null) // static
				webRoot=context.getResource("").getFile().getPath()+File.separator;
			
		} catch (IOException e) {
			
		}
	}

}
