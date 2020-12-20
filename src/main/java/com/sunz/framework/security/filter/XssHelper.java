package com.sunz.framework.security.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.util.StringUtil;

@Component
public class XssHelper implements ICacheRefreshable{
	static Logger logger = Logger.getLogger(XssHelper.class);
	private static final int Type_Black=1,
					  		 Type_White=0;
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	private class ParamInfo{
		public ParamInfo(String param) {
			//this.queryString=param;
			String[] kv=param.split("=");
			this.name=kv[0];
			this.value=kv.length>1?kv[1]:null;
		}
		public ParamInfo(String n,String v,String origString){
			this.name=n;
			this.value=v;
			//this.queryString=origString;
		}
		public String name;
		public String value;
		//public String queryString;
	};
	private class XssConfig{
		public XssConfig(ParamInfo[] curInfos,String fieldString,int type) {
			this.paramInfos=curInfos;
			this.fields=StringUtil.parseToArray(fieldString);
			this.type=type;
		}
		
		public ParamInfo[] paramInfos;
		public String[] fields;
		public int type;
	}
	private static Map<String,List<XssConfig>> configMapping=new HashMap<String, List<XssConfig>>();
	
	private static XssConfig getMatchConfig(ServletRequest request) {
		String uri=((HttpServletRequest) request).getServletPath().substring(1);
		logger.debug("XSS为接口"+uri+"查找配置...");
		if(configMapping.containsKey(uri)){
			List<XssConfig> configs=configMapping.get(uri);
			for(XssConfig c:configs) {
				if(c.paramInfos!=null && c.paramInfos.length>0) {
					for(ParamInfo info: c.paramInfos) {
						if((info.value==null||info.value.equals(""))?request.getParameter(info.name) !=null //值为空，只要存在这个参数即可
							: info.value.equalsIgnoreCase(request.getParameter(info.name))) {
							logger.debug("XSS为接口"+uri+"找到规则："+c.type+"->"+info.name+((info.value==null||info.value.equals(""))?"":("="+info.value)));
							return c;
						}
					}
				}
			}
			return configs.get(configs.size()-1);
		}
		return null;
	}
	public static void filter(HttpServletRequest request,IXssValidator handler) {
		// 无配置，全局检验
		// 白名单：仅较验fields（其余放行），无fields直接放行
		// 黑名单：放行fields（其余全部较验）,无fields全部较验
		String[] includes=null,
				excluces=null;
		XssConfig c=getMatchConfig(request);
		if(c!=null) {
			if(c.type==Type_White) {
				if(c.fields==null|| c.fields.length==0)
					return;
				includes=c.fields;
			}else {
				excluces=c.fields;
				/*if(c.fields==null|| c.fields.length==0) {
					excluces=c.fields;
				}else {
					return;
				}*/
			}
		}
		if(includes!=null) {
			for(String field:includes) {
				if(handler.isXss(request, field))
					return;
			}
		}else{
			if(excluces!=null && excluces.length>0) {
				Enumeration<String> en=request.getParameterNames();
				while (en.hasMoreElements()) {
					String field= en.nextElement();
					for(int i=0,len=excluces.length;i<len;i++) {
						if(field.equals(excluces[i])) {
							if(handler.isXss(request, field))
								return;
							
							break;
						}
					}
				}
			}
		}
	}
	
	@PostConstruct
	public void init() {
		configMapping.clear();
		String sql="SELECT PATH_, PARAMNAMES_, TYPE_, ID from T_S_SECURITY_XSS ORDER BY RANK_ ASC";
		jdbcTemplate.query(sql,new ResultSetExtractor(){
			@Override
			public Object extractData(ResultSet rs) throws SQLException,DataAccessException {
				while(rs.next()){
					String path=rs.getString(1),
						   fields=rs.getString(2);
					int type=rs.getInt(3);
					
					String[] urlParts=path.split("\\?|&");				
					String uri=urlParts[0];
					int len=urlParts.length;
					ParamInfo[] curInfos=new ParamInfo[len-1];
					for(int i=1;i<len;i++) {
						curInfos[i-1]=new ParamInfo(urlParts[i]);
					}
					List<XssConfig> configs=configMapping.containsKey(uri)?configMapping.get(uri):new ArrayList<XssConfig>();
					configs.add(new XssConfig(curInfos, fields, type));
					configMapping.put(uri, configs);
				}
				return null;
			}
		});
	}

	@Override
	public void refresh(String item) {
		init();
	}

	@Override
	public String getCategory() {
		return "xssconfig";
	}

	@Override
	public String getDescription() {
		return "XSS配置（不支持单项刷新）";
	}	
	
}
