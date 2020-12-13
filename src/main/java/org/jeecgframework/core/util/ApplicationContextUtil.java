package org.jeecgframework.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * @author  jeecg
 *
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {
	private static ApplicationContext context;
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static ApplicationContext getContext() {
		return context;
	}
}
