package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;
import com.sunz.framework.util.StringUtil;

/**
 * 单个参数控制：
 * 		配置的参数为放行的关键参数（即Controller下的方法名），进行有无比对
 * 
 * @sample login.do?validate&userName=123&password=456要被放行的话，配置为
 * 		 path:login.do
 * 		 handler:simpleparam  --即SingleParamSecurityResoler
 * 		 setting:validate	  --即单个关键参数
 * 
 * @remark 	1.使用request的getParameterValues进行比对--据jdk注释，无指定的参数返回null（否则为数组）
 * 			2.无配置认为所有子路径不允许通过
 * 
 * @author Xingzhe
 *
 */
@Component
public class SingleParamSecurityResolver extends BaseUriSecurityResolver {

	private String[] excudeParams;
	public void setSetting(String arrString) {
		excudeParams=StringUtil.parseToArray(arrString);
	}

	public ValidateInfo validate(HttpServletRequest request) {
		if(excudeParams!=null){
			for(String pkey:excudeParams){
				if(request.getParameterValues(pkey)!=null) 
					return new ValidateInfo(true, this.path);
			}
		}
		
		return new ValidateInfo(false,this.path,this.error==null||"".equals(this.error)?Error_Empty:this.error);
	}

	public String getType(){
		return "singleparam";
	}
}
