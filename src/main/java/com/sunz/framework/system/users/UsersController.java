package com.sunz.framework.system.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.apache.log4j.Logger;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSUserOrg;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;


/**
 * @author hyp
 * 日期 2017年4月2日
 * 用途 TODO  提供获取系统部门，用户的帮助类
 */
@Scope("prototype")
@Controller
@RequestMapping("framework/usersController")
public class UsersController extends BaseController {
	private String message;

	public String getMessage() {
		return message;
	}
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
 
	/**
	 * @Title: UserController.java
	 * @Description: 获取所有职能部门信息
	 * @author hyp
	 * @date 下午12:22:04
	 * @version V1.0
	 */
	@RequestMapping(params = "getDeparts")
	@ResponseBody
	public JsonResult getDeparts() {
		JsonResult js = new JsonResult();
		List<TSDepart> ts = commonService.findHql("FROM TSDepart where org_type!= ? ORDER BY orderIndex","1");
		List<Map> father = new ArrayList<Map>();
		for (TSDepart de : ts) {
			Map map = new HashMap();
			map.put("id", de.getId());
			map.put("text", de.getDepartname());
			father.add(map);
		}
		js.setData(father);
		return js;
	}
	/**
	 * @Title: UserController.java
	 * @Description: 
	 * @author hyp
	 * @date 2017年4月2日 下午12:34:38
	 * @version V1.0
	 */
	@RequestMapping(params = "getDepartUser")
	@ResponseBody
	public JsonResult getDepartUser(String departid) {
		JsonResult js = new JsonResult();
		try{
		TSDepart de = new TSDepart();
		de.setId(departid);
		String hql = "from TSUserOrg to where to.tsDepart.id = ?";
		List<TSUserOrg> list = commonService.findHql(hql, departid);
		List<Map> child = new ArrayList<Map>();
			String uhql="select t.* from  t_s_base_user  t inner join t_s_user_org o on o.user_id= t.id  WHERE o.org_id=:departid  ORDER BY t.orderIndex";
			Map<String, String> params = new HashMap<String, String>();
			params.put("departid", departid);
			List list1 = jdbcTemplate.queryForList(uhql,params);
			for(Object base:list1){
				Map<String, Object> mapu = (Map<String, Object>) base;
				Map children = new HashMap();
				if (base != null) {
					children.put("id", mapu.get("USERNAME").toString());
					
					children.put("text",  mapu.get("REALNAME").toString());					
					child.add(children);
				}
				
			}
		js.setData(child);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return js;
	}
	
	/**
	 * @Title: UserController.java
	 * @Description: 得到所有部门和所有用户
	 * @author hyp
	 * @date 2017年4月2日 下午12:38:14
	 * @version V1.0
	 */
	@RequestMapping(params = "getAllUsers")
	@ResponseBody
	public JsonResult getAllUsers() {
		JsonResult js = new JsonResult();
		List<TSDepart> ts = commonService.findHql("FROM TSDepart where org_type!= ? ORDER BY orderIndex","1");
		List<Map> father = new ArrayList<Map>();
		for (TSDepart de : ts) {
			Map map = new HashMap();
			map.put("id", de.getId());
			map.put("text", de.getDepartname());
			List<TSUserOrg> list = commonService.findByProperty(
					TSUserOrg.class, "tsDepart", de);
			List<Map> child = new ArrayList<Map>();
			for (TSUserOrg user : list) {
				Map children = new HashMap();
				TSUser base = commonService.getEntity(TSUser.class,
						user.getTsUser().getId());
				if (base != null) {
					children.put("id", base.getUserName());
					children.put("text", base.getRealName());
					child.add(children);
				}
			}
			map.put("children", child);
			father.add(map);
		}
		js.setData(father);
		return js;
	}
	/**
	 * @Title: UserController.java
	 * @Description: 得到角色对应用户数据
	 * @author hyp
	 * @date 2017年4月2日 下午12:45:05
	 * @version V1.0
	 */
	@RequestMapping(params = "getRoleuser")
	@ResponseBody
	public JsonResult getRoleuser(String code ) {
		JsonResult js = new JsonResult();
		List<Map> father = new ArrayList<Map>();
		//分割用户
		String[] re = code.split(",");
		
		for (String str : re) {
		 
			List<TSRole> role = commonService.findByProperty(TSRole.class,"roleCode",str);
			
			for (TSRole r : role) {
				Map map = new HashMap();
				map.put("id", r.getId());
				map.put("text", r.getRoleName());
				List<TSRoleUser> list = commonService.findByProperty(
						TSRoleUser.class, "TSRole", r);
				List<Map> child = new ArrayList<Map>();
				for (TSRoleUser roleUser : list) {
					Map children = new HashMap();
					TSUser base = commonService.getEntity(TSUser.class,
							roleUser.getTSUser().getId());
					if (base != null) {
						children.put("id", base.getUserName());
						children.put("text", base.getRealName());
						child.add(children);
					}
			}
			map.put("children", child);
			father.add(map);
		}
		}
		js.setData(father);
		return js;
	}
	 
	/**
	 * @Title: UserController.java
	 * @Description: 通过用户数组得到用户数据
	 * @author hyp
	 * @date 2017年4月2日 下午12:45:52
	 * @version V1.0
	 */
	@RequestMapping(params = "getUsers")
	@ResponseBody
	public JsonResult getUsers(String code ) {
		JsonResult js = new JsonResult();
		List father = new ArrayList();
		//分割用户
		String[] re = code.split(",");
		
		for (String str : re) {
			
			List<TSUser> baseUser = commonService.findByProperty(TSUser.class,"userName",str);
			for (TSUser user : baseUser) {
//				father.add(user.getRealName());
				Map children = new HashMap();
				children.put("id", user.getUserName());
				children.put("text", user.getRealName());
//				System.out.print("名字"+user.getRealName());
				father.add(children);
			}
			
		}
		js.setData(father);
		return js;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
