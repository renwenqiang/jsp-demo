package com.sunz.framework.core.util;

import java.sql.ResultSet;

/**
 * 数据行处理器
 * --用于处理大量数据，避免生成内存数据集合
 * 
 * @author Xingzhe
 *
 */
@FunctionalInterface
public interface IDataRowHandler {
	
	/**
	 *	处理数据行
	 * 
	 * @param rs 原生ResultSet对象，原则上不需要[实现类]去next和close，而由调用方处理
	 */
	void handleRow(ResultSet rs);
}
