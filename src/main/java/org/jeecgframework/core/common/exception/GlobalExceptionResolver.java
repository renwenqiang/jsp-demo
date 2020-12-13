package org.jeecgframework.core.common.exception;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.core.JsonResult;

/**
 * spring mvc 全局处理异常捕获 根据请求区分ajax和普通请求，分别进行响应.
 * 第一、异常信息输出到日志中。
 * 第二、截取异常详细信息的前50个字符，写入日志表中t_s_log。
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
	@Autowired
	private SystemService systemService;
	private static final Logger logger = Logger.getLogger(GlobalExceptionResolver.class);
	//记录数据库最大字符长度
	private static final int WIRTE_DB_MAX_LENGTH = 2000; 
	private static final short LOG_LEVEL = 6;
	private static final short LOG_TYPE = 99;
	
	ModelAndView empty = new ModelAndView();
	/**
	 * 对异常信息进行统一处理，区分异步和同步请求，分别处理
	 */	
	public ModelAndView resolveException(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) {
        // logger
      	logger.error("全局处理异常捕获:", ex);
      	
      	// db
      	//String exceptionMessage = "错误异常: "+ex.getClass().getSimpleName()+",错误描述："+ex.getMessage();
      	String exceptionMessage = getThrowableMessage(ex);
		if(oConvertUtils.isNotEmpty(exceptionMessage)){
			if(exceptionMessage.length() > WIRTE_DB_MAX_LENGTH){
				exceptionMessage = exceptionMessage.substring(0,WIRTE_DB_MAX_LENGTH);
			}
		}
		systemService.addLog(exceptionMessage, LOG_TYPE, LOG_LEVEL);
		
		// return by type
		boolean isajax = request.getHeader("X-Requested-With")!=null;
		if(isajax){
			response.setHeader("Cache-Control", "no-store");
			JsonResult json = new JsonResult(false);
			//json.setMsg("意外的错误【"+ex.getClass().getSimpleName()+"】，详情请联系系统管理人员");
			json.setMsg(ex.getMessage());// ajax请求输出错误信息风险很小
			try {
				PrintWriter pw=response.getWriter();
				pw.write(JsonHelper.toJSONString(json));
				pw.flush();
			} catch (IOException e) {
				logger.error("全局错误处理器向前端返回错误json时异常",e);
			}
			return empty;
		}else{
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("errClass",ex instanceof RuntimeException?ex.getMessage():ex.getClass().getSimpleName());
			//model.put("errClass",ex.getClass().getSimpleName());
			model.put("errTime", new Date().toLocaleString());
			//model.put("err", exceptionMessage);
			//model.put("ex", ex);
			return new ModelAndView("common/error", model);
		}
	}
	
	/**
	 * 返回错误信息字符串
	 * 
	 * @param ex
	 *            Exception
	 * @return 错误信息字符串
	 */
	public String getThrowableMessage(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
}
