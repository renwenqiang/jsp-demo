//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.im.rongim;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.core.Config;
import com.sunz.framework.extend.dingtalk.OApiException;
import com.sunz.framework.extend.dingtalk.OApiResultException;
import com.sunz.framework.extend.util.HttpUtil;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class RongIMUtil {
    private static Logger log = Logger.getLogger(RongIMUtil.class);
    private static String nonceStr = "sunztech.com";

    public RongIMUtil() {
    }

    public static String getAppKey() {
        return Config.get("rong.AppKey");
    }

    public static String getAppSecret() {
        return Config.get("rong.AppSecret");
    }

    public static String getOapiUrl() {
        String OapiUrl = Config.get("rong.OapiUrl");
        if (OapiUrl == null) {
            OapiUrl = "http://api.cn.ronghub.com/";
        }

        return OapiUrl;
    }

    public static String getTokenFromServer(String userId, String name, String portraitUri) throws Exception {
        String token = null;
        String token_url = getOapiUrl() + "user/getToken.json";
        Map<String, String> param = new HashMap();
        param.put("userId", userId);
        param.put("name", name);
        param.put("portraitUri", portraitUri);
        String token_str = httpPost(token_url, param);
        log.info("getRong_TokenFromServer-->" + token_str);

        try {
            Map<String, Integer> map = (Map)JSONObject.parse(token_str);
            if (token_str.indexOf("errcode") > -1) {
                int errcode = (Integer)map.get("errcode");
                throw new Exception(token_str);
            }

            token = map.get("token") + "";
            log.info("getRong_TokenFromServer-->" + token);
        } catch (JSONException var9) {
            var9.printStackTrace();
        }

        return token;
    }

    public static String sign(String appSecret, String nonceStr, long timeStamp) throws OApiException {
        String plain = appSecret + nonceStr + timeStamp;

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return bytesToHex(sha1.digest());
        } catch (NoSuchAlgorithmException var6) {
            throw new OApiResultException(var6.getMessage());
        } catch (UnsupportedEncodingException var7) {
            throw new OApiResultException(var7.getMessage());
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

    private static boolean checkResult(String result) throws Exception {
        JSONObject jsonObj = JSONObject.parseObject(result);
        if (jsonObj.getLong("code") != 200L) {
            log.error("token失效 重新获取token");
            return false;
        } else {
            return true;
        }
    }

    public static String httpGet(String url) throws Exception {
        String result = httpGet(url, "UTF-8");
        return result;
    }

    public static String httpGet(String url, String charset) throws Exception {
        String result = HttpUtil.get(url, charset);
        if (!checkResult(result)) {
            result = httpGet(url, charset);
        }

        return result;
    }

    public static String httpPost(String url, Map<String, String> data) throws Exception {
        long timeStamp = (new Date()).getTime();
        String Signature = sign(getAppSecret(), nonceStr, timeStamp);
        Map<String, String> header = new HashMap();
        header.put("App-Key", getAppKey());
        header.put("Nonce", nonceStr);
        header.put("Timestamp", String.valueOf(timeStamp));
        header.put("Signature", Signature);
        String result = null;
        result = HttpUtil.sendPostP(url, header, data, "UTF-8", "application/x-www-form-urlencoded");
        if (!checkResult(result)) {
            result = httpPost(url, data);
        }

        return result;
    }

    public void test() throws Exception {
        try {
            String token = getTokenFromServer("user2", "user2", "222");
            log.debug(token);
        } catch (Exception var2) {
            System.out.print("var2.getMessage()");
            System.out.print(var2.getMessage());
        }

    }
}
