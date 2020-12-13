package org.jeecgframework.core.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecgframework.core.annotation.Ehcache;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author  jeecg
 *
 */
@Component
@Aspect
public class EhcacheAspect {
	private static Cache dictCache;
	private static Cache eternalCache;
	
	static {
		if (eternalCache == null) {
			eternalCache = CacheManager.getInstance().getCache("eternalCache");
		}
		if (dictCache == null) {
			dictCache = CacheManager.getInstance().getCache("dictCache");
		}
		
//		// 没有配置缓存
//		if(dictCache==null){
//			dictCache=new Cache("dictCache", 10000, true, false, 1200, 1200);
//		}
//		if(eternalCache==null){
//			eternalCache=new Cache("eternalCache", 10000, true, true, 1200, 1200);
//		}
	}

	@Pointcut("@annotation(org.jeecgframework.core.annotation.Ehcache)")
	public void simplePointcut() {
	}

	@AfterReturning(pointcut = "simplePointcut()")
	public void simpleAdvice() {
	}

	@Around("simplePointcut()")
	public Object aroundLogCalls(ProceedingJoinPoint joinPoint)
			throws Throwable {	
		//Class clsTarget = joinPoint.getTarget().getClass();
		//Method currentMethod = clsTarget.getMethod(msign.getName(), msign.getParameterTypes());
		//Ehcache flag = currentMethod.getAnnotation(Ehcache.class);
		MethodSignature msign=(MethodSignature) joinPoint.getSignature();
		Ehcache flag = msign.getMethod().getAnnotation(Ehcache.class);	// 当前注解仅可能打在实现类而不是接口上！？
		if(flag==null){
			return null;
		}
		if(flag.eternal()){
			if(eternalCache==null)
				return null;
		}else if(dictCache==null)
			return null;
		
		String targetName = joinPoint.getTarget().getClass().toString();
		String methodName = msign.getName();
		Object[] arguments = joinPoint.getArgs();  
			
		
		//Ehcache flag =joinPoint.getTarget().getClass().getMethod(methodName).getAnnotation(Ehcache.class);
		Object result;
		String cacheKey = getCacheKey(targetName, methodName, arguments);
		
		Element element = null;
		if(flag.eternal()){
			//永久缓存			
			element = eternalCache.get(cacheKey);
		}else{
			//临时缓存			
			element = dictCache.get(cacheKey);
		}
		

		if (element == null) {
			if ((arguments != null) && (arguments.length != 0)) {
				result = joinPoint.proceed(arguments);
			} else {
				result = joinPoint.proceed();
			}

			element = new Element(cacheKey, (Serializable) result);
			if(flag.eternal()){
				//永久缓存
				eternalCache.put(element);
			}else{
				//临时缓存
				dictCache.put(element);
			}
		}
		return element.getValue();
	}

	/**
	 * 获得cache key的方法，cache key是Cache中一个Element的唯一标识 cache key包括
	 * 包名+类名+方法名，如com.co.cache.service.UserServiceImpl.getAllUser
	 */
	private String getCacheKey(String targetName, String methodName,
			Object[] arguments) {
		StringBuffer sb = new StringBuffer();
		sb.append(targetName).append(".").append(methodName);
		if ((arguments != null) && (arguments.length != 0)) {
			for (int i = 0; i < arguments.length; i++) {
				sb.append(".").append(JSON.toJSONString(arguments[i]));
			}
		}
		return sb.toString();
	}
}
