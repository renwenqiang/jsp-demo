package org.jeecgframework.core.common.service.impl;

import org.jeecgframework.core.common.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
	protected ICommonDao commonDao;
	@Autowired
	public void setCommonDao(ICommonDao commonDao) {
		this.commonDao = commonDao;
	}
}
