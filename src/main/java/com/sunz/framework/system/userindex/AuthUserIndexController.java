package com.sunz.framework.system.userindex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;

@Controller 
@RequestMapping("framework/authuserindex")
public class AuthUserIndexController extends BaseController {
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@RequestMapping(params="setting")
	public ModelAndView userIndexSetting(){
		ModelAndView mv=new ModelAndView("framework/system/userindex/userIndex");
		return mv;
	}
	
	@RequestMapping(params="queryIndexTree")
	@ResponseBody
	public ListJsonResult queryIndex() {
		//获取部门和用户以及相关的首页路径
		List<Map<String, Object>> all = new ArrayList<Map<String , Object>>();
		String dsql = "select de.id as id,de.departname as name,ui.indexpath as code,ui.indexname as remark,null pid from t_s_depart de "
				+ "left join t_s_user_index ui on ui.departid = de.id "
				+ " union all "
				+ " select t.id,t.realname as name,ui.indexpath as code,ui.indexname as remark,o.Org_Id pid from  t_s_base_user  t "
				+ " inner join t_s_user_org o on o.user_id= t.id "
				+ " left join t_s_user_index ui on ui.userid = t.id ";
		all = jdbcTemplate.queryForList(dsql, new HashMap<String,Object>());
		return new ListJsonResult(all);		
	}
	
	@RequestMapping(params="save")
	@ResponseBody
	public JsonResult save(@ModelAttribute("userIndex") UserIndex userIndex) {
		JsonResult js = new JsonResult();
		//传过来的参数userIndex中userid和departid只有一个字段是有值。
		//查询表中有没有数据，没有进行新增，有就更新
		String hql = "from UserIndex u where(u.userid is not null and u.userid=?) or (u.departid is not null and u.departid = ?)";
		List<UserIndex> result = commonService.findHql(hql, userIndex.getUserid(),userIndex.getDepartid());
		if(result != null && result.size() > 0) {
			String sql = "update t_s_user_index set indexpath = ?,indexname=? where (userid is not null and userid=?) or (departid is not null and departid=?)";
			commonService.executeSql(sql, userIndex.getIndexpath(),userIndex.getIndexname(),userIndex.getUserid(),userIndex.getDepartid());
		} else {
			commonService.save(userIndex);
		}
		js.setMsg("保存用户首页路径");
		return js;
	}
	
	@RequestMapping(params = "getIndex")
	@ResponseBody
	public JsonResult getUrl() {
		JsonResult js = new JsonResult();
		TSUser user=this.getLoginUser();
		if(user!= null && user.getId() != null) {
			//优先使用userid对应的首页路径
			UserIndex ui = commonService.findUniqueByProperty(UserIndex.class, "userid", user.getId());
			if(ui != null && ui.getIndexpath() != null && !"".equals(ui.getIndexpath())) {
				js.setData(ui.getIndexpath());
			} else {
				//获取departid对应的首页路径
				String uhql="select ui.indexpath from t_s_user_index ui "
						+ " inner join  t_s_user_org  o  on ui.departid = o.org_id "
						+ " inner join  t_s_base_user  t  on o.user_id= t.id "
						+ " WHERE o.user_id = ?  ORDER BY t.orderIndex";
				Map<String , Object> map = commonService.findOneForJdbc(uhql, user.getId());
				if(map != null && map.get("INDEXPATH") != null && !"".equals(map.get("INDEXPATH"))) {
					js.setData(map.get("INDEXPATH"));
				} else {
					js.setData(null);
				}
			}
			js.setMsg("获取用户首页路基");
		} else {
			js.setMsg("用户未登录");
			js.setData(null);
		}
		return js;
	}
}
