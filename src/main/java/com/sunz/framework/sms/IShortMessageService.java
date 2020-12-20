package com.sunz.framework.sms;

import java.util.Map;

import com.sunz.framework.core.JsonResult;

/**
 * 	短信服务接口
 * 
 * 	本接口向着通用方向设计，并不刚好与某项目短信接口一致，
 * 	@remark 除基本的目标号码及内容外，其它信息通过扩展信息（extendInfo参数）传递，扩展信息依赖于项目实现==短信服务接口及项目要求
 * 	@remark 项目代码调用此接口传递扩展信息参数前，请认真阅读接口具体实现代码或其相关说明
 * 
 * @author Xingzhe
 *
 */
public interface IShortMessageService {
	/**
	 * 向指定的人发送短信
	 * 
	 * @param phoneNum
	 * @param content
	 * @param extendInfo 扩展信息
	 * @param log 是否需要在数据库中记录
	 */
	JsonResult send(String phoneNum,String content, Map<String,Object> extendInfo, boolean log);
	
	/**
	 * 向指定的多人发送短信
	 * 
	 * @param phoneNum
	 * @param content
	 * @param extendInfo 扩展信息
	 * @param log 是否需要在数据库中记录
	 */
	JsonResult batchSend(String[] phoneNum, String content, Map<String,Object> extendInfo, boolean log);
}
