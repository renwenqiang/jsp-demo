package com.sunz.framework.auth.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.auth.AuthTagHelper;
import com.sunz.framework.auth.IAuthFilter;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.ILoginSupport;


/**
 * 预计可能需要的跟 角色 挂钩的权限过滤
 * @author Xingzhe
 *
 */
@Component
public class RoleAuthFitler implements IAuthFilter,ICacheRefreshable{
	
	private ILoginSupport loginSupport;	
	@Autowired
	public void setLoginSupport(ILoginSupport loginSupport) {
		this.loginSupport = loginSupport;
	}
	
	private NamedParameterJdbcTemplate template;
	@Autowired
	public void setTemplate(NamedParameterJdbcTemplate template) {
		this.template = template;
	}
	
	private Map<String, String> m_dict;
	private final String Char_Joinner="_";	
	@PostConstruct
	public void cache(){
		m_dict=new HashMap<String, String>();
		List all=template.queryForList("select tr.page \"page\",tr.code \"code\",trs.roleid \"rolecode\",trs.controltype \"controltype\""
				+ " from T_S_UIResource_Role trs "
				+ " inner join T_S_UIResource tr on tr.id=trs.resourceid "
				//+ " inner join T_S_ROLE tro on tro.rolecode=trs.roleid "
				,(Map) null);
		
		for(Object o:all){
			Map<String,String> m=(Map<String,String>) o;
			String key=m.get("page")+Char_Joinner+m.get("code")+Char_Joinner+m.get("rolecode");
			m_dict.put(key, m.get("controltype"));
		}		
	}
	
	@Override
	public String getType() {
		return "role";
	}
	
	@Override
	public String getControlType(String resourceCode, String params,
			HttpServletRequest request) {
		
		TSUser user=loginSupport.getLoginUser(request);
		if(user==null)
			throw new RuntimeException("无法确定当前连接的用户身份，而页面上下文需要登陆用户[角色]信息！");
		
		String page=AuthTagHelper.getJspPath(request);
		
		// 后续做缓存优化
		/*
		String sql="select controltype from T_S_UIResource_Role trs "
				+ " inner join T_S_UIResource tr on tr.id=trs.resourceid "
				+ " inner join T_S_ROLE tro on tro.rolecode=trs.roleid "
				+ " where tr.code =:code and tr.page=:page "
				+ " 	  and tro.id in (select roleid from t_s_role_user where userid=:userid)";
		Map map=new HashMap();
		map.put("userid", user.getId());
		map.put("page", page);
		map.put("code", resourceCode);
		
		// forObject要求有且仅有一条记录
		List<String> result= template.queryForList(sql, map, String.class);
		return result.size()>0?result.get(0):null;*/
		
		// roles循环
		//return m_dict.get(page+Char_Joinner+resourceCode+Char_Joinner+)
		String ctype=null;
		for(TSRole role:user.getRoles()){
			ctype= m_dict.get(page+Char_Joinner+resourceCode+Char_Joinner+role.getRoleCode());
			if(ctype!=null)
				break;
		}
		return ctype;
	}
	
	
	@Override
	public void refresh(String item) {
		cache();
	}

	@Override
	public String getCategory() {
		return "RoleAuth";
	}

	@Override
	public String getDescription() {
		return "基于登陆用户角色的权限控制缓存(因为数量级和缓存key的复杂性，不支持单项刷新)";
	}

}
