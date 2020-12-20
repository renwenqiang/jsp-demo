package com.sunz.framework.security.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ISecurityResolverHelper;
import com.sunz.framework.security.IUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;
import com.sunz.framework.util.StringUtil;

/**
 * 参数控制
 * 	  本身并不进行任何运算，仅重定向到另一个处理器
 * 	  可重复多级使用，每级使用&连接
 * 
 * @sample 本身不需要配置
 * @remark 本身作为根分发器，因此并不需要在配置中配置处理器
 * 
 * @author Xingzhe
 *
 */
@Component
public class ParamDistributeSecurityResolver extends BaseUriSecurityResolver  {
	private final String Type="paramdistribute";
	private class ParamInfo{
		public ParamInfo(String n,String v,String origString){
			this.name=n;
			this.value=v;
			this.queryString=origString;
		}
		public String name;
		public String value;
		public String queryString;
	};
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}	
	
	//private static Map<String,IUriSecurityResolver> pathBasedMap;
	private List<ParamInfo> configs;
	
	private ParamDistributeSecurityResolver createResolver(int type,String subpath,String setting,String err,List<ParamInfo> paramInfos,String id){
		ParamDistributeSecurityResolver newResoler=new ParamDistributeSecurityResolver();
		newResoler.setId(id);
		newResoler.configs=paramInfos;
		newResoler.init(type,subpath, err);
		newResoler.setSetting(setting);
		
		resolverHelper.addMapping(type,subpath,newResoler);
		
		return newResoler;
	}
		
	
	@SuppressWarnings("unchecked")
	//@PostConstruct
	public void loadConfig(){
		String sql="select PATH_ ,PARAM_ ,HANDLER_ ,SETTING_ ,ERRORMSG_ ,TYPE_ ,ID"
				+ " from T_S_SECURITYSETTING "
				+ " ORDER BY TYPE_,PATH_,RANK_,PARAM_ ASC";
		//allConfigs=jdbcTemplate.query(sql,(Map)null,new BeanPropertyRowMapper(ParamResolerConfig.class));
		jdbcTemplate.query(sql,new ResultSetExtractor(){
			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				//pathBasedMap=new HashMap<String, IUriSecurityResolver>();
				int oIndex=1,tIndex=2,hIndex=3,sIndex=4,errIndex=5,typeIndex=6;
				String lastDistributePath=null;
				List<ParamInfo> curInfos=null;
				//IUriSecurityResolver curResolver;
				while(rs.next()){
					String subpath =rs.getString(oIndex);
					
					if(subpath==null ||"".equals(subpath)) continue;// 错误配置
					
					String param=rs.getString(tIndex),
						   resolverType=rs.getString(hIndex),
						   setting=rs.getString(sIndex),
						   defaultError=rs.getString(errIndex),
						   resolverId=rs.getString(7);
					int type=rs.getInt(typeIndex);
					
					if(param!=null)param=param.trim();
					
					logger.debug(String.format("正在解析权限配置【%1$s】【%2$s】【%3$s】 \t【%4$s】 \t【%5$s】", type,subpath,param,resolverType,setting));
					// 解决本身
					// 支持将param直接写到path
					String[] pathParts=subpath.split("\\?|&");
					if(pathParts.length==2){   // 1.不包含连接符，不处理 3.多个连接符，不支持，因为需要跳转多级
						String basePath=pathParts[0];
						String innerParam=pathParts[1] /* 不需要连接param，因为param自有其本身计算 +(param==null||"".equals(param)?"":(Char_Union+param))*/;
						if(innerParam !=null && !"".equals(innerParam)){
							IUriSecurityResolver resolver=resolverHelper.getResolver(type,basePath);
							if(null==resolver){							
								subpath=basePath+Char_Union+innerParam; // 对subpath赋值是为较正？号	
								List<ParamInfo> innerInfos=new ArrayList<ParamInfo>();
								createResolver(type,basePath,null,null,innerInfos,"Auto_"+basePath+type);
								addParam(innerInfos, innerParam);
								
							}else if(resolver instanceof ParamDistributeSecurityResolver){
								ParamDistributeSecurityResolver pResolver=(ParamDistributeSecurityResolver) resolver;
								addParam(pResolver.configs, innerParam);
							}
						}
					}
					
					if(Type.equals(resolverType)){ // 明确指定为此类处理器，则setting是有意义的	
						createResolver(type,subpath,setting,defaultError,curInfos=new ArrayList<ParamInfo>(),resolverId);
						lastDistributePath=subpath;
					}else{
						String solverKey=subpath+(param==null||"".equals(param)?"":(Char_Union+param));
						// 创建当前配置的处理器（指向另一个处理器）
						IUriSecurityResolver resolver=resolverHelper.createResolver(type,resolverType, solverKey, setting, defaultError,resolverId);
						resolverHelper.addMapping(type, solverKey, resolver);
					}
					
					if(param!=null&&!"".equals(param)/* 即使是*即应当建立分发器 &&!Char_WildCard.equals(param)*/){
						if(!subpath.equals(lastDistributePath)){
							createResolver(type,subpath,null,null,curInfos=new ArrayList<ParamInfo>(),"Auto_"+subpath+type);
						}
						// 分解Param
						// 仅允许转发1个，原因是与/或关系无法计算
						String[] kv=param.split(Char_Split);
						curInfos.add( new ParamInfo(kv[0], kv.length>1?kv[1]:null,param));						
						
						lastDistributePath=subpath;
					}
				}
				
				return null;
			}
		});
	}
	private void addParam(List<ParamInfo> curInfos,String param){
		for(ParamInfo info:curInfos) {	// 2019-12-31 去重
			if(info.queryString.equals(param))
				return;
		}
		String[] kv=param.split(Char_Split);
		curInfos.add(new ParamInfo(kv[0], kv.length>1?kv[1]:null,param));
	}
	public void setSetting(String arrString) {
		if(this.configs==null)
			this.configs=new ArrayList<ParamInfo>();
	
		if(arrString==null||"".equals(arrString))
			return;
		
		String[] arr= StringUtil.parseToArray(arrString);
		for(String config:arr){
			addParam(this.configs, config);
		}
	}

	public ValidateInfo validate(HttpServletRequest request) {
		// 无值仅判断参数有无，有值则要求一致
		IUriSecurityResolver resolver=null;
		for(int i=0;i<configs.size();i++){
			ParamInfo pinfo=configs.get(i);
			if((pinfo.value==null||pinfo.value.equals(""))?request.getParameter(pinfo.name)!=null //值为空，只要存在这个参数即可
				:pinfo.value.equals(request.getParameter(pinfo.name))){
					resolver=resolverHelper.getResolver(this.securityType/*resolverHelper.getSecurityType(request)*/, this.path+Char_Union+pinfo.queryString);
					break;
			}
		}
		// 尝试找到通配的处理器
		if(resolver==null){
			resolver=resolverHelper.getResolver(this.securityType/*resolverHelper.getSecurityType(request)*/, this.path+Char_Union+Char_WildCard);
		}
		if(resolver!=null)
			return resolver.validate(request);
		
		// 分发本身不应返回结果：无分发即不控制
		// 但是不控制在type为0时是不让通过的
		if(ISecurityResolverHelper.SecurityType_LoginRequired==this.securityType/*resolverHelper.getSecurityType(request)*/)
			return new ValidateInfo(true,this.path);
		
		return new ValidateInfo(false, request.getContextPath()+request.getQueryString(), this.error==null||"".equals(this.error)?Error_Empty:this.error);
		//return this.error==null||"".equals(this.error)?Error_Empty:this.error;
	}
	
	public String getType(){
		return Type;
	}

	//
	@Override
	public IUriSecurityResolver create(int type,String path,String defaultError,String setting,String id){
		return createResolver(type,path, setting,defaultError,null,id);
	}
	
	public void refresh(){
		loadConfig();
	}

}
