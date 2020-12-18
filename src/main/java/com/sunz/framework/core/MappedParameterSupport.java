package com.sunz.framework.core;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 	为指定请求收集“内置”参数（即非前端传过来的参数，当参数比较常用而sql获取又消耗较大或不方便从前端传递时）形成Map（主要用于NamedParameterJdbcTemplate方法)
 * 
 * @author Xingzhe
 *
 */
public abstract class MappedParameterSupport {

	private ILoginSupport loginSupport;
	@Autowired(required=false)
	public void setLoginSupport(ILoginSupport loginSupport) {
		this.loginSupport = loginSupport;
	}
	protected ILoginSupport getLoginSupport() {
		if(loginSupport==null){
			loginSupport=new BaseLoginSupport();
		}
		return loginSupport;
	}
	protected String getIp(HttpServletRequest request) { 
	    String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("PRoxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}
	
	/**
	 * 将指定字段的值转换为数字（Integer）
	 * 主要是分页参数
	 * @param map
	 * @param field
	 * @param defValue
	 */
	protected void convertNumber(Map<String,Object> map,String field,int defValue){
		String  v=(String) map.get(field);
		if(v==null)
			return ;
		
		if("".equals(v)){
			map.put(field,defValue);
		}else{
			map.put(field, Integer.parseInt(v));
		}
	}
	/**
	 * 转换内置对象到字段
	 * 如当前时间等
	 * @param map
	 * @param innerData 内置对象
	 * @param convertTo 字段
	 */
	protected void convertInner(Map<String,Object> map,Object innerData,String[] convertTo){
		if(convertTo!=null && convertTo.length>0){
			for(String field:convertTo){
				if(field==null ||"".equals(field)) continue;
				for(String f:field.split(",")){
					if(f==null||"".equals(f)) continue;
						map.put(f,innerData);
				}
			}
		}
	}
	
	public final Map<String,Object> getMappedParameter(HttpServletRequest request){
		Map<String,Object> mResult=new HashMap<String,Object>();
		
		Map<String, String[]> mRaw= request.getParameterMap();
		Iterator<String> iter= mRaw.keySet().iterator();
		while (iter.hasNext()) {
			String dKey=iter.next();
			String[] values=mRaw.get(dKey);
			if(values.length==0){
				mResult.put(dKey, null);
			}else if(values.length==1){
				mResult.put(dKey, values[0]);
			}else{
				mResult.put(dKey, values);
			}
		}
		// 分页参数数据类型转换
		convertNumber(mResult, PageParameter.Key_Limit, -1);
		convertNumber(mResult, PageParameter.Key_Start, -1);
		convertNumber(mResult, PageParameter.Key_Total, -1);
		
		// 当前时间
		convertInner(mResult,new Date(),request.getParameterValues("timeFields"));
		// 顺便把访问IP
		String[] ipFields=request.getParameterValues("ipFields");
		if(ipFields !=null && ipFields.length>0)
			convertInner(mResult, getIp(request), ipFields);
		
		
		// 再加入用户信息
		TSUser user=(TSUser)getLoginSupport().getLoginUser(request); 
		if(user!=null){
			mResult.put(ILoginSupport.Session_ParameterName_User, user);
			mResult.put(ILoginSupport.Session_ParameterName_Extend, getLoginSupport().getLoginExtendInfo(request));
			mResult.put("loginuserid", user.getId());
			mResult.put("loginuserguid", user.getId()); // 兼容原来代码
			mResult.put("loginusername",user.getUserName());
			
			if(user.getRoles()!=null){
				String[] roles=new String[user.getRoles().size()];
				for(int i=0;i<user.getRoles().size();i++){					
					roles[i]=user.getRoles().get(i)==null?"":user.getRoles().get(i).getRoleCode();
				}
				mResult.put("loginuserroles", roles);
			}
		}

		this.setExtendMappedParameter(mResult, request);
		// 增加规则可将内部（实际也可是外部）信息映射到指定参数
		Enumeration<String> enNames= request.getParameterNames();
		if(enNames!=null){
			while(enNames.hasMoreElements()){
				String iname=enNames.nextElement();
				if(iname.indexOf("_Fields")>1){
					String sname=iname.substring(0,iname.indexOf("_Fields"));
					if(mResult.containsKey(sname)){
						convertInner(mResult, mResult.get(sname), request.getParameterValues(iname));
					}
				}
			}
		}
		
		return mResult;
	}
	/**
	 * 向Map中添加[用于如sql等运算的]参数
	 * 
	 * @param map
	 * @param request
	 */
	protected abstract void setExtendMappedParameter(Map<String,Object> map,HttpServletRequest request);
}
