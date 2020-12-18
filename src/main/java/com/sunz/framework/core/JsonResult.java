package com.sunz.framework.core;

/**
 * 定义json返回类型的数据结构
 * 
 * @author Xingzhe
 *
 */
public class JsonResult {
	
	/**
	 * 提供一个success返回true的空值
	 * 注意： 请不要更改此对象的值
	 */
	public static final JsonResult Success=new FinalResult(true);
	/**
	 * 提供一个success返回false的空值
	 * 注意： 请不要更改此对象的值
	 */
	public static final JsonResult Fail=new FinalResult(false);
	
	/**
	 * 设置msg，并将success置为false
	 * 
	 * @param err
	 */
	public void setError(String err){
		this.msg=err;
		this.success=false;
	}
	
	public JsonResult() {
	}

	/**
	 * 
	 * @param data
	 */
	public JsonResult(Object data){
		this.data=data;
	}
	/**
	 * 将自动判断err
	 * 
	 * @param err 
	 */
	public JsonResult(String err){
		this.success=(err==null || "".equals(err));
		this.msg=err;
	}
	/**
	 * 仅指定是否出错
	 * 
	 * @param success
	 */
	public JsonResult(boolean success){
		this.success=success;
	}
	
	/**
	 * 将自动判断err
	 * 
	 * @param err 
	 * 
	 * @param data
	 */
	public JsonResult(String err,Object data){
		this.success=(err==null || "".equals(err));
		this.data=data;
		this.msg=err;
	}
	
	
	boolean success=true;
	/**
	 * 是否正确[执行业务]
	 */
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	String msg;
	/**
	 * 错误描述
	 */
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	Object data;
	/**
	 * 返回[给前台的]结果
	 */
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
class FinalResult extends JsonResult{
	public FinalResult(boolean finalSuccess){
		this.finalSuccess=finalSuccess;
	}
	@Override
	public String getMsg(){
		return null;
	}
	private boolean finalSuccess;
	@Override
	public boolean isSuccess() {
		return finalSuccess;
	}
	@Override
	public Object getData(){
		return null;
	}
}