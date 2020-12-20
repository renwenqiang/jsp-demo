//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.qyweixin;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.core.Config;
import com.sunz.framework.extend.dingtalk.DingDingUtil;
import com.sunz.framework.extend.dingtalk.OApiException;
import com.sunz.framework.extend.dingtalk.OApiResultException;
import com.sunz.framework.extend.util.HttpUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class QyWeiXinUtil {
    private static Logger log = Logger.getLogger(QyWeiXinUtil.class);
    private static String AppID;
    private static String AppSecret;
    private static String AgentId;
    private static String QyWeixinOapiUrl;
    public static final int OUT_TIME = 1000;
    private static Date Last_Access_Token_Time;
    private static Date Last_Ticket_Time;
    private static String Access_Token;
    private static String Ticket;

    public QyWeiXinUtil() {
    }

    public static String getAppID() {
        if (AppID == null) {
            AppID = Config.get("qyweixin.AppID");
        }

        return AppID;
    }

    public static String getAppSecret() {
        if (AppSecret == null) {
            AppSecret = Config.get("qyweixin.AppSecret");
        }

        return AppSecret;
    }

    public static String getAgentId() {
        if (AgentId == null) {
            AgentId = Config.get("qyweixin.AgentId");
        }

        return AgentId;
    }

    public static String getQyWeixinOapiUrl() {
        if (QyWeixinOapiUrl == null) {
            QyWeixinOapiUrl = Config.get("qyweixin.QyWeixinOapiUrl");
        }

        if (QyWeixinOapiUrl == null) {
            QyWeixinOapiUrl = "https://api.weixin.qq.com";
        }

        return QyWeixinOapiUrl;
    }

    public static void setWeixOapiUrl(String WeixOapiUrl) {
    }

    public static String getAccess_Token() throws Exception {
        Date now = new Date();
        if (Access_Token == null) {
            Access_Token = getAccess_TokenFromServer();
            Last_Access_Token_Time = now;
        } else {
            Date outDate = new Date(Last_Access_Token_Time.getTime() + 1000L);
            log.info("当前时间：" + now.toLocaleString() + "---超时时间：" + outDate.toLocaleString());
            if (now.getTime() > outDate.getTime()) {
                log.info("已超时，重新请求");
                Access_Token = getAccess_TokenFromServer();
                Last_Access_Token_Time = now;
            } else {
                log.info("未超时");
            }
        }

        log.info("getAccess_Token-->" + Access_Token);
        return Access_Token;
    }

    public static String getAccess_TokenFromServer() throws Exception {
        String AppID = getAppID();
        String AppSecret = getAppSecret();
        String accessToken_url = getQyWeixinOapiUrl() + "/gettoken?corpid=" + AppID + "&corpsecret=" + AppSecret;
        String accessToken_str = HttpUtil.sendGet(accessToken_url, "UTF-8");

        try {
            Map<String, Integer> map = (Map)JSONObject.parse(accessToken_str);
            if (accessToken_str.indexOf("errcode") > -1) {
                int errcode = (Integer)map.get("errcode");
                if (errcode != 0) {
                    throw new Exception(accessToken_str);
                }
            }

            Access_Token = map.get("access_token") + "";
            log.info("getAccess_TokenFromServer-->" + Access_Token);
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return Access_Token;
    }

    public static String getTicket() throws Exception {
        Date now = new Date();
        if (Ticket == null) {
            Ticket = getTicketFromServer(getAccess_Token());
            Last_Ticket_Time = now;
        } else {
            Date outDate = new Date(Last_Ticket_Time.getTime() + 1000L);
            if (now.getTime() > outDate.getTime()) {
                log.info("已超时，重新请求服务器");
                Ticket = getTicketFromServer(getAccess_Token());
                Last_Ticket_Time = now;
            }
        }

        log.info("getTicket-->" + Ticket + "accessToken-->" + getAccess_Token());
        return Ticket;
    }

    public static String getTicketFromServer(String access_token) throws Exception {
        String ticket_url = getQyWeixinOapiUrl() + "/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        String ticket_str = httpGet(ticket_url, "UTF-8");

        try {
            Map<String, Integer> map = (Map)JSONObject.parse(ticket_str);
            int errcode = (Integer)map.get("errcode");
            if (errcode != 0) {
                Ticket = DingDingUtil.getTicketFromServer(access_token);
            } else {
                Ticket = map.get("ticket") + "";
            }

            log.info("getTicketFromServer-->" + Ticket + "access_token-->" + access_token);
        } catch (JSONException var5) {
            var5.printStackTrace();
        }

        return Ticket;
    }

    public static Map<String, String> getUserInfoByCode(String Code) throws Exception {
        String access_token = getAccess_Token();
        String url = getQyWeixinOapiUrl() + "/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE";
        url = url.replace("ACCESS_TOKEN", access_token);
        url = url.replace("CODE", Code);
        String user_str = httpGet(url, "UTF-8");
        log.info(user_str);
        Map map = null;

        try {
            map = (Map)JSONObject.parse(user_str);
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return map;
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
            log.error("token失效 重新获取token");
            getAccess_TokenFromServer();
            return false;
        } else {
            return true;
        }
    }

    public static String sign(String noncestr, String jsapi_ticket, String timestamp, String url) throws OApiException {
        String plain = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            String signature = bytesToHex(sha1.digest());
            log.info(noncestr);
            log.info(jsapi_ticket);
            log.info(String.valueOf(timestamp));
            log.info(url);
            log.info(plain);
            log.info(signature);
            return signature;
        } catch (NoSuchAlgorithmException var7) {
            throw new OApiResultException(var7.getMessage());
        } catch (UnsupportedEncodingException var8) {
            throw new OApiResultException(var8.getMessage());
        }
    }

    public static String getConfig(String baseUrl) throws Exception {
        String ticket = getTicket();
        String accessToken = getAccess_Token();
        String nonceStr = "sunztech.com";
        long _timeStamp = (new Date()).getTime();
        String timeStamp = String.valueOf(_timeStamp).substring(0, 9);
        String url = baseUrl;
        String agentid = "";
        String signature = "";

        try {
            signature = sign(nonceStr, ticket, timeStamp, url);
        } catch (OApiException var11) {
            var11.printStackTrace();
        }

        String appId = "";
        return "{signature:'" + signature + "',nonceStr:'" + nonceStr + "',timestamp:'" + timeStamp + "',appId:'" + getAppID() + "',url:'" + baseUrl + "' }";
    }

    public static String getConfig(HttpServletRequest request, boolean useReferer) throws Exception {
        String ticket = getTicket();
        String url = "";
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
        url = basePath + Config.get("qyweixin.loginurl");
        if (useReferer) {
            log.info(request.getHeader("Referer"));
            url = request.getHeader("Referer");
        }

        url = URLDecoder.decode(url, "utf-8");
        return getConfig(url);
    }

    public void test() throws Exception {
        try {
            String code = "H7TQ3CtwhKund06z4ZMiuCMGbxJSBpo37lJkHxnISz0";
            getUserInfoByCode(code);
        } catch (Exception var2) {
            System.out.print("var2.getMessage()");
            System.out.print(var2.getMessage());
        }

    }
}
