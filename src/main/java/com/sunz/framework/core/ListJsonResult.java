package com.sunz.framework.core;

import java.util.List;


/**
 * 列表Json返回结构
 * 
 * @author Xingzhe
 *
 */
public class ListJsonResult extends JsonResult {
	public ListJsonResult(){}
	

	public ListJsonResult(boolean success){
		super(success);
	}
	
	public ListJsonResult(String err){
		super(err);
	}
	
	public ListJsonResult(List data){
		this.data=data;
		if(data!=null)
			this.total=data.size();
	}
	
	public ListJsonResult(List data,int total){
		this.data=data;
		this.total=total;
	}
	
	public ListJsonResult(String err,List data,int total){
		super(err);
		this.data=data;
		this.total=total;
	}
	
	int total;
	/**
	 * 数据总数
	 */
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	/**
	 * 返回data，强制转换为List
	 *    注：不能使用getList，因为加get后会Json化
	 *  
	 * @return
	 */
	public List list(){
		return (List)data;
	}
}
