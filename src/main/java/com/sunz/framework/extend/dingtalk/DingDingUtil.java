//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.extend.util.HttpUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class DingDingUtil {
    public static final int OUT_TIME = 60000;
    public static final int OUT_TIME_TICKET = 60000;
    private static Date Last_Access_Token_Time;
    private static Date Last_Ticket_Time;
    private static String Access_Token;
    private static String Ticket;
    private static Logger logger = Logger.getLogger(DingDingUtil.class);

    public DingDingUtil() {
    }

    public static String getCorpID() {
        return Config.get("dingtalk.CorpID");
    }

    public static String getCorpSecret() {
        return Config.get("dingtalk.CorpSecret");
    }

    public static String getAgentId() {
        return Config.get("dingtalk.AgentId");
    }

    public static String getOaAgentId() {
        return Config.get("dingtalk.OaAgentId");
    }

    public static String getOapiUrl() {
        String OapiUrl = Config.get("dingtalk.DingOapiUrl");
        if (OapiUrl == null) {
            OapiUrl = "https://oapi.dingtalk.com";
        }

        return OapiUrl;
    }

    public static String getOaMessageUrl() {
        return Config.get("oaMessageUrl");
    }

    public static String getWebMessageUrl() {
        return Config.get("webMessageUrl");
    }

    public static void main(String[] args) {
    }

    public static String getTicket() throws Exception {
        Date now = new Date();
        if (Ticket == null) {
            Ticket = getTicketFromServer(getAccess_Token());
            Last_Ticket_Time = now;
        } else {
            Date outDate = new Date(Last_Ticket_Time.getTime() + 60000L);
            if (now.getTime() > outDate.getTime()) {
                logger.debug("已超时，重新请求钉钉服务器");
                Ticket = getTicketFromServer(getAccess_Token());
                Last_Ticket_Time = now;
            }
        }

        logger.debug("getTicket-->" + Ticket + "accessToken-->" + getAccess_Token());
        return Ticket;
    }

    public static String getTicketFromServer(String access_token) throws Exception {
        String ticket_url = getOapiUrl() + "/get_jsapi_ticket?access_token=ACCESS_TOKEN";
        String ticket_str = httpGet(ticket_url, "UTF-8");

        try {
            Map<String, Integer> map = (Map)JSONObject.parse(ticket_str);
            int errcode = (Integer)map.get("errcode");
            if (errcode != 0) {
                Ticket = getTicketFromServer(access_token);
            } else {
                Ticket = map.get("ticket") + "";
            }

            logger.debug("getTicketFromServer-->" + Ticket + "access_token-->" + access_token);
        } catch (JSONException var5) {
            logger.error("获取钉钉ticket失败", var5);
        }

        return Ticket;
    }

    public static String getAccess_Token() throws Exception {
        Date now = new Date();
        if (Access_Token == null) {
            Access_Token = getAccess_TokenFromServer();
            Last_Access_Token_Time = now;
        } else {
            Date outDate = new Date(Last_Access_Token_Time.getTime() + 60000L);
            logger.debug("当前时间：" + now.toLocaleString() + "---超时时间：" + outDate.toLocaleString());
            if (now.getTime() > outDate.getTime()) {
                logger.debug("已超时，重新请求钉钉服务器");
                Access_Token = getAccess_TokenFromServer();
                Last_Access_Token_Time = now;
                Ticket = null;
            }
        }

        logger.debug("getAccess_Token-->" + Access_Token);
        return Access_Token;
    }

    public static String getAccess_TokenFromServer() throws Exception {
        String corpid = getCorpID();
        String corpsecret = getCorpSecret();
        String accessToken_url = getOapiUrl() + "/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;
        String accessToken_str = HttpUtil.sendGet(accessToken_url, "UTF-8");

        try {
            Map<String, Integer> map = (Map)JSONObject.parse(accessToken_str);
            int errcode = (Integer)map.get("errcode");
            if (errcode != 0) {
                throw new Exception(accessToken_str);
            }

            Access_Token = map.get("access_token") + "";
            logger.debug("getAccess_TokenFromServer-->" + Access_Token);
        } catch (JSONException var6) {
            logger.error("获取钉钉token失败", var6);
        }

        return Access_Token;
    }

    public static String getConfig(HttpServletRequest request, boolean useReferer) throws Exception {
        String ticket = getTicket();
        String url = "";
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        url = basePath + Config.get("dingtalk.loginurl");
        if (useReferer) {
            logger.debug(request.getHeader("Referer"));
            url = request.getHeader("Referer");
        }

        url = URLDecoder.decode(url, "utf-8");
        return getConfig(url);
    }

    public static String getConfig(String baseUrl) throws Exception {
        String ticket = getTicket();
        String accessToken = getAccess_Token();
        String nonceStr = "sunztech.com";
        long timeStamp = (new Date()).getTime();
        String url = baseUrl;
        String agentid = "";
        agentid = getAgentId();
        String signature = "";

        try {
            signature = sign(ticket, nonceStr, timeStamp, url);
        } catch (OApiException var11) {
            logger.error("获取钉钉签名失败", var11);
        }

        String corpId = getCorpID();
        String appId = "";
        return "{jsticket:'" + ticket + "',signature:'" + signature + "',nonceStr:'" + nonceStr + "',timeStamp:'" + timeStamp + "',corpId:'" + corpId + "',agentid:'" + agentid + "',appid:'" + appId + "',url:'" + baseUrl + "',accessToken:'" + accessToken + "'}";
    }

    public static String getOAConfig(HttpServletRequest request) throws Exception {
        String ticket = getTicket();
        String nonceStr = "sunztech.com";
        long timeStamp = (new Date()).getTime();
        String url = "";
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        url = basePath + "/lzxmdc/login.do?oaLogin";
        String signature = "";

        try {
            signature = sign(ticket, nonceStr, timeStamp, url);
        } catch (OApiException var12) {
            logger.error("获取钉钉签名失败", var12);
        }

        String corpId = getCorpID();
        String agentid = getOaAgentId();
        String appId = "";
        return "{jsticket:'" + ticket + "',signature:'" + signature + "',nonceStr:'" + nonceStr + "',timeStamp:'" + timeStamp + "',corpId:'" + corpId + "',agentid:'" + agentid + "',appid:'" + appId + "',url:'" + url + "',accessToken:'" + getAccess_Token() + "'}";
    }

    public static String sign(String ticket, String nonceStr, long timeStamp, String url) throws OApiException {
        String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timeStamp + "&url=" + url;

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return bytesToHex(sha1.digest());
        } catch (NoSuchAlgorithmException var7) {
            throw new OApiResultException(var7.getMessage());
        } catch (UnsupportedEncodingException var8) {
            throw new OApiResultException(var8.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        byte[] var2 = hash;
        int var3 = hash.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            formatter.format("%02x", b);
        }

        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String httpGet(String url) throws Exception {
        String result = httpGet(url, "UTF-8");
        return result;
    }

    public static String httpGet(String url, String charset) throws Exception {
        String furl = url.replace("ACCESS_TOKEN", getAccess_Token());
        String result = HttpUtil.get(furl, charset);
        if (!checkResult(result)) {
            result = httpGet(url, charset);
        }

        return result;
    }

    public static String httpPost(String url, JSONObject json) throws Exception {
        String furl = url.replace("ACCESS_TOKEN", getAccess_Token());
        String result = HttpUtil.httpPost(furl, json);
        if (!checkResult(result)) {
            result = httpPost(url, json);
        }

        return result;
    }

    private static boolean checkResult(String result) throws Exception {
        JSONObject jsonObj = JSONObject.parseObject(result);
        if (jsonObj.getLong("errcode") == 40014L) {
            logger.error("token失效 重新获取token");
            getAccess_TokenFromServer();
            return false;
        } else {
            return true;
        }
    }

    public static JsonResult sendUserMsg_Text(DingDingUser touser, String textStr) throws Exception {
        JsonResult json = new JsonResult();
        String url = getOapiUrl() + "/message/send?access_token=ACCESS_TOKEN";
        JSONObject text = new JSONObject();
        String currTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        String content = textStr + "--" + currTime;
        text.put("content", content);
        JSONObject jo = new JSONObject();
        jo.put("touser", touser.getUserid());
        jo.put("agentid", getAgentId());
        jo.put("msgtype", "text");
        jo.put("text", text);

        try {
            String result = httpPost(url, jo);
            logger.debug(result);
            Map<String, String> map = (Map)JSONObject.parse(result);
            json.setData(map);
            json.setSuccess(true);
            logger.debug(touser.getUserid() + "---------" + textStr);
        } catch (Exception var10) {
            json.setSuccess(false);
            logger.error("发送钉钉消息失败", var10);
        }

        return json;
    }

    public static String sendUserMsg_Text(List<DingDingUser> touserList, String textStr) throws Exception {
        String messageid = "";
        String url = getOapiUrl() + "/message/send?access_token=ACCESS_TOKEN";
        JSONObject text = new JSONObject();
        String currTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        String content = textStr + "--" + currTime;
        text.put("text", content);
        text.put("messageUrl", "http://218.65.149.131:8081/lz_xmdc/lzxmdc/login.do?mLogin");
        text.put("picUrl", "");
        text.put("title", textStr);
        String tousers = "";
        DingDingUser dingUser;
        if (!touserList.isEmpty() && touserList != null && touserList.size() > 0) {
            for(Iterator var8 = touserList.iterator(); var8.hasNext(); tousers = tousers + dingUser.getUserid() + "|") {
                dingUser = (DingDingUser)var8.next();
            }
        }

        if (!"".equals(tousers)) {
            JSONObject jo = new JSONObject();
            jo.put("touser", tousers.substring(0, tousers.length() - 1));
            jo.put("agentid", getAgentId());
            jo.put("msgtype", "link");
            jo.put("link", text);

            try {
                String result = httpPost(url, jo);
                logger.debug(result);
                JSONObject resultjson = JSONObject.parseObject(result);
                messageid = resultjson.getString("messageId");
            } catch (Exception var11) {
                logger.error("发送钉钉消息失败", var11);
            }
        }

        return messageid;
    }

    public static boolean sendMsg_Text_Test() throws Exception {
        Boolean state = false;
        String url = getOapiUrl() + "/message/send?access_token=ACCESS_TOKEN";
        new JSONObject();
        String textStr = "加快征地及相关前期工作什么等等一长串的督查建议1";
        String userid = "manager4555";
        sendLink_Msg(userid, textStr, "http://www.leechg.com/", "leechg", "");
        return state;
    }

    public static String sendLink_Msg(String userIds, String textStr, String messageUrl, String title, String picUrl) throws Exception {
        JSONObject text = new JSONObject();
        if (userIds == null && userIds == "") {
            throw new Exception("发送对象不能为空");
        } else {
            JSONObject jo = new JSONObject();
            jo.put("touser", userIds);
            jo.put("agentid", getAgentId());
            if (messageUrl == null) {
                jo.put("msgtype", "text");
                text.put("content", textStr);
                jo.put("text", text);
            } else {
                jo.put("msgtype", "link");
                text.put("messageUrl", messageUrl);
                text.put("text", textStr);
                text.put("picUrl", title);
                text.put("title", title);
                jo.put("link", text);
            }

            String messageId = sendMsgCommon(jo);
            return messageId;
        }
    }

    public static Map<String, Object> sendMsg_Result(String messageId) throws Exception {
        Map<String, Object> msgResult = new HashMap();
        String url = getOapiUrl() + "/message/list_message_status?access_token=ACCESS_TOKEN";
        JSONObject jo = new JSONObject();
        jo.put("messageId", messageId);

        try {
            String result = httpPost(url, jo);
            logger.debug(result);
            JSONObject resultjson = JSONObject.parseObject(result);
            logger.debug("消息：" + messageId + "---------已读：" + resultjson.getString("read") + "--未读：" + resultjson.getString("unread"));
            msgResult.put("read", resultjson.getJSONArray("read"));
            msgResult.put("unread", resultjson.getJSONArray("unread"));
        } catch (Exception var6) {
            msgResult = null;
            logger.error("获取消息结果失败", var6);
        }

        return msgResult;
    }

    public static Map<String, Object> getMsgResult(String messageId) throws Exception {
        return sendMsg_Result(messageId);
    }

    public static String sendRich_Msg(List<DingDingUser> toUserList, List<DingDingUser> toDepartmentList, String textStr, String head_text, String body_image, String file_count, String author, String message_url, String pc_message_url) throws Exception {
        String tousers = "";
        String topartys = "";
        Iterator var11;
        DingDingUser dingUser;
        if (toUserList != null && !toUserList.isEmpty()) {
            for(var11 = toUserList.iterator(); var11.hasNext(); tousers = tousers + dingUser.getUserid()) {
                dingUser = (DingDingUser)var11.next();
                if (!tousers.equals("")) {
                    tousers = tousers + "|";
                }
            }
        }

        if (toDepartmentList != null && !toDepartmentList.isEmpty()) {
            for(var11 = toDepartmentList.iterator(); var11.hasNext(); topartys = topartys + dingUser.getUserid()) {
                dingUser = (DingDingUser)var11.next();
                if (!topartys.equals("")) {
                    topartys = topartys + "|";
                }
            }
        }

        String messageid = sendRich_Msg(tousers, topartys, textStr, head_text, body_image, file_count, author, message_url, pc_message_url);
        return messageid;
    }

    public static String sendRich_Msg(String tousers, String topartys, String textStr, String head_text, String body_image, String file_count, String author, String message_url, String pc_message_url) throws Exception {
        String messageid = "";
        JSONObject jo = new JSONObject();
        if (tousers != null && tousers.length() > 0) {
            jo.put("touser", tousers);
        }

        if (topartys != null && topartys.length() > 0) {
            jo.put("toparty", topartys);
        }

        jo.put("agentid", getAgentId());
        JSONObject text = new JSONObject();
        JSONObject head = new JSONObject();
        JSONObject body = new JSONObject();
        head.put("bgcolor", "FF6bddce");
        String headText = " ";
        if (head_text != null && head_text.length() > 10) {
            headText = head_text.substring(0, 10);
        } else if (head_text != null && head_text.length() > 0) {
            headText = head_text;
        }

        head.put("text", headText);
        text.put("head", head);
        body.put("title", head_text);
        body.put("content", textStr);
        body.put("image", body_image);
        body.put("file_count", file_count);
        body.put("author", author);
        text.put("body", body);
        if (pc_message_url == null || "".equals(message_url)) {
            pc_message_url = message_url;
        }

        text.put("message_url", message_url);
        text.put("pc_message_url", pc_message_url);
        jo.put("msgtype", "oa");
        jo.put("oa", text);
        messageid = sendMsgCommon(jo);
        return messageid;
    }

    public static String sendMsgCommon(JSONObject json) throws Exception {
        String result = "发送钉钉消息失败";
        String messageid = null;
        String url = getOapiUrl() + "/message/send?access_token=ACCESS_TOKEN";

        try {
            result = httpPost(url, json);
            logger.debug(result);
            JSONObject resultjson = JSONObject.parseObject(result);
            messageid = resultjson.getString("messageId");
            return messageid;
        } catch (Exception var5) {
            logger.error("发送钉钉消息失败", var5);
            throw new Exception(result);
        }
    }

    public static JSONArray getDingDepart() throws Exception {
        String url = getOapiUrl() + "/department/list?access_token=ACCESS_TOKEN";
        String str = httpGet(url, "UTF-8");
        JSONObject jsonObj = JSONObject.parseObject(str);
        JSONArray departArr = jsonObj.getJSONArray("department");
        return departArr;
    }

    public static JSONObject getDingDepartDetail(long id) throws Exception {
        String url = getOapiUrl() + "/department/get?access_token=ACCESS_TOKEN&id=" + id;
        String str = httpGet(url, "UTF-8");
        JSONObject jsonObj = JSONObject.parseObject(str);
        return jsonObj;
    }

    public static JSONArray getDingUserByDepartmentId(String id) throws Exception {
        String url = null;

        try {
            url = getOapiUrl() + "/user/list?access_token=ACCESS_TOKEN" + "&department_id=" + id;
        } catch (Exception var5) {
            logger.error("获取钉钉API路径失败", var5);
        }

        String str = httpGet(url, "UTF-8");
        JSONObject jsonObj = JSONObject.parseObject(str);
        JSONArray userList = jsonObj.getJSONArray("userlist");
        return userList;
    }

    public static JSONObject getDingUserById(String userid) throws Exception {
        String url = getOapiUrl() + "/user/get?access_token=ACCESS_TOKEN" + "&userid=" + userid;
        String str = httpGet(url, "UTF-8");
        JSONObject jsonObj = JSONObject.parseObject(str);
        return jsonObj;
    }

    public static JSONObject getDingUserInfoByCode(String code) throws Exception {
        String url = getOapiUrl() + "/user/getuserinfo?access_token=ACCESS_TOKEN" + "&code=" + code;
        String str = httpGet(url, "UTF-8");
        JSONObject jsonObj = JSONObject.parseObject(str);
        return jsonObj;
    }

    public static JSONObject createUser(String userId, String name, String[] departments, String position, String mobile, String tel, String workPlace, String email, String jobnum) throws Exception {
        String url = getOapiUrl() + "/user/create?access_token=ACCESS_TOKEN";
        JSONObject json = new JSONObject();
        if (userId != null && userId != "") {
            json.put("userid", userId);
        }

        json.put("name", name);
        json.put("department", departments);
        if (position != null && position != "") {
            json.put("position", position);
        }

        json.put("mobile", mobile);
        if (tel != null && tel != "") {
            json.put("tel", tel);
        }

        if (workPlace != null && workPlace != "") {
            json.put("workPlace", workPlace);
        }

        if (email != null && email != "") {
            json.put("email", email);
        }

        if (jobnum != null && jobnum != "") {
            json.put("jobnumber", jobnum);
        }

        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return jsonObj;
        } else {
            throw new Exception(result);
        }
    }

    public static boolean updateUser(String userId, String name, String[] departments, String position, String mobile, String tel, String workPlace, String email, String jobnum) throws Exception {
        String url = getOapiUrl() + "/user/update?access_token=ACCESS_TOKEN";
        JSONObject json = new JSONObject();
        if (userId != null && userId != "") {
            json.put("userid", userId);
        }

        json.put("name", name);
        json.put("department", departments);
        if (position != null && position != "") {
            json.put("position", position);
        }

        json.put("mobile", mobile);
        if (tel != null && tel != "") {
            json.put("tel", tel);
        }

        if (workPlace != null && workPlace != "") {
            json.put("workPlace", workPlace);
        }

        if (email != null && email != "") {
            json.put("email", email);
        }

        if (jobnum != null && jobnum != "") {
            json.put("jobnumber", jobnum);
        }

        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return true;
        } else {
            throw new Exception(jsonObj.getString("errmsg"));
        }
    }

    public static boolean updateUser(JSONObject json) throws Exception {
        String url = getOapiUrl() + "/user/update?access_token=ACCESS_TOKEN";
        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return true;
        } else {
            throw new Exception(jsonObj.getString("errmsg"));
        }
    }

    public static boolean deleteUser(String userId) throws Exception {
        String url = getOapiUrl() + "/user/delete?access_token=ACCESS_TOKEN" + "&userid=" + userId;
        String result = httpGet(url);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return true;
        } else {
            throw new Exception(jsonObj.getString("errmsg"));
        }
    }

    public static boolean deleteUsers(String[] userIds) throws Exception {
        String url = getOapiUrl() + "/user/batchdelete?access_token=ACCESS_TOKEN";
        JSONObject json = new JSONObject();
        json.put("useridlist", userIds);
        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return true;
        } else {
            throw new Exception(jsonObj.getString("errmsg"));
        }
    }

    public static boolean createDepart(String name, String parentid, String order, boolean createDeptGroup, boolean deptHiding, String deptPerimits, boolean outerDept, String outerPermitDepts, String outerPermitUsers) throws Exception {
        String url = getOapiUrl() + "/department/create?access_token=ACCESS_TOKEN";
        JSONObject json = new JSONObject();
        if (name != null && name != "") {
            json.put("name", name);
        }

        if (parentid != null && parentid != "") {
            json.put("parentid", parentid);
        } else {
            json.put("parentid", "1");
        }

        if (order != null && order != "") {
            json.put("order", order);
        }

        json.put("createDeptGroup", createDeptGroup);
        json.put("deptHiding", deptHiding);
        if (deptPerimits != null && deptPerimits != "") {
            json.put("deptPerimits", deptPerimits);
        }

        json.put("outerDept", outerDept);
        if (outerPermitDepts != null && outerPermitDepts != "") {
            json.put("outerPermitDepts", outerPermitDepts);
        }

        if (outerPermitUsers != null && outerPermitUsers != "") {
            json.put("outerPermitUsers", outerPermitUsers);
        }

        if (name != null && name != "") {
            json.put("name", name);
        }

        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        return jsonObj.getInteger("errcode") == 0;
    }

    public static boolean updateDepart(String id, String name, String parentid, String order, boolean createDeptGroup, boolean deptHiding, String deptPerimits, boolean outerDept, String outerPermitDepts, String outerPermitUsers) throws Exception {
        String url = getOapiUrl() + "/department/update?access_token=ACCESS_TOKEN";
        JSONObject json = new JSONObject();
        json.put("id", id);
        if (name != null && name != "") {
            json.put("name", name);
        }

        if (parentid != null && parentid != "") {
            json.put("parentid", parentid);
        } else {
            json.put("parentid", "1");
        }

        if (order != null && order != "") {
            json.put("order", order);
        }

        json.put("createDeptGroup", createDeptGroup);
        json.put("deptHiding", deptHiding);
        if (deptPerimits != null && deptPerimits != "") {
            json.put("deptPerimits", deptPerimits);
        }

        json.put("outerDept", outerDept);
        if (outerPermitDepts != null && outerPermitDepts != "") {
            json.put("outerPermitDepts", outerPermitDepts);
        }

        if (outerPermitUsers != null && outerPermitUsers != "") {
            json.put("outerPermitUsers", outerPermitUsers);
        }

        if (name != null && name != "") {
            json.put("name", name);
        }

        String result = httpPost(url, json);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        return jsonObj.getInteger("errcode") == 0;
    }

    public static boolean updateDepart(JSONObject departInfo) throws Exception {
        String url = getOapiUrl() + "/department/update?access_token=ACCESS_TOKEN";
        String result = httpPost(url, departInfo);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        return jsonObj.getInteger("errcode") == 0;
    }

    public static boolean deleteDepart(String departId) throws Exception {
        String url = getOapiUrl() + "/department/delete?access_token=ACCESS_TOKEN" + "&id=" + departId;
        String result = httpGet(url);
        JSONObject jsonObj = JSONObject.parseObject(result);
        logger.debug(result);
        if (jsonObj.getInteger("errcode") == 0) {
            return true;
        } else {
            throw new Exception(jsonObj.getString("errmsg"));
        }
    }

    public void userTest() throws Exception {
        String mobile = "13521816989";
        String userId = "041123355320742850";
        createUser((String)null, "冷颖峰", new String[]{"1"}, (String)null, "13870022068", (String)null, (String)null, (String)null, (String)null);
    }

    public void userDepart() throws Exception {
        String mobile = "13521816989";
        String departId = "101";
        getAccess_Token();
    }

    public void testSendMsg() throws Exception {
        logger.debug(URLDecoder.decode("https://zhidao.baidu.com/question/%E5%B1%B1%E4%B8%9C/%E6%B5%8E%E5%8D%97", "utf-8"));
    }
}
