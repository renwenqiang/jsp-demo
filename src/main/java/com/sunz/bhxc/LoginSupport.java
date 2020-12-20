package com.sunz.bhxc;

import com.sunz.framework.core.BaseLoginSupport;
import com.sunz.framework.query.QueryService;
import org.apache.log4j.Logger;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoginSupport extends BaseLoginSupport {

	protected Logger logger=Logger.getLogger(this.getClass());

	protected QueryService queryService;
	@Autowired
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	@Override
	public Object getExtendInfo(TSUser user) {
		Map m= (Map) super.getExtendInfo(user);
		try {
			Map mExtend= queryService.queryObject(null, "getUserExtendInfoBH", m);
			// mExtend存储的信息：
			// gh 工号
			// dw 单位 (针对除巡察组外其他)
			// zw 职务
			// insId 巡察组ID (针对除巡察组)
			m.putAll(mExtend);
		}catch(Exception e){
			logger.warn("获取扩展信息出错：", e);
		}
		
		return m;
	}
}
