//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.extend.util.CHNToPinyinUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.CommonService;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.PasswordUtil;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.pojo.base.TSUserOrg;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class DingDingService {
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private SystemService systemService;
    protected CommonService commonService;
    protected NamedParameterJdbcTemplate jdbcTemplate;
    private RowMapper<DingDingUser> DingUserMapper = new BeanPropertyRowMapper(DingDingUser.class);

    public DingDingService() {
    }

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public List<DingDingUser> getDingUserListByUserName(String usernames) {
        String sql = "SELECT d.*,t.realname FROM T_S_BASE_USER t inner join T_S_DDUSER d on t.id=d.t_s_id where t.username in ";
        String _usernames = "";
        String[] usernamearr = usernames.split(",");
        String[] var5 = usernamearr;
        int var6 = usernamearr.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String username = var5[var7];
            if ("".equals(_usernames)) {
                _usernames = "'" + username + "'";
            } else {
                _usernames = _usernames + ",'" + username + "'";
            }
        }

        sql = sql + "(" + _usernames + ")";
        List<DingDingUser> list = this.jdbcTemplate.query(sql, this.DingUserMapper);
        return list;
    }

    public List<DingDingUser> getDingUserListByDingUserIds(String ddids) {
        String sql = "SELECT d.*,t.realname FROM T_S_BASE_USER t inner join T_S_DDUSER d on t.id=d.t_s_id where d.userid in ";
        sql = "SELECT d.* FROM   T_S_DDUSER d   where d.userid in ";
        String _idstr = "";
        String[] ids = ddids.split(",");
        String[] var5 = ids;
        int var6 = ids.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String id = var5[var7];
            if ("".equals(_idstr)) {
                _idstr = "'" + id + "'";
            } else {
                _idstr = _idstr + ",'" + id + "'";
            }
        }

        sql = sql + "(" + _idstr + ")";
        List<DingDingUser> list = this.jdbcTemplate.query(sql, this.DingUserMapper);
        return list;
    }

    public List<DingDingUser> getDingUserListByTSUserIds(String tsuserids) {
        String sql = "SELECT d.*,t.realname FROM T_S_BASE_USER t inner join T_S_DDUSER d on t.id=d.t_s_id where d.t_s_id in ";
        sql = "SELECT d.* FROM   T_S_DDUSER d   where d.t_s_id  in ";
        String _idstr = "";
        String[] ids = tsuserids.split(",");
        String[] var5 = ids;
        int var6 = ids.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String id = var5[var7];
            if ("".equals(_idstr)) {
                _idstr = "'" + id + "'";
            } else {
                _idstr = _idstr + ",'" + id + "'";
            }
        }

        sql = sql + "(" + _idstr + ")";
        List<DingDingUser> list = this.jdbcTemplate.query(sql, this.DingUserMapper);
        return list;
    }

    public DingDingUser getDingUserByDingUserId(String id) {
        DingDingUser duser = null;
        String sql = "SELECT d.*,t.realname FROM T_S_BASE_USER t inner join T_S_DDUSER d on t.id=d.t_s_id where d.userid in ";
        sql = "SELECT d.* FROM   T_S_DDUSER d   where d.userid in ";
        sql = sql + "('" + id + "')";
        List<DingDingUser> list = this.jdbcTemplate.query(sql, this.DingUserMapper);
        if (list != null && list.size() > 0) {
            duser = (DingDingUser)list.get(0);
        }

        return duser;
    }

    public boolean isDingUserCopied(String userid) {
        boolean result = false;
        this.getDingUserListByDingUserIds(userid);
        return result;
    }

    public String removeDingUserLink(DingDingUser dingUser) {
        String message = "";
        String sql = "SELECT t.ID FROM T_S_DDUSER t WHERE t.t_s_id=?";
        List<Map<String, Object>> list = this.commonService.findForJdbc(sql, new Object[]{dingUser.getT_s_id()});
        if (list.size() != 0) {
            String ids = (String)((Map)list.get(0)).get("ID");
            dingUser = (DingDingUser)this.commonService.getEntity(DingDingUser.class, ids);
            message = "关联解除成功";

            try {
                this.commonService.delete(dingUser);
                this.systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
            } catch (Exception var7) {
                message = "关联解除失败";
                throw new RuntimeException(var7.getMessage());
            }
        } else {
            message = "没有关联";
        }

        return message;
    }

    public List<DingDingDepart> getDingDeparts() {
        List<DingDingDepart> departlist = new ArrayList();
        JSONArray departArr = new JSONArray();

        try {
            departArr = DingDingUtil.getDingDepart();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        Comparator var10000 = new Comparator<DingDingDepart>() {
            public int compare(DingDingDepart o1, DingDingDepart o2) {
                return o1.getId() == o2.getParentid() ? 1 : -1;
            }
        };

        for(int i = 0; i < departArr.size(); ++i) {
            DingDingDepart depart = new DingDingDepart();
            JSONObject jsonItem = departArr.getJSONObject(i);
            depart.setId(jsonItem.getInteger("id"));
            depart.setName(jsonItem.getString("name"));
            Object pid = jsonItem.get("parentid");
            if (pid != null) {
                depart.setParentid(jsonItem.getInteger("parentid"));
            } else {
                depart.setParentid(0);
            }

            departlist.add(depart);
        }

        return departlist;
    }

    public String copyDingDepartmentToSys(List<DingDingDepart> departlist) {
        departlist.addAll(this.getDingDeparts());
        String message = "";
        Collections.sort(departlist);
        String a = "";

        DingDingDepart de;
        for(Iterator var4 = departlist.iterator(); var4.hasNext(); a = a + de.getId() + "," + de.getParentid() + "||") {
            de = (DingDingDepart)var4.next();
        }

        this.logger.debug(a);
        String del_sql = "delete from T_S_DDdepart d";
        this.commonService.executeSql(del_sql, new Object[0]);

        try {
            Iterator var17 = departlist.iterator();

            while(var17.hasNext()) {
                DingDingDepart dingDepart = (DingDingDepart)var17.next();
                String id = "ding" + dingDepart.getId().toString();
                String add_sql = "insert into T_S_DDdepart values(?,?,?)";
                this.commonService.executeSql(add_sql, new Object[]{dingDepart.getId(), dingDepart.getName(), dingDepart.getParentid()});
                TSDepart depart = null;
                boolean isExit = false;
                depart = (TSDepart)this.commonService.get(TSDepart.class, id);
                if (depart == null) {
                    depart = new TSDepart();
                } else {
                    isExit = true;
                    this.logger.info("已存在");
                }

                depart.setOrgCode(id);
                depart.setId(id);
                depart.setDepartname(dingDepart.getName());
                String parentId = "";
                if (dingDepart.getParentid() != null && dingDepart.getParentid() != 0) {
                    parentId = "ding" + dingDepart.getParentid().toString();
                    new TSDepart();

                    TSDepart pdepart;
                    try {
                        pdepart = (TSDepart)this.commonService.getEntity(TSDepart.class, parentId);
                    } catch (Exception var14) {
                        pdepart = null;
                    }

                    if (pdepart == null) {
                        new TSDepart();
                    } else {
                        depart.setTSPDepart(pdepart);
                    }

                    depart.setOrgType("2");
                } else {
                    depart.setOrgType("1");
                }

                if (!isExit) {
                    String addSql = "insert into T_S_DEPART t (id,departname,parentdepartid, org_code,org_type) values(:id,:departname,:parentdepartid, :org_code,:org_type)";
                    Map<String, Object> param = new HashMap();
                    param.put("id", depart.getId());
                    param.put("departname", depart.getDepartname());
                    param.put("parentdepartid", parentId);
                    param.put("org_code", depart.getOrgCode());
                    param.put("org_type", depart.getOrgType());
                    this.jdbcTemplate.update(addSql, param);
                } else {
                    this.commonService.saveOrUpdate(depart);
                }
            }

            return message;
        } catch (Exception var15) {
            var15.printStackTrace();
            message = "同步部门失败";
            throw new RuntimeException(var15.getMessage());
        }
    }

    public String copyDingUsersToSysByDingDepartId(int departId) {
        JSONArray jsonArr = null;

        try {
            jsonArr = DingDingUtil.getDingUserByDepartmentId(String.valueOf(departId));
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        if (jsonArr != null) {
            for(int i = 0; i < jsonArr.size(); ++i) {
                JSONObject json = jsonArr.getJSONObject(i);
                String userid = json.getString("userid");
                this.saveDingUserJsonToSys(userid, json, true);
            }
        }

        List<DingDingDepart> departList = this.getDepartListByParentId(departId);
        if (departList != null) {
            Iterator var8 = departList.iterator();

            while(var8.hasNext()) {
                DingDingDepart depart = (DingDingDepart)var8.next();
                this.copyDingUsersToSysByDingDepartId(depart.getId());
            }
        }

        return "";
    }

    public String copyDingUsersToSys(String ids) throws Exception {
        String message = "同步用户成功";
        String[] var3 = ids.split(",");
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String id = var3[var5];
            this.copyDingUserToSys(id);
            this.systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
        }

        return message;
    }

    public String copyDingUserToSys(String userid) throws Exception {
        String message = "同步用户成功";

        try {
            new JSONObject();
            JSONObject jsonObj = DingDingUtil.getDingUserById(userid);
            this.saveDingUserJsonToSys(userid, jsonObj, true);
            return message;
        } catch (Exception var4) {
            var4.printStackTrace();
            throw var4;
        }
    }

    public void linkDingUserSysUser(String userid, String code) throws Exception {
        JSONObject userInfo = DingDingUtil.getDingUserInfoByCode(code);
        if (userInfo != null) {
            String id = userInfo.getString("userid");
            userInfo = DingDingUtil.getDingUserById(id);
            this.saveDingUserJsonToSys(id, userInfo, false);
            DingDingUser dingUser = this.getDingUserByDingUserId(id);
            if (dingUser == null) {
                throw new Exception("未知错误");
            } else {
                TSUser user = (TSUser)this.commonService.get(TSUser.class, userid);
                if (user != null) {
                    dingUser.setT_s_id(userid);
                    Map<String, Object> param = new HashMap();
                    param.put("userid", userid);
                    param.put("duserid", id);
                    this.commonService.executeSql("update T_S_DDUSER set t_s_id = :userid where userid = :duserid", param);
                } else {
                    throw new Exception("系统用户不存在");
                }
            }
        } else {
            throw new Exception("未获取到钉钉用户信息");
        }
    }

    public void saveDingUserJsonToSys(String userid, JSONObject jsonObj, boolean saveToSysUser) {
        TSUser user = new TSUser();
        String name = jsonObj.getString("name");
        String image = jsonObj.getString("avatar");
        boolean isExit = true;
        String username = CHNToPinyinUtil.getPingYin(name.replaceAll(" ", ""));
        DingDingUser duser = this.getDingUserByDingUserId(userid);
        if (duser == null) {
            isExit = false;
            duser = new DingDingUser();
        } else {
            duser = (DingDingUser)this.commonService.get(DingDingUser.class, duser.getId());
        }

        user.setUserName(username);
        if (!isExit) {
            List<TSUser> sysUserList = this.commonService.findByQueryString("from TSUser where userName like '%" + username + "%'");
            if (sysUserList != null && sysUserList.size() > 0) {
                user.setUserName(username + sysUserList.size());
            }

            String password = PasswordUtil.encrypt(user.getUserName(), "123456", PasswordUtil.getStaticSalt());
            user.setPassword(password);
        } else if (duser.getT_s_id() != null) {
            user = (TSUser)this.commonService.get(TSUser.class, duser.getT_s_id());
        }

        user.setRealName(name.replaceAll(" ", ""));
        Short status = new Short("1");
        user.setStatus(status);
        duser.setD_name(name);
        duser.setUserid(userid);
        duser.setImage(image);

        try {
            this.commonService.saveOrUpdate(duser);
        } catch (Exception var18) {
            var18.printStackTrace();
        }

        if (saveToSysUser) {
            this.commonService.saveOrUpdate(user);
            duser.setT_s_id(user.getId());
            this.commonService.saveOrUpdate(duser);
            JSONArray departmentCodes = jsonObj.getJSONArray("department");
            List<TSDepart> departList = this.commonService.findByProperty(TSDepart.class, "orgCode", "ding" + departmentCodes.getString(0));
            if (departList != null && departList.size() > 0) {
                List<TSUserOrg> userOrgList = new ArrayList();
                Iterator var14 = departList.iterator();

                while(var14.hasNext()) {
                    TSDepart depart = (TSDepart)var14.next();
                    TSUserOrg org = new TSUserOrg();
                    List<TSUserOrg> orglist = this.commonService.findByQueryString("from TSUserOrg  where tsDepart.id ='" + depart.getId() + "' and  tsUser.id ='" + user.getId() + "'");
                    if (orglist != null && orglist.size() > 0) {
                        org = (TSUserOrg)orglist.get(0);
                    }

                    org.setTsUser(user);
                    org.setTsDepart(depart);
                    userOrgList.add(org);
                    this.commonService.saveOrUpdate(org);
                }
            }

        }
    }

    public List getDepart(int pid) {
        List<DingDingDepart> ddepartList = this.getDepartListByParentId(pid);
        List departlist = new ArrayList();

        for(int i = 0; i < ddepartList.size(); ++i) {
            DingDingDepart dingdepart = (DingDingDepart)ddepartList.get(i);
            String name = dingdepart.getName();
            Integer id = dingdepart.getId();
            Map<String, Object> map = new HashMap();
            map.put("name", name);
            map.put("id", id);
            departlist.add(map);
        }

        return departlist;
    }

    public List<DingDingDepart> getDepartListByParentId(int pid) {
        String hql = "from DingDingDepart d";
        List ddepartList;
        if (pid >= 0) {
            hql = hql + " where d.parentid=?";
            ddepartList = this.commonService.findHql(hql, new Object[]{pid});
        } else {
            ddepartList = this.commonService.findHql(hql, new Object[0]);
        }

        return ddepartList;
    }

    public String sendRichMsg(List<TSUser> users, String textStr, String head_text, String body_image, int file_count, TSUser author, String message_url, String pc_message_url) throws Exception {
        String userids = "";

        TSUser user;
        for(Iterator var10 = users.iterator(); var10.hasNext(); userids = userids + "" + user.getId() + "") {
            user = (TSUser)var10.next();
            if (userids.length() > 0) {
                userids = userids + ",";
            }
        }

        List<DingDingUser> dingUsers = this.getDingUserListByTSUserIds(userids);
        return this.sendRichMsg0(dingUsers, textStr, head_text, body_image, file_count, author, message_url, pc_message_url);
    }

    public String sendRichMsg0(List<DingDingUser> dingUsers, String textStr, String head_text, String body_image, int file_count, TSUser author, String message_url, String pc_message_url) throws Exception {
        return this.sendRichMsg0((String)null, dingUsers, textStr, head_text, body_image, file_count, author, message_url, pc_message_url);
    }

    public String sendRichMsg0(String bid, List<DingDingUser> dingUsers, String textStr, String head_text, String body_image, int file_count, TSUser author, String message_url, String pc_message_url) throws Exception {
        String duserids = "";

        DingDingUser user;
        for(Iterator var11 = dingUsers.iterator(); var11.hasNext(); duserids = duserids + "" + user.getUserid() + "") {
            user = (DingDingUser)var11.next();
            if (duserids.length() > 0) {
                duserids = duserids + "|";
            }
        }

        DingDingMsg msg = new DingDingMsg(bid, duserids, textStr, head_text, body_image, file_count, message_url, pc_message_url, author.getRealName(), author);
        this.commonService.save(msg);
        String msgId = DingDingUtil.sendRich_Msg(duserids, (String)null, textStr, head_text, body_image, "" + file_count, author.getRealName(), message_url, pc_message_url);
        msg.setMsgId(msgId);
        this.commonService.saveOrUpdate(msg);
        Iterator var13 = dingUsers.iterator();

        while(var13.hasNext()) {
//            DingDingUser user = (DingDingUser)var13.next();// TODO
            user = (DingDingUser)var13.next();
            DingDingMsgUser msguser = new DingDingMsgUser();
            msguser.setMsgId(msgId);
            msguser.setUserId(user.getT_s_id());
            msguser.setChecked(false);
            msguser.setDingUserId(user.getUserid());
            this.commonService.saveOrUpdate(msguser);
        }

        return msgId;
    }

    public String sendTextMsg(List<TSUser> users, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        String userids = "";

        TSUser user;
        for(Iterator var7 = users.iterator(); var7.hasNext(); userids = userids + "'" + user.getId() + "'") {
            user = (TSUser)var7.next();
            if (userids.length() > 0) {
                userids = userids + ",";
            }
        }

        List<DingDingUser> dingUsers = this.getDingUserListByTSUserIds(userids);
        return this.sendTextMsg0((String)null, dingUsers, textStr, messageUrl, title, picUrl);
    }

    public String sendTextMsg(String bid, List<TSUser> users, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        String userids = "";

        TSUser user;
        for(Iterator var8 = users.iterator(); var8.hasNext(); userids = userids + "'" + user.getId() + "'") {
            user = (TSUser)var8.next();
            if (userids.length() > 0) {
                userids = userids + ",";
            }
        }

        List<DingDingUser> dingUsers = this.getDingUserListByTSUserIds(userids);
        return this.sendTextMsg0(bid, dingUsers, textStr, messageUrl, title, picUrl);
    }

    public String sendTextMsg(String userIds, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        List<DingDingUser> dingUsers = this.getDingUserListByTSUserIds(userIds);
        return this.sendTextMsg0((String)null, dingUsers, textStr, messageUrl, title, picUrl);
    }

    public String sendTextMsg(String bid, String userIds, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        List<DingDingUser> dingUsers = this.getDingUserListByTSUserIds(userIds);
        return this.sendTextMsg0(bid, dingUsers, textStr, messageUrl, title, picUrl);
    }

    public String sendTextMsg0(List<DingDingUser> dingUsers, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        return this.sendTextMsg0((String)null, dingUsers, textStr, messageUrl, title, picUrl);
    }

    public String sendTextMsg0(String bid, List<DingDingUser> dingUsers, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        String duserids = "";

        DingDingUser user;
        for(Iterator var8 = dingUsers.iterator(); var8.hasNext(); duserids = duserids + "" + user.getUserid() + "") {
            user = (DingDingUser)var8.next();
            if (duserids.length() > 0) {
                duserids = duserids + "|";
            }
        }

        String msgId = DingDingUtil.sendLink_Msg(duserids, textStr, messageUrl, title, picUrl);
        DingDingMsg msg = new DingDingMsg(bid, duserids, textStr, (String)null, (String)null, 0, (String)null, (String)null, "", (TSUser)null);

        try {
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            TSUser loginUser = (TSUser)session.getAttribute("LOCAL_CLINET_USER");
            msg.setAuthor(loginUser.getRealName());
            msg.setAuthorUser(loginUser);
        } catch (Exception var13) {
            this.logger.error("获取登录用户失败。");
        }

        msg.setMsgId(msgId);
        this.commonService.save(msg);
        Iterator var14 = dingUsers.iterator();

        while(var14.hasNext()) {
//            DingDingUser user = (DingDingUser)var14.next();// TODO
            user = (DingDingUser)var14.next();
            DingDingMsgUser msguser = new DingDingMsgUser();
            msguser.setMsgId(msgId);
            msguser.setUserId(user.getT_s_id());
            msguser.setChecked(false);
            msguser.setDingUserId(user.getUserid());
            this.commonService.saveOrUpdate(msguser);
        }

        return msgId;
    }

    public Map<String, Object> checkMsgResult(String msgId) throws Exception {
        DingDingMsg msg = null;
        List<DingDingMsg> msgs = this.commonService.findByProperty(DingDingMsg.class, "msgId", msgId);
        if (msgs.size() > 0) {
            msg = (DingDingMsg)msgs.get(0);
            if (!msg.isAllChecked()) {
                Map<String, Object> result = DingDingUtil.getMsgResult(msgId);
                JSONArray read = (JSONArray)result.get("read");
                JSONArray unread = (JSONArray)result.get("unread");

                for(int i = 0; i < read.size(); ++i) {
                    String uid = read.getString(i);
                    this.changeMsgUserReaded(msgId, uid);
                }

                if (unread.size() <= 0) {
                    this.changeMsgAllReaded(msgId);
                }

                return result;
            }
        }

        return null;
    }

    public void changeMsgUserReaded(String msgId, String userid) {
        Map<String, Object> params = new HashMap();
        params.put("msgId", msgId);
        params.put("userId", userid);
        List<DingDingMsgUser> users = this.commonService.findHql(" from DingDingMsgUser t where t.msgId =? ", new Object[]{msgId});
        Iterator var5 = users.iterator();

        while(var5.hasNext()) {
            DingDingMsgUser user = (DingDingMsgUser)var5.next();
            if (!user.isChecked()) {
                user.setChecked(true);
                this.commonService.saveOrUpdate(user);
            }
        }

    }

    public void changeMsgAllReaded(String msgId) {
        Map<String, Object> params = new HashMap();
        params.put("msgId", msgId);
        List<DingDingMsg> msgs = this.commonService.findHql(" from DingDingMsg t where t.msgId =?", new Object[]{msgId});
        Iterator var4 = msgs.iterator();

        while(var4.hasNext()) {
            DingDingMsg msg = (DingDingMsg)var4.next();
            if (!msg.isAllChecked()) {
                msg.setAllChecked(true);
                this.commonService.saveOrUpdate(msg);
            }
        }

    }

    public static void main(String[] args) {
        new DingDingService();
    }
}
