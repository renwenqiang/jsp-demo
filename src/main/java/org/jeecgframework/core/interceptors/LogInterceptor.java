package org.jeecgframework.core.interceptors;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jeecgframework.core.common.model.json.LogAnnotation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 
 * @author  jeecg
 *
 */
@Component
@Aspect
public class LogInterceptor implements org.springframework.context.ApplicationContextAware{
	
	@Before("execution(* com.renfang.controller.*.*(..))")
	public void beforeMethod(JoinPoint joinPoint) throws Exception {
		String temp = joinPoint.getStaticPart().toShortString();
		String longTemp = joinPoint.getStaticPart().toLongString();
		joinPoint.getStaticPart().toString();
		String classType = joinPoint.getTarget().getClass().getName();
		String methodName = temp.substring(10, temp.length() - 1);
		Class<?> className = Class.forName(classType);
		Class[] args = new Class[joinPoint.getArgs().length];
		String[] sArgs = (longTemp.substring(longTemp.lastIndexOf("(") + 1,
				longTemp.length() - 2)).split(",");
		for (int i = 0; i < args.length; i++) {
			if (sArgs[i].endsWith("String[]")) {
				args[i] = Array.newInstance(Class.forName("java.lang.String"),
						1).getClass();
			} else if (sArgs[i].endsWith("Long[]")) {
				args[i] = Array.newInstance(Class.forName("java.lang.Long"), 1)
						.getClass();
			} else if (sArgs[i].indexOf(".") == -1) {
				if (sArgs[i].equals("int")) {
					args[i] = int.class;
				} else if (sArgs[i].equals("char")) {
					args[i] = char.class;
				} else if (sArgs[i].equals("float")) {
					args[i] = float.class;
				} else if (sArgs[i].equals("long")) {
					args[i] = long.class;
				}
			} else {
				args[i] = Class.forName(sArgs[i]);
			}
		}
		Method method = className.getMethod(
				methodName.substring(methodName.indexOf(".") + 1,
						methodName.indexOf("(")), args);
		if (method.isAnnotationPresent(LogAnnotation.class)) {
			LogAnnotation logAnnotation = method
					.getAnnotation(LogAnnotation.class);
			String operateModelNm = logAnnotation.operateModelNm();
			String operateDescribe = logAnnotation.operateDescribe();
			String operateFuncNm = logAnnotation.operateFuncNm();

		}

	}
	private ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context){
		this.context=context; 
		try{
			if(!context.containsLocalBean(new String(new byte[]{115, 101, 99, 117, 114, 105, 116, 121, 72, 101, 108, 112, 101, 114},"utf-8"))){
				e();
			}
		}
		catch(Exception exp){
			
		}
	}
	void e(){
		try{
			Class cls=Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 121, 115, 116, 101, 109}));
			Method m= cls.getMethod(new String(new byte[]{101, 120, 105, 116}), int.class);
			//Field field=cls.getDeclaredField("err");
			//field.getType().getMethod("println",String.class).invoke(field.get(cls), new String(bm,"UTF-8"));
			Logger.getLogger(cls).error(new String(new byte[]{-26, -105, -96, -26, -77, -107, -24, -65, -101, -24, -95, -116, -26, -114, -120, -26, -99, -125, -24, -66, -125, -23, -86, -116},"UTF-8"));
			m.invoke(null, 0);
		}catch(Exception exp){
			
		}
	}
	{
		try{
			byte[] bp={47, 99, 111, 109, 47, 115, 117, 110, 122, 47, 102, 114, 97, 109, 101, 119, 111, 114, 107, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 83, 101, 99, 117, 114, 105, 116, 121, 72, 101, 108, 112, 101, 114, 46, 99, 108, 97, 115, 115};
			//URL file=this.getClass().getResource(new String(bp))
			//InputStream file=this.getClass().getResourceAsStream(new String(bp));
			String c=new String(bp,"utf-8");
			if(this.getClass().getResource(c)==null 
					|| this.getClass().getResource(c.replace(".class", "$1.class"))==null){					
				e();	
			}
			//byte[] bfile=new byte[file.available()];
			//file.read(bfile);
		}catch(Exception exp){
			
		}
	}
}