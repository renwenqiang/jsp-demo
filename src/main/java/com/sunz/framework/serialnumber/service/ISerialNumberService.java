package com.sunz.framework.serialnumber.service;

import java.util.List;

import com.sunz.framework.serialnumber.SerialNumber;


/**
 *  通过请求配置返回特定的序列号
 *  
 * @author WH
 *
 */
public interface ISerialNumberService {
	
	/**
	 * 生成不动产规则号
	 * 
	 * @param userid 
	 * @param jobid 动态前缀sql所需要的参数
	 * @param rulekey 通过rulekey 查询编码配置
	 * @return 返回用户String
	 */
	public String generateNumber(String rulekey,String userid,String jobid) throws Exception;
	
	/**
	 * 是否与流程（jobid）无关的--如果是，意味着可以在无流程（jobid）上下文环境下调用
	 * @param rulekey
	 * @return
	 */
	boolean isJobIndependent(String rulekey);
	
	/**
	 * 获取所有规则定义
	 * @return
	 */
	List<SerialNumber> getAllDefine();
	/**
	 * 获取指定编码的规则定义
	 * 
	 * @param rulekey
	 * @return
	 */
	SerialNumber getDefine(String rulekey);
}
