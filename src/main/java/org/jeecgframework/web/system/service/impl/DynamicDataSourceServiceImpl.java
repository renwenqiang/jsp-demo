package org.jeecgframework.web.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jeecgframework.core.common.service.impl.BaseService;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.web.system.service.DynamicDataSourceServiceI;
import org.jeecgframework.web.system.pojo.base.DynamicDataSourceEntity;


@Service("dynamicDataSourceService")
@Transactional
public class DynamicDataSourceServiceImpl extends BaseService implements DynamicDataSourceServiceI {

	/**初始化数据库信息，TOMCAT启动时直接加入到内存中**/
	public List<DynamicDataSourceEntity> initDynamicDataSource() {
		DynamicDataSourceEntity.DynamicDataSourceMap.clear();

		List<DynamicDataSourceEntity> dynamicSourceEntityList = this.commonDao.loadAll(DynamicDataSourceEntity.class);

		for (DynamicDataSourceEntity dynamicSourceEntity : dynamicSourceEntityList) {
			DynamicDataSourceEntity.DynamicDataSourceMap.put(dynamicSourceEntity.getDbKey(), dynamicSourceEntity);
		}
		return dynamicSourceEntityList;
	}

	public static DynamicDataSourceEntity getDbSourceEntityByKey(String dbKey) {
		DynamicDataSourceEntity dynamicDataSourceEntity = DynamicDataSourceEntity.DynamicDataSourceMap.get(dbKey);

		return dynamicDataSourceEntity;
	}

	public void refleshCache() {
		initDynamicDataSource();
	}

	//add-begin--Author:luobaoli  Date:20150620 for：增加通过数据源Key获取数据源Type
	@Override
	//update-begin--Author:luobaoli  Date:20150623 for：增加通过数据源Key获取数据源
	public DynamicDataSourceEntity getDynamicDataSourceEntityForDbKey(String dbKey){
		List<DynamicDataSourceEntity> dynamicDataSourceEntitys = commonDao.findHql("from DynamicDataSourceEntity where dbKey = ?", dbKey);
		if(dynamicDataSourceEntitys.size()>0)
			return dynamicDataSourceEntitys.get(0);
		return null;
	}
	//update-end--Author:luobaoli  Date:20150623 for：增加通过数据源Key获取数据源
	//add-end--Author:luobaoli  Date:20150620 for：增加通过数据源Key获取数据源Type

}