package com.sunz.framework.core;

/**
 * 定义分面参数对象
 * 
 * @author Xingzhe
 *
 */
public class PageParameter {
	
	public static final String Key_Start="start";
	public static final String Key_Limit="limit";
	public static final String Key_Total="total";
	
	public static final PageParameter NoPaging=new PageParameter(){
		@Override
		public void setStart(int start) {
			throw new RuntimeException("NoPaging的start为只读属性");
		}
		@Override
		public void setLimit(int limit) {
			throw new RuntimeException("NoPaging的limit为只读属性");
		}
	};
	
	public PageParameter(){
		
	}
	public PageParameter(int start,int limit){
		this.setLimit(limit);
		this.setStart(start);		
	}
	public PageParameter(int start,int limit,int total){
		this.setLimit(limit);
		this.setStart(start);
		this.setStart(start);
	}
	
	int limit=-1;
	/**
	 * 返回记录上限
	 */
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	int start=-1;
	/**
	 * 起始记录数
	 */
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	int total=-1;
	/**
	 * 总记录数（传入表示已知总数，不进行总数计算，节省资源）
	 */
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	/**
	 * 是否需要分页
	 * 
	 * @return
	 */
	public boolean isPagingNeeded(){
		return start>0 || limit>0;
	}
	
	/**
	 * 是否需要计算总数
	 * 
	 * @return
	 */
	public boolean isTotalNeeded(){
		if(total>0)
			return false;
		
		return isPagingNeeded();
	}
	
	/**
	 * 获取实际起始值
	 * @return
	 */
	public int getRealStart(){
		 return start<0?0:start;
	}
	
	/**
	 * 获取实际限制值
	 * 
	 * @return
	 */
	public int getRealLimit(){
		return limit<0?Integer.MAX_VALUE:limit;
	}
	
	/**
	 * 获取结束记录
	 * 
	 * @return
	 */
	public int getEnd(){
		int s=getRealStart();
		return limit<0||s+limit<0/*即大于MaxValue*/?Integer.MAX_VALUE:(s+limit);
	}
	
	/**
	 * 转到“页”（0 based）
	 * 
	 * @return
	 */
	public int toPage(){
		return start<=0?0:(start/getRealLimit());
	}
}
