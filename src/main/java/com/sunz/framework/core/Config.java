//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import com.sunz.framework.core.config.ConfigDefination;
import com.sunz.framework.util.StringUtil;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

public class Config {
    static Logger logger = Logger.getLogger(Config.class);
    static NamedParameterJdbcTemplate jdbcTemplate;
    static String webRoot;
    static String User_Locale;
    static Map<String, String> propertyFileConfigs = new HashMap();
    static ServletContext application;
    static BeanPropertyRowMapper<ConfigDefination> rowMapper = new BeanPropertyRowMapper<ConfigDefination>(ConfigDefination.class) {
        protected void initialize(Class mappedClass) {
            super.initialize(mappedClass);
            Map mappedFields = null;

            try {
                Field f = this.getClass().getSuperclass().getDeclaredField("mappedFields");
                f.setAccessible(true);
                mappedFields = (Map)f.get(this);
            } catch (Exception var8) {
                this.logger.debug("", var8);
            }

            Object[] var9 = mappedFields.entrySet().toArray();
            int var4 = var9.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Object o = var9[var5];
                Entry<String, PropertyDescriptor> en = (Entry)o;
                mappedFields.put((String)en.getKey() + "_", en.getValue());
            }

        }

        public ConfigDefination mapRow(ResultSet rs, int rowNumber) throws SQLException {
            ConfigDefination c = (ConfigDefination)super.mapRow(rs, rowNumber);
            Config.cache.put(c.getCode(), c);
            if (Config.isGrouped(c)) {
                String g = c.getGroup();
                List<ConfigDefination> gList = (List)Config.cacheGroup.get(g);
                if (gList == null) {
                    Config.cacheGroup.put(g, gList = new ArrayList());
                }

                ((List)gList).add(c);
            }

            if (c.isBackground()) {
                Config.application.setAttribute(c.getCode(), c.getValue());
            }

            return c;
        }
    };
    static Map<String, ConfigDefination> cache = new HashMap();
    static Map<String, List<ConfigDefination>> cacheGroup = new HashMap();
    private static Map<String, List<String>> dependenceMap = new HashMap();
    private static String jsPath = "webpage/framework/resource/js/config.js";
    private static Long updateTime = (new Date()).getTime();
    private static String allForegroundJs;
    private static List<Config.ChangeHandler> changeHandlers = new ArrayList();

    public Config() {
    }

    static boolean isGrouped(ConfigDefination c) {
        return c.getGroup() != null && c.getGroup().trim().length() > 0;
    }

    private static String resolveInclude(String code) {
        ConfigDefination def = (ConfigDefination)cache.get(code);
        if (def == null) {
            logger.warn("系统配置中发现未定义的占位符【" + code + "】，将保留原值，请确认是否为失误或遗漏！");
            return code;
        } else {
            String value = def.getValue();
            if (def.isIncudeResolved()) {
                return value;
            } else {
                StringBuffer sb = new StringBuffer();
                Matcher m = ConfigDefination.patternInclude.matcher(value);
                int lastPosition = 0;

                while(m.find()) {
                    String subKey = m.group(1);
                    List<String> relyList = (List)dependenceMap.get(subKey);
                    if (relyList == null) {
                        relyList = new ArrayList();
                        dependenceMap.put(subKey, relyList);
                    }

                    if (!((List)relyList).contains(code)) {
                        ((List)relyList).add(code);
                    }

                    sb.append(value.substring(lastPosition, m.start()));
                    lastPosition = m.end();
                    sb.append(resolveInclude(subKey));
                }

                sb.append(value.substring(lastPosition));
                value = sb.toString();
                def.setValue(value);
                def.setIncudeResolved(true);
                return value;
            }
        }
    }

    public static String getJsPath(boolean avoidCache) {
        return avoidCache ? jsPath + "?t=" + updateTime : jsPath;
    }

    private static void generateJs() {
        try {
            (new FileOutputStream(webRoot + jsPath)).write(getJsContent(new String[]{"all"}, false).getBytes("utf-8"));
            updateTime = (new Date()).getTime();
        } catch (Exception var1) {
            logger.warn("配置项前端js生成出错，此错误可能会使得那些要求了所有配置项的页面无法正常使用！", var1);
        }

    }

    private static void cacheForeground() {
        StringBuilder sb = new StringBuilder();
        Iterator var1 = cache.values().iterator();

        while(var1.hasNext()) {
            ConfigDefination c = (ConfigDefination)var1.next();
            if (c.isForeground()) {
                getJsFragment(c, sb);
            }
        }

        allForegroundJs = sb.toString();
    }

    private static void getJsFragment(ConfigDefination c, StringBuilder sb) {
        if (c.getValue() != null) {
            sb.append(",\"");
            sb.append(c.getCode());
            sb.append("\":");
            if (c.getValue() == null) {
                sb.append("null");
            } else {
                sb.append("`");
                sb.append(c.getValue());
                sb.append("`");
            }

        }
    }

    private static void getJsFragment(Collection<ConfigDefination> defines, StringBuilder sb, boolean unlimit) {
        Iterator var3 = defines.iterator();

        while(true) {
            while(var3.hasNext()) {
                ConfigDefination c = (ConfigDefination)var3.next();
                if (!unlimit && !c.isForeground()) {
                    logger.warn("页面试图在前端直接【越权】使用配置项【" + c.getCode() + "】，系统已阻止；请慎重考虑将此配置项向前端开放或修改使用方式");
                } else {
                    getJsFragment(c, sb);
                }
            }

            return;
        }
    }

    public static String getJsContent(String[] items, boolean unlimit) {
        Collection<ConfigDefination> defines = new ArrayList();
        StringBuilder sb = new StringBuilder("window.C=window.Config=$.extend(window.C,{get:function(k){return C[k]},requireResources:function(){/*resources,configs,configGroups,sync 支持{}传参，分开传时最后一个为是否同步*/var args=arguments,o=args[0],sync;if(typeof o == 'string'){o={},ns=['resources','configs','configGroups'];$.each(arguments,function(i,s){if(typeof s=='boolean'){sync=s;return false}else{o[ns[i]]=s}});}else{sync=o.sync}$.ajax('framework/config.do?getResources&'+$.param(o),{async:!sync,method:'GET',dataType:'text',success:function(r){$(r).appendTo(document.head);}})}");
        if (items != null) {
            String[] var4 = items;
            int var5 = items.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String k = var4[var6];
                if ("all".equals(k)) {
                    sb.append(allForegroundJs);
                } else if (cache.containsKey(k)) {
                    defines.add(cache.get(k));
                }
            }
        }

        getJsFragment(defines, sb, unlimit);
        return sb.append("});").toString();
    }

    public static void addChangeListener(Config.ChangeHandler handler) {
        changeHandlers.add(handler);
    }

    private static void reloadItem(String k) {
        List list = jdbcTemplate.getJdbcOperations().query("SELECT * FROM T_S_CONFIG WHERE CODE_=? AND (LOCALE_ IS NULL OR LOCALE_='' OR LOCALE_=?) ORDER BY　(CASE WHEN LOCALE_ IS NULL OR LOCALE_='' THEN 0 ELSE 1 END) ASC", new Object[]{k, User_Locale}, rowMapper);
        resolveInclude(k);
        Iterator var2 = changeHandlers.iterator();

        while(var2.hasNext()) {
            Config.ChangeHandler handler = (Config.ChangeHandler)var2.next();
            handler.onChange(k);
        }

    }

    public static String get(String k) {
        if (propertyFileConfigs.containsKey(k)) {
            return (String)propertyFileConfigs.get(k);
        } else {
            return cache.containsKey(k) ? ((ConfigDefination)cache.get(k)).getValue() : null;
        }
    }

    public static void set(String k, String v) {
        if (propertyFileConfigs.containsKey(k)) {
            propertyFileConfigs.put(k, v);
            logger.warn("修改了由properties文件提供的参数值，仅在本次运行期间生效");
        }

        ConfigDefination c;
        if (cache.containsKey(k)) {
            c = (ConfigDefination)cache.get(k);
            c.setValue(v);
            resolveInclude(k);
            if (c.isPersist()) {
                jdbcTemplate.getJdbcOperations().update("UPDATE T_S_CONFIG SET value_ =? where id=?", new Object[]{v, c.getId()});
            }
        } else {
            c = new ConfigDefination();
            c.setCode(k);
            c.setValue(v);
            jdbcTemplate.getJdbcOperations().update("INSERT INTO T_S_CONFIG(code_,value_) values(?,?)", new Object[]{k, v});
            reloadItem(k);
        }

    }

    public static void refreshItem(String item) {
        refreshItem(item, true);
    }

    private static void refreshItem(String item, boolean effected) {
        List relyList;
        if (StringUtil.isEmpty(item)) {
            cache.clear();
            cacheGroup.clear();
            dependenceMap.clear();
            List baseList = jdbcTemplate.getJdbcOperations().query("SELECT * FROM T_S_CONFIG WHERE LOCALE_ IS NULL OR LOCALE_=''", rowMapper);
            if (User_Locale != null && !"".equals(User_Locale)) {
                relyList = jdbcTemplate.getJdbcOperations().query("SELECT * FROM T_S_CONFIG WHERE LOCALE_=?", new Object[]{User_Locale}, rowMapper);
            }

            Iterator var7 = cache.keySet().iterator();

            while(var7.hasNext()) {
                String k = (String)var7.next();
                resolveInclude(k);
            }

            var7 = changeHandlers.iterator();

            while(var7.hasNext()) {
                Config.ChangeHandler handler = (Config.ChangeHandler)var7.next();
                handler.onChange((String)null);
            }
        } else {
            if (cache.containsKey(item)) {
                ConfigDefination c = (ConfigDefination)cache.get(item);
                if (isGrouped(c)) {
                    relyList = (List)cacheGroup.get(c.getGroup());
                    if (relyList != null && relyList.contains(c)) {
                        relyList.remove(c);
                    }
                }

                cache.remove(item);
                relyList = (List)dependenceMap.get(item);
                if (relyList != null) {
                    Iterator var9 = relyList.iterator();

                    while(var9.hasNext()) {
                        String relyKey = (String)var9.next();
                        refreshItem(relyKey, false);
                    }
                }
            }

            reloadItem(item);
        }

        if (effected) {
            cacheForeground();
            generateJs();
        }

    }

    public static List<String> getList(String group) {
        if (!cacheGroup.containsKey(group)) {
            return null;
        } else {
            List<String> subs = new ArrayList();
            Iterator var2 = ((List)cacheGroup.get(group)).iterator();

            while(var2.hasNext()) {
                ConfigDefination c = (ConfigDefination)var2.next();
                subs.add(c.getValue());
            }

            return subs;
        }
    }

    public static Map<String, String> getMap(String group) {
        if (!cacheGroup.containsKey(group)) {
            return null;
        } else {
            Map<String, String> subs = new HashMap();
            Iterator var2 = ((List)cacheGroup.get(group)).iterator();

            while(var2.hasNext()) {
                ConfigDefination c = (ConfigDefination)var2.next();
                subs.put(c.getCode(), c.getValue());
            }

            return subs;
        }
    }

    public static List<Map<String, String>> getListMap(String group) {
        if (!cacheGroup.containsKey(group)) {
            return null;
        } else {
            List<Map<String, String>> subs = new ArrayList();
            Iterator var2 = ((List)cacheGroup.get(group)).iterator();

            while(var2.hasNext()) {
                ConfigDefination c = (ConfigDefination)var2.next();
                Map<String, String> m = new HashMap();
                m.put(c.getCode(), c.getValue());
                subs.add(m);
            }

            return subs;
        }
    }

    public static Collection<ConfigDefination> getAllDefinations() {
        return cache.values();
    }

    public static ConfigDefination getDefination(String k) {
        return (ConfigDefination)cache.get(k);
    }

    @Component
    public static class ConfigInitor extends WebRootSupport implements ICacheRefreshable, ServletContextAware {
        public ConfigInitor() {
        }

        @Autowired
        @Qualifier("namedParameterJdbcTemplate")
        public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            Config.jdbcTemplate = namedParameterJdbcTemplate;
        }

        @Autowired(
            required = false
        )
        @Qualifier("config.locale")
        public void setUserConfigLocale(String locale) {
            Config.User_Locale = locale;
        }

        public void setServletContext(ServletContext servletContext) {
            Config.application = servletContext;
        }

        @PostConstruct
        public void init() {
            try {
                Config.refreshItem((String)null, true);
            } catch (Exception var2) {
                Config.logger.warn("系统配置初始化失败，依赖此功能的模块将不可用", var2);
            }

        }

        public void refresh(String item) {
            Config.refreshItem(item, true);
        }

        public String getCategory() {
            return "config";
        }

        public String getDescription() {
            return "系统参数配置--即一般性properties配置";
        }

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            super.setApplicationContext(applicationContext);
            Config.webRoot = webRoot;
        }

        public void setFileConfigs(Map<String, String> configs) {
            Config.propertyFileConfigs.putAll(configs);
        }
    }

    @FunctionalInterface
    public interface ChangeHandler {
        void onChange(String var1);
    }
}
