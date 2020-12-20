package com.sunz.framework.datatable.event;

/**
 * 对datatable的【所有方法】使用事件进行拦截、监听
 * 
 * @remark 若有先后关系，实现@see::IOrder即可
 *  
 * @author Xingzhe
 */
public interface IDatatableEventsHandler extends IAddHandler, IBeforeAddHandler, IBeforeSaveHandler, ISaveHandler, IBeforeDeleteHandler, IDeleteHandler, IBeforeQueryHandler, IQueryHandler {
	
}
