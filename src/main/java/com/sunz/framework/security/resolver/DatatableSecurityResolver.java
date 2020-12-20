package com.sunz.framework.security.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Datatable.do专用处理器
 * 		本身并不进行任何运算，仅重定向到另一个处理器
 * 
 * @author Xingzhe
 *
 */
@Component
public class DatatableSecurityResolver extends BaseUriSecurityResolver /*implements ICacheRefreshable*/ {

	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}	
	
	/**
	 * 记录各table的operate顺序
	 * @remark 必须包含所有operate的定义，否则通配不起作用
	 */
	private Map<String,List<String>> tableOperates;
	static Map<String,List<String>> tableOperatesOpen=new HashMap<String, List<String>>(),
									tableOperatesLogined=new HashMap<String, List<String>>();
	
	
	/**
	 * 记录通配的operate顺序
	 */
	private List<String> wildcardOperates=new ArrayList<String>();
	static List<String> wildcardOperatesOpen=new ArrayList<String>(),
						wildcardOperatesLogined=new ArrayList<String>();
	/**
	 * 反向通配
	 */
	private List<String> revertWildcardList;
	static List<String> revertWildcardListOpen=new ArrayList<String>(),
						revertWildcardListLogined=new ArrayList<String>();
	
	//@PostConstruct
	@SuppressWarnings("unchecked")
	public void loadConfig(){
		jdbcTemplate.query("select OPERATE_,TABLENAME_,HANDLER_,SETTING_,ERRORMSG_,TYPE_,ID from T_S_SECURITY_DATATABLE ORDER BY TYPE_,TABLENAME_,RANK_ ASC",new ResultSetExtractor(){

			@Override
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				tableOperatesOpen.clear();
				tableOperatesLogined.clear();
				wildcardOperatesOpen.clear();
				wildcardOperatesLogined.clear();
				revertWildcardListOpen.clear();
				revertWildcardListLogined.clear();
				
				int oIndex=1,tIndex=2,hIndex=3,sIndex=4,errIndex=5,typeIndex=6;
				
				List<String> curOperates=null;
				String curTable=null;
				while(rs.next()){
					String operate =rs.getString(oIndex),
						   tableName=rs.getString(tIndex),
						   resolverType=rs.getString(hIndex),
						   setting=rs.getString(sIndex),
						   defaultError=rs.getString(errIndex);
					int type=rs.getInt(typeIndex);
					
					if(operate!=null)operate=operate.trim();
					if(tableName==null){
						tableName=Char_WildCard;
					}
					else{
						tableName=tableName.trim().toUpperCase();						
					}
					/**
					 * tablename={} operate=* 写法
					 */
					if(Char_WildCard.equals(operate)){
						if(!tableName.equals(Char_WildCard)) // *+*不能注册给反向
							(type==ISecurityResolverHelper.SecurityType_Open?revertWildcardListOpen:revertWildcardListLogined).add(tableName);
					}else{
						if(tableName.equals(Char_WildCard)){
							(type==ISecurityResolverHelper.SecurityType_Open?wildcardOperatesOpen:wildcardOperatesLogined).add(operate);
						}else{
							if(!tableName.equals(curTable)){
								curOperates=new ArrayList<String>();
								(type==ISecurityResolverHelper.SecurityType_Open?tableOperatesOpen:tableOperatesLogined).put(curTable=tableName, curOperates);
							}
							if(!curOperates.contains(operate))
								curOperates.add(operate);
						}
					}
					
					String solverKey=operate+Char_Union+tableName;
					IUriSecurityResolver resolver= resolverHelper.createResolver(type,resolverType, solverKey, setting, defaultError,rs.getString(7));
					resolverHelper.addMapping(type, solverKey, resolver);					
				}
				// 完成后要对各表的operates
				
				return null;
			}
		});
	}
	
	public void setSetting(String paramString) {
		// 从独立的表读取配置
	}

	private String findOperate(HttpServletRequest request ,List<String> curOperates){
		for(int i=0;i<curOperates.size();i++){
			String operate=curOperates.get(i);
			if(request.getParameterValues(operate)!=null){				
				return operate;
			}
		}
		return null;
	}
	public ValidateInfo validate(HttpServletRequest request) {
		String tableName=request.getParameter("t");
		if(tableName==null)
			tableName=Char_WildCard;
		else 
			tableName=tableName.toUpperCase();
		
		// 尝试找到处理器
		// 1.table+operate优先
		// 2.table+* 通配
		// 3.全局table通配（*+operate）
		// 4.全局通配（*+*）
		IUriSecurityResolver resolver=null;
		List<String> curOperates=this.tableOperates.get(tableName);
		String operate=curOperates==null?null:findOperate(request,curOperates);
		if(operate!=null){	// 能找到相应的operate即意味着有相应的resolver定义
			resolver=resolverHelper.getResolver(this.securityType/*resolverHelper.getSecurityType(request)*/, operate+Char_Union+tableName);
		}else{
			/**
			 * tablename=x  operate=* 写法
			 */
			if(this.revertWildcardList.contains(tableName)){
				resolver=resolverHelper.getResolver(this.securityType/*resolverHelper.getSecurityType(request)*/, Char_WildCard+Char_Union+tableName);
			}else{
				operate=findOperate(request,this.wildcardOperates);
				// 支持 *+*写法
				if(operate==null)
					operate=Char_WildCard;
				/**
				 * 全局通配在反向通配之后
				 */
				resolver=resolverHelper.getResolver(this.securityType/*resolverHelper.getSecurityType(request)*/, operate+Char_Union+Char_WildCard);
			}
		}
		if(resolver!=null)
			return resolver.validate(request);
		
		return new ValidateInfo(false,request.getRequestURI(), this.error==null||"".equals(this.error)?Error_Empty:this.error);
	}
	
	public String getType(){
		return "datatable";
	}

	//
	@Override
	public IUriSecurityResolver create(int type,String path,String defaultError,String setting,String id){
		DatatableSecurityResolver resolver= new DatatableSecurityResolver();
		resolver.setId(id);
		resolver.init(type, path, defaultError);
		resolver.setSetting(setting);
		// 关键代码
		resolver.tableOperates=(type==ISecurityResolverHelper.SecurityType_Open?tableOperatesOpen:tableOperatesLogined);
		resolver.revertWildcardList=(type==ISecurityResolverHelper.SecurityType_Open?revertWildcardListOpen:revertWildcardListLogined);
		resolver.wildcardOperates=(type==ISecurityResolverHelper.SecurityType_Open?wildcardOperatesOpen:wildcardOperatesLogined);				
		
		return resolver;
	}
	public void refresh(){
		loadConfig();
	}

	//
	public void refresh(String item) {
		loadConfig();
	}

	public String getCategory() {		
		return "DatatableSecurity";
	}

	public String getDescription() {
		return "基于datatable.do的安全控制配置";
	}
	
}
