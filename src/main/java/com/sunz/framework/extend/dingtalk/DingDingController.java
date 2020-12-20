//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.util.IPagingQueryService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Scope("prototype")
@Controller
@RequestMapping({"/framework/dingUser"})
public class DingDingController extends BaseController {
    @Autowired
    private SystemService systemService;
    @Autowired
    protected DingDingService dingDingService;
    @Autowired
    IPagingQueryService pagingQueryService;
    private String message;

    public DingDingController() {
    }

    public void setPagingQueryService(IPagingQueryService pagingQueryService) {
        this.pagingQueryService = pagingQueryService;
    }

    @RequestMapping(
        params = {"list"}
    )
    public ModelAndView list(HttpServletRequest request) {
        return new ModelAndView("dingding/dingList");
    }

    @RequestMapping(
        params = {"list1"}
    )
    public ModelAndView list1(HttpServletRequest request) {
        return new ModelAndView("dingding/dingUserList");
    }

    @RequestMapping(
        params = {"list2"}
    )
    public ModelAndView list2(HttpServletRequest request) {
        return new ModelAndView("dingding/tongBuPage");
    }

    @RequestMapping(
        params = {"list3"}
    )
    public ModelAndView list3(HttpServletRequest request) {
        return new ModelAndView("dingding/sysUserList");
    }

    @RequestMapping(
        params = {"msgList"}
    )
    public ModelAndView msgList(PageParameter page) {
        return new ModelAndView("dingding/msgList");
    }

    @RequestMapping(
        params = {"queryMsgList"}
    )
    @ResponseBody
    public ListJsonResult queryMsgList(PageParameter page) {
        new ListJsonResult();
        String sql = " select msg.id,msg.textstr,msg.userids,msg.msgid,u.realname,u.id userID from t_s_ddmsg msg left join t_s_base_user u on msg.authortsuserid = u.id";
        ListJsonResult j = this.pagingQueryService.query(sql, this.toMap(), page);
        return j;
    }

    @RequestMapping(
        params = {"doPLTB"}
    )
    @ResponseBody
    public JsonResult doPLTB(String ids, HttpServletRequest request) {
        try {
            String[] var3 = ids.split(",");
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String id = var3[var5];
                this.dingDingService.copyDingUserToSys(id);
                this.systemService.addLog(this.message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
            }
        } catch (Exception var7) {
            this.logger.error("同步失败:" + ids, var7);
            return new JsonResult(var7.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"copyUserOfDepart"}
    )
    @ResponseBody
    public JsonResult copyDingUsersToSysByDingDepartId(String ids) {
        try {
            String[] ida = ids.split(",");
            String[] var3 = ida;
            int var4 = ida.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String id = var3[var5];
                int departId = Integer.parseInt(id);
                this.dingDingService.copyDingUsersToSysByDingDepartId(departId);
            }
        } catch (Exception var8) {
            this.logger.error("同步指定部门下的用户失败:" + ids, var8);
            return new JsonResult(var8.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"doTongbu"}
    )
    @ResponseBody
    public JsonResult doTongbu(String userid) {
        try {
            this.dingDingService.copyDingUserToSys(userid);
        } catch (Exception var3) {
            this.logger.error("同步用户失败:" + userid, var3);
            return new JsonResult(var3.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"dolink"}
    )
    @ResponseBody
    public JsonResult linkDingUserSysUser(String userid, String code) {
        try {
            this.dingDingService.linkDingUserSysUser(userid, code);
        } catch (Exception var4) {
            this.logger.error("同步用户失败:" + userid + "->" + code, var4);
            return new JsonResult(var4.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"dolink2"}
    )
    @ResponseBody
    public JsonResult linkDingUserSysUser2(String duserid, String tsid, String name, String dname) {
        try {
            DingDingUser duser = new DingDingUser();
            duser.setD_name(dname);
            duser.setUserid(duserid);
            duser.setT_s_id(tsid);
            this.commonService.saveOrUpdate(duser);
        } catch (Exception var6) {
            this.logger.error("同步用户失败:" + duserid + "->" + tsid, var6);
            return new JsonResult(var6.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"getDingUserIdByBusiId"}
    )
    @ResponseBody
    public ListJsonResult getDingUserIdByBusiId(String ywid) {
        String sql = "SELECT d.*,t.realname FROM T_S_BASE_USER t inner join t_s_ddUSER d on t.id=d.t_s_id where t.username in( SELECT k.assignee_ FROM ACT_HI_PROCINST t INNER JOIN ACT_RU_TASK k ON t.id_=k.proc_inst_id_ WHERE t.business_key_=?)";
        List<Map<String, Object>> list = this.commonService.findForJdbc(sql, new Object[]{ywid});
        return new ListJsonResult(list);
    }

    @RequestMapping(
        params = {"getAllUser"}
    )
    @ResponseBody
    public JsonResult getAllUser(String flag) {
        if ("101".equals(flag)) {
            return new JsonResult();
        } else {
            JsonResult js = new JsonResult();
            StringBuffer rysqlBuffer = new StringBuffer();
            rysqlBuffer.append("select t.id \"id\",t.text \"text\",t.code \"code\",t.pid \"pid\",t.iconCls \"iconCls\",t.connuser \"connuser\",t.sort from  view_dd_departuser t");
            StringBuffer dwsqlBuffer = new StringBuffer();
            dwsqlBuffer.append("select t.id \"id\",t.text \"text\",t.code \"code\",t.pid \"pid\",t.iconCls \"iconCls\",t.connuser \"connuser\",t.sort from  view_dd_departuser t");
            dwsqlBuffer.append(" start with t.pid is null and  t.text = ?");
            dwsqlBuffer.append(" connect by t.pid= prior t.code");
            dwsqlBuffer.append(" ORDER SIBLINGS BY t.sort ");
            String dwsql = dwsqlBuffer.toString();
            String rysql = rysqlBuffer.toString();
            List<Map<String, Object>> rylist = this.commonService.findForJdbc(rysql, new Object[0]);
            this.commonService.findForJdbc(dwsql, new Object[]{"单位"});
            List<Map<String, Object>> rytree = this.formatTree(rylist);
            js.setData(rytree);
            return js;
        }
    }

    public List<Map<String, Object>> formatTree(List<Map<String, Object>> list) {
        List<TSDepart> parentDepart = this.commonService.findHql("from  TSDepart where parentdepartid is null and 1=? order by orderindex asc", new Object[]{1});
        List<Map<String, Object>> treeList = new ArrayList();
        new ArrayList();

        HashMap rootmap;
        for(Iterator var4 = parentDepart.iterator(); var4.hasNext(); treeList.add(rootmap)) {
            TSDepart departparent = (TSDepart)var4.next();
            rootmap = new HashMap();
            rootmap.put("id", departparent.getId());
            rootmap.put("text", departparent.getDepartname());
            rootmap.put("iconCls", "icon-duka");
            rootmap.put("state", "closed");
            if (list.size() > 0 && list != null) {
                List<Map<String, Object>> childmap = new ArrayList();

                for(int i = 0; i < list.size(); ++i) {
                    Map<String, Object> node = (Map)list.get(i);
                    if (!"".equals(node.get("pid")) && node.get("pid") != null && node.get("pid").equals(rootmap.get("id"))) {
                        if ("icon-duka".equals(node.get("iconCls"))) {
                            node = this.getChildrenNodes(list, node);
                            if (!node.isEmpty()) {
                                node.put("state", "closed");
                            }
                        }

                        childmap.add(node);
                    }
                }

                rootmap.put("children", childmap);
            }
        }

        return treeList;
    }

    public Map<String, Object> getChildrenNodes(List<Map<String, Object>> list, Map<String, Object> pnode) {
        List<Map<String, Object>> childmap = new ArrayList();

        for(int i = 0; i < list.size(); ++i) {
            Map<String, Object> node = (Map)list.get(i);
            if (pnode.get("code").equals(node.get("pid"))) {
                if ("icon-duka".equals(node.get("iconCls"))) {
                    node = this.getChildrenNodes(list, node);
                    if (!node.isEmpty()) {
                        node.put("state", "closed");
                    }
                }

                childmap.add(node);
            }
        }

        if (!childmap.isEmpty()) {
            pnode.put("children", childmap);
        }

        return pnode;
    }

    @RequestMapping(
        params = {"getUser"}
    )
    @ResponseBody
    public JsonResult getUser() {
        JsonResult js = new JsonResult();
        List<TSDepart> parentDepart = this.commonService.findHql("from  TSDepart where parentdepartid is null and 1=? order by orderindex asc", new Object[]{1});
        List<Map> totalParent = new ArrayList();
        Iterator var4 = parentDepart.iterator();

        while(var4.hasNext()) {
            TSDepart departparent = (TSDepart)var4.next();
            Map<String, Object> map = new HashMap();
            map.put("id", departparent.getId());
            map.put("text", departparent.getDepartname());
            map.put("iconCls", "icon-duka");
            map.put("state", "closed");
            List<Map> child = new ArrayList();
            if ("4028ef81533a930001533a95b5f10004".equals(departparent.getId())) {
                child = this.getNextMapUser(departparent.getId());
            } else {
                new ArrayList();
                String sql = "select u.* from t_s_user_org u where u.org_id=? ";
                List<Map<String, Object>> listOrg = this.commonService.findForJdbc(sql, new Object[]{departparent.getId()});
                Iterator var10 = listOrg.iterator();

                while(var10.hasNext()) {
                    Map<String, Object> user = (Map)var10.next();
                    Map<String, String> children = new HashMap();
                    new HashMap();
                    String sql1 = "select b.* from t_s_base_user b where b.id=? order by b.orderindex asc ";
                    Map<String, Object> baseuser = this.commonService.findOneForJdbc(sql1, new Object[]{user.get("USER_ID")});
                    if (baseuser != null) {
                        children.put("id", (String)baseuser.get("ID"));
                        children.put("text", (String)baseuser.get("REALNAME"));
                        children.put("iconCls", "icon-man");
                        ((List)child).add(children);
                    } else {
                        ((List)child).add(children);
                    }
                }

                List<TSDepart> childdeparts = this.commonService.findHql("from  TSDepart where PARENTDEPARTID =? order by ORDERINDEX ASC", new Object[]{departparent.getId()});
                new ArrayList();
                if (childdeparts != null) {
                    Iterator var16 = childdeparts.iterator();

                    while(var16.hasNext()) {
                        TSDepart departchild = (TSDepart)var16.next();
                        Map<String, Object> mapchild = new HashMap();
                        mapchild.put("id", departchild.getId());
                        mapchild.put("text", departchild.getDepartname());
                        mapchild.put("iconCls", "icon-duka");
                        mapchild.put("state", "closed");
                        ((List)child).add(mapchild);
                    }
                }
            }

            map.put("children", child);
            totalParent.add(map);
        }

        js.setData(totalParent);
        return js;
    }

    @RequestMapping(
        params = {"getNextUser"}
    )
    @ResponseBody
    public JsonResult getNextUser(String nodeid) {
        JsonResult js = new JsonResult();
        List<Map> child = this.getNextMapUser(nodeid);
        js.setData(child);
        return js;
    }

    protected List<Map> getNextMapUser(String nodeid) {
        TSDepart departparent = (TSDepart)this.commonService.getEntity(TSDepart.class, nodeid);
        new ArrayList();
        String sql = "select u.* from t_s_user_org u where u.org_id=? ";
        List<Map<String, Object>> listOrg = this.commonService.findForJdbc(sql, new Object[]{departparent.getId()});
        List<Map> child = new ArrayList();
        Iterator var6 = listOrg.iterator();

        while(var6.hasNext()) {
            Map<String, Object> user = (Map)var6.next();
            Map<String, String> children = new HashMap();
            new HashMap();
            String sql1 = "select b.* from t_s_base_user b where b.id=? order by b.orderindex asc ";
            Map<String, Object> baseuser = this.commonService.findOneForJdbc(sql1, new Object[]{user.get("USER_ID")});
            if (baseuser != null) {
                children.put("id", (String)baseuser.get("ID"));
                children.put("text", (String)baseuser.get("REALNAME"));
                children.put("iconCls", "icon-man");
                child.add(children);
            } else {
                child.add(children);
            }
        }

        List<TSDepart> childdeparts = this.commonService.findHql("from  TSDepart where PARENTDEPARTID =? order by ORDERINDEX ASC ", new Object[]{nodeid});
        if (childdeparts != null) {
            List<Map> childTotal = this.getChildren(childdeparts);
            child.addAll(childTotal);
        }

        return child;
    }

    protected List<Map> getChildren(List<TSDepart> parents) {
        List<Map> totalParent = new ArrayList();
        Iterator var3 = parents.iterator();

        while(var3.hasNext()) {
            TSDepart departparent = (TSDepart)var3.next();
            Map<String, Object> map = new HashMap();
            map.put("id", departparent.getId());
            map.put("text", departparent.getDepartname());
            map.put("iconCls", "icon-duka");
            map.put("state", "closed");
            new ArrayList();
            String sql = "select u.* from t_s_user_org u where u.org_id=? ";
            List<Map<String, Object>> listOrg = this.commonService.findForJdbc(sql, new Object[]{departparent.getId()});
            List<Map> child = new ArrayList();
            Iterator var9 = listOrg.iterator();

            while(var9.hasNext()) {
                Map<String, Object> user = (Map)var9.next();
                Map<String, String> children = new HashMap();
                new HashMap();
                String sql1 = "select b.* from t_s_base_user b where b.id=? order by b.orderindex asc ";
                Map<String, Object> baseuser = this.commonService.findOneForJdbc(sql1, new Object[]{user.get("USER_ID")});
                if (baseuser != null) {
                    children.put("id", (String)baseuser.get("ID"));
                    children.put("text", (String)baseuser.get("REALNAME"));
                    children.put("iconCls", "icon-man");
                    child.add(children);
                } else {
                    child.add(children);
                }
            }

            List<TSDepart> childdeparts = this.commonService.findHql("from  TSDepart where PARENTDEPARTID =?", new Object[]{departparent.getId()});
            if (childdeparts != null) {
                List<Map> childTotal = this.getChildren(childdeparts);
                child.addAll(childTotal);
            }

            map.put("children", child);
            totalParent.add(map);
        }

        return totalParent;
    }

    @RequestMapping(
        params = {"getDingUserList"}
    )
    @ResponseBody
    public ListJsonResult getDingUserList(String id, PageParameter page) {
        Map<String, Object> param = new HashMap();
        param.put("id", id);
        return this.pagingQueryService.query("dd_getDingUserList", param, page);
    }

    @RequestMapping(
        params = {"getDingDepart"}
    )
    @ResponseBody
    public JsonResult getDingDepart() {
        return new JsonResult(this.dingDingService.getDingDeparts());
    }

    @RequestMapping(
        params = {"syncDingDepart"}
    )
    @ResponseBody
    public JsonResult syncDingDepart() {
        List<DingDingDepart> allDeparts = this.dingDingService.getDingDeparts();
        String err = this.dingDingService.copyDingDepartmentToSys(allDeparts);
        return new JsonResult(err, allDeparts);
    }

    @RequestMapping(
        params = {"getDingUserIdByUserName"}
    )
    @ResponseBody
    public ListJsonResult getDingUserIdByUserName(String usernames) {
        if (usernames != null && !"".equals(usernames)) {
            List<DingDingUser> list = this.dingDingService.getDingUserListByUserName(usernames);
            return new ListJsonResult(list);
        } else {
            return new ListJsonResult("用户未指定");
        }
    }

    @RequestMapping(
        params = {"doCancel"}
    )
    @ResponseBody
    public JsonResult doCancel(DingDingUser dingUser) {
        this.message = this.dingDingService.removeDingUserLink(dingUser);
        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"doCancelUsers"}
    )
    @ResponseBody
    public JsonResult doCancelUsers(String ids) {
        List<DingDingUser> dinguserlist = this.dingDingService.getDingUserListByDingUserIds(ids);
        Iterator var3 = dinguserlist.iterator();

        while(var3.hasNext()) {
            DingDingUser duser = (DingDingUser)var3.next();
            this.dingDingService.removeDingUserLink(duser);
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"sendTextMsg"}
    )
    @ResponseBody
    public JsonResult sendTextMsg(String bid, String ids, String msg, String messageUrl, String title, String picUrl) {
        try {
            List<DingDingUser> dinguserlist = this.dingDingService.getDingUserListByTSUserIds(ids);
            if (dinguserlist == null || dinguserlist.size() <= 0) {
                return new JsonResult("指定的人员不存在");
            }

            this.dingDingService.sendTextMsg0(bid, dinguserlist, msg, messageUrl, title, picUrl);
        } catch (Exception var8) {
            this.logger.error("消息发送失败", var8);
            return new JsonResult(var8.getMessage());
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"getMsgResultFromDing"}
    )
    @ResponseBody
    public JsonResult getMsgResultFromDing(String msgId) {
        try {
            Map<String, Object> result = this.dingDingService.checkMsgResult(msgId);
            return new JsonResult(result);
        } catch (Exception var3) {
            this.logger.error("消息状态检测失败:" + msgId, var3);
            return new JsonResult(var3.getMessage());
        }
    }

    @RequestMapping(
        params = {"addDingUser"}
    )
    @ResponseBody
    public JsonResult addUser(String name, String department, String mobile, String email) {
        JsonResult result = new JsonResult();
        String[] departments = new String[]{department};

        try {
            DingDingUtil.createUser((String)null, name, departments, (String)null, mobile, mobile, (String)null, email, (String)null);
        } catch (Exception var8) {
            result.setSuccess(false);
            result.setMsg(var8.getMessage());
            this.logger.error("添加钉钉用户失败", var8);
        }

        return result;
    }
}
