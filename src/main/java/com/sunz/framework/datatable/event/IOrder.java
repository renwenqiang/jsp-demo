package com.sunz.framework.datatable.event;

/**
 * 排序
 * 
 * @author Xingzhe
 */
public interface IOrder {
	/**
	 * 排序值，越小越靠前，默认100，实现类可参考此值排前或排后
	 * @return
	 */
	default int getOrder() {return 100;}
}
