package com.sunz.framework.message;

/**
 * 消息事件处理器
 * 
 * @author Xingzhe
 *
 */
@FunctionalInterface
public interface IOnMessageHandler {
	
	/**
	 * 收到消息事件
	 * @param topic 
	 * @param code
	 * @param bid
	 * @param msgContent
	 */
	void onMessage(String topic,String code,String bid,byte[] msgContent);
}
