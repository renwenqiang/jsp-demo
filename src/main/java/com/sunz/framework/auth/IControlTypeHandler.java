package com.sunz.framework.auth;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;


/**
 * 控制类型处理器--处理上下文即TagSupport的上下文
 *    
 *    @remark 
 *       需特别注意的是，本接口会被以单例模式使用，请谨慎实现
 *       
 *       @list code和name进行自描述，code必须唯一
 *       @list 两个主要方法的返回值通常为0或6（还有1｜5等，其详细意义请参考TagSupport的常量）
 *       
 *    doStartTag 和 doEndTag 中的前4个参数为标签本身的属性
 *    
 * @author Xingzhe
 *
 */
public interface IControlTypeHandler {
	
	/**
	 * 自描述属性，表示此接口所处理的控制类型
	 * 
	 * @return
	 */
	String getCode();
	
	/**
	 * 自描述属性，表示此处理器的名称--将被用于显示
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 标签开始时的逻辑
	 *    直接沿用TagSupport
	 *    
	 * @param context
	 * @return
	 */
	int doStartTag(String filterType,String srourceCode,String srourceName, String params,PageContext context);
	
	/**
	 * 标签结束时的逻辑
	 *    直接沿用TagSupport
	 *    需要注意的是，被标签包含的内容不会自动输出，需要实现者主动调用输出（如需要输出)
	 * @param context
	 * @param content
	 * @return
	 */
	int doEndTag(String filterType,String srourceCode,String srourceName,String params,PageContext context,BodyContent content);
}
