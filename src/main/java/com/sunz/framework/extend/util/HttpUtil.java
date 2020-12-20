//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
    private static String defaultCharset = "utf-8";

    public HttpUtil() {
    }

    public static String get(String url) {
        return get(url, defaultCharset);
    }

    public static String get(String url, String charset) {
        return sendGet(url, charset);
    }

    public static String sendGet(String url, String charset) {
        String result = "";
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();

            String line;
            for(in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset)); (line = in.readLine()) != null; result = result + line) {
            }
        } catch (Exception var15) {
            System.out.println("发送GET请求出现异常！" + var15);
            var15.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception var14) {
                var14.printStackTrace();
            }

        }

        return result;
    }

    public static String sendPostUrl(String url, String param, String charset) {
        return sendPostUrl(url, param, charset, "text/html;charset=utf-8");
    }

    public static String sendPostUrl(String url, String param, String charset, String contentType) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();

            String line;
            for(in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset)); (line = in.readLine()) != null; result = result + line) {
            }
        } catch (Exception var18) {
            System.out.println("发送 POST 请求出现异常！" + var18);
            var18.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            }

        }

        return result;
    }

    public static String sendPost(String url, Map<String, String> param, String charset) {
        return sendPost(url, param, charset, "text/html;charset=utf-8");
    }

    public static String sendPost(String url, Map<String, String> param, String charset, String contentType) {
        String result = "";
        result = sendPostP(url, (Map)null, (Map)param, charset, contentType);
        return result;
    }

    public static String sendPostP(String url, Map<String, String> header, Map<String, String> param, String charset, String contentType) {
        StringBuffer buffer = new StringBuffer();
        if (param != null && !param.isEmpty()) {
            Iterator var6 = param.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<String, String> entry = (Entry)var6.next();
                buffer.append((String)entry.getKey()).append("=").append(URLEncoder.encode((String)entry.getValue())).append("&");
            }
        }

        buffer.deleteCharAt(buffer.length() - 1);
        return sendPostP(url, header, buffer, charset, contentType);
    }

    public static String sendPostP(String url, Map<String, String> header, StringBuffer buffer, String charset, String contentType) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", contentType);
            if (header != null) {
                Set<String> keys = header.keySet();
                Iterator var11 = keys.iterator();

                while(var11.hasNext()) {
                    String key = (String)var11.next();
                    conn.setRequestProperty(key, (String)header.get(key));
                }
            }

            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(buffer);
            out.flush();

            String line;
            for(in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset)); (line = in.readLine()) != null; result = result + line) {
            }
        } catch (Exception var21) {
            System.out.println("发送 POST 请求出现异常！" + var21);
            var21.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }
            } catch (IOException var20) {
                var20.printStackTrace();
            }

        }

        return result;
    }

    public static String httpPost(String url, Object data) throws ClientProtocolException, IOException {
        String jsonString = httpPost(url, (Map)null, (Map)null, data);
        return jsonString;
    }

    public static String httpPost(String url, Map<String, String> header, Map<String, String> param, Object data) throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(url);
        if (header != null) {
            Set<String> keys = header.keySet();
            Iterator var6 = keys.iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                httpPost.setHeader(key, (String)header.get(key));
            }
        }

        HttpParams p = httpPost.getParams();
        String jsonString;
        if (param != null) {
            Set<String> keys = param.keySet();
            Iterator var12 = keys.iterator();

            while(var12.hasNext()) {
                jsonString = (String)var12.next();
                p.setParameter(jsonString, param.get(jsonString));
            }
        }

        httpPost.setParams(p);
        DefaultHttpClient client = new DefaultHttpClient();
        if (data != null) {
            StringEntity entity = new StringEntity(data.toString(), "UTF-8");
            entity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(entity);
        }

        HttpResponse response = client.execute(httpPost);
        jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
        return jsonString;
    }

    public static void main(String[] args) {
        String corpid = "";
        String corpsecret = "";
        String accessToken_url = "https://oapi.dingtalk.com/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;
        String aaa = sendGet(accessToken_url, "UTF-8");
        System.out.println(aaa);
    }
}
