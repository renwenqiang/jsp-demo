package com.sunz.framework.security.resolver;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.query.helper.SqlParser;
import com.sunz.framework.security.BaseUriSecurityResolver;
import com.sunz.framework.security.ValidateInfo;

/**
 * 简单路径控制：凡使用此解决器的直接放行
 * 
 * @author Xingzhe
 *
 */
@Component
public class SqllySecurityResolver extends BaseUriSecurityResolver {

	/*private static IQueryService queryService;
	@Autowired
	public void setQueryService(IQueryService queryService) {
		SqllySecurityResolver.queryService = queryService;
	}*/
	
	private static NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		SqllySecurityResolver.jdbcTemplate = jdbcTemplate;
	}
	
	private String freemarkerSql;
	public void setSetting(String fmSql) {
		this.freemarkerSql=fmSql;
	}
	
	public ValidateInfo validate(HttpServletRequest request) {
		if(freemarkerSql==null||"".equals(freemarkerSql))
			return new ValidateInfo(true, this.path);	//
					
		try{
			Map param=toMap(request);
			
			String sql= SqlParser.parseFreemarkerSql(freemarkerSql, param, this.getId());
			if(sql==null||"".equals(sql))	// freemark 先过滤一层
				return new ValidateInfo(true, this.path);
			
			// 此时sql已经不是freemarkerSql了
			//String msg =(String) queryService.queryObject(sql, param, String.class, "_security_sql_"+this.path);
			String msg=(String) jdbcTemplate.queryForObject(sql, param, String.class);
			
			return new ValidateInfo(msg==null||"".equals(msg),this.path, msg);
		}catch(Exception e){
			logger.error("基于sql语句的安全控制运算出错，"+this.path+"被限制访问",e);
		}
		return new ValidateInfo(false, this.path, "内部（安全控制）错误");
	}	

	public String getType(){
		return "sql";
	}
	
}
