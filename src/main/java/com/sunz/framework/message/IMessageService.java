package com.sunz.framework.message;

/**
 * 消息投递接口
 * 	@remark 实现类约定：
 * 		1.需实现允许投递空消息
 * 		2.最终msgContent需要转换为byte[]，建议直接使用默认实现toBytes方法--遵循原则：byte[]不转换，String直接取bytes，基本类型转为String后取bytes，其它转为json后取bytes
 * 
 * @author Xingzhe
 *
 */
@FunctionalInterface
public interface IMessageService {
	/**
	 * 	发送消息
	 * 
	 * @param type	消息类型
	 * @param code	消息类型编码（即小类）
	 * @param bid	业务（概念）id
	 * @param msgContent 消息内容，因mq通常是独立的第三方，最终将被转换为byte[]，调用方如果方便的话最好直接传byte[]
	 */
	void sendMessage(String type,String code,String bid,Object msgContent);

	/**
	 * 	系统类消息
	 */
	static final String MESSAGE_TYPE_SYSTEM="system";
	
	/**
	 * 	日志类消息
	 */
	static final String MESSAGE_TYPE_LOG="log";	
	/**
	 * 	登录，发送消息时bid为loginuserid
	 */
	static final String MESSAGE_CODE_LOGIN="login";
	/**
	 * 	登出，发送消息时bid为loginuserid
	 */
	static final String MESSAGE_CODE_LOGOUT="logout";
	/**
	 * 	session开始,bid为sessionid
	 */
	static final String MESSAGE_CODE_SESSION_START="sessionstart";
	/**
	 * 	session结束,bid为sessionid
	 */
	static final String MESSAGE_CODE_SESSION_END="sessionend";
	
	
	
	/**
	 * 	命令类消息，指第三方系统向项目要求提供某些信息的消息，如实时获取登录人数等，通常通过bid来区分批次
	 */
	static final String MESSAGE_TYPE_COMMAND="command";	
	/**
	 * 	回应类消息，应对COMMAND类消息，code、bid与COMMAND相同
	 */
	static final String MESSAGE_TYPE_REPLY="reply";
	
	// 以下code被设计为可与command与reply组合，一问一答
	
	/**
	 * 	获取系统实例，发送消息时msg在command时为空，reply时为当前应用程序实例标识（如ip）
	 */
	static final String MESSAGE_CODE_INSTANCE="instance";
	/**
	 * 	获取登录人员列表命令
	 */
	static final String MESSAGE_CODE_LOGINS="logins";
	/**
	 * 	获取在线人数命令
	 */
	static final String MESSAGE_CODE_LOGINCOUNT="login-count";
	
	/**
	 * 	获取会话列表
	 */
	static final String MESSAGE_CODE_SESSIONS="sessions";
	/**
	 * 	获取会话数量
	 */
	static final String MESSAGE_CODE_SESSIONCOUNT="session-count";
	
}
