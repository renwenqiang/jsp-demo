package com.sunz.framework.security.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;
import com.sunz.framework.util.StringUtil;

/**
 * 简单参数控制：
 * 		配置的参数为参数url格式（字符串数组），进行有无和值比对
 * 
 * @sample a&b=1&x=y&...
 * 
 * @remark 	1.此类被预计为比较常用的，当前性能较一般
 * 			2.无配置认为所有子路径不允许通过
 * 
 * @author Xingzhe
 *
 */
@Component
public class SimpleParamSecurityResolver extends BaseUriSecurityResolver {

	private class ParamInfo{
		public ParamInfo(String n,String v){
			this.name=n;
			this.value=v;
		}
		public String name;
		public String value;
	};
	
	private ParamInfo[][] configs;
	
	public void setSetting(String arrString) {
		// ["a&b=1&x=y&...",...]
		// a&b=1&x=y&...,a&b=1&x=y&...
		// a&b=1&x=y&...|a&b=1&x=y&...
		
		String[] all=StringUtil.parseToArray(arrString);
		if(all==null|| all.length==0)
			return;
		
		configs=new ParamInfo[all.length][];
		for(int index=0;index<all.length;index++){
			String[] config=all[index].split(Char_Union);
			ParamInfo[] infos=new ParamInfo[config.length];
			for(int i=0;i<infos.length;i++){
				String[] kv=config[i].split(Char_Split);
				infos[i]= new ParamInfo(kv[0], kv.length>1?kv[1]:null);
			}
			configs[index]=infos;
		}
	}

	public ValidateInfo validate(HttpServletRequest request) {
		if(configs!=null){	
			for(int i=0;i<configs.length;i++){
				ParamInfo[] config=configs[i];
				boolean isMatch=true; // 排除法，因为多个条件间是and的关系
				for(int j=0;j<config.length;j++){
					ParamInfo pinfo=config[j];
					// 无值仅判断参数有无，有值则要求一致
					if((pinfo.value==null||pinfo.value.equals(""))?
							request.getParameterValues(pinfo.name)==null
							:!pinfo.value.equals(request.getParameter(pinfo.name))){
						isMatch=false;
						break;
					}
				}
				if(isMatch) return new ValidateInfo(true, this.path);
			}	
		}
		return new ValidateInfo(false,this.path,this.error==null||"".equals(this.error)?Error_Empty:this.error);
	}

	public String getType(){
		return "simpleparam";
	}
}
