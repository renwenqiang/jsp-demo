//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.auth;

import com.sunz.framework.core.ICacheRefreshable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthTagHelper implements ICacheRefreshable, ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(AuthTagHelper.class);
    private static Map<String, String> dictDefaultControl;
    private NamedParameterJdbcTemplate template;
    private static Pattern authPattern = Pattern.compile("<z:auth([^>]*)>");
    private static Pattern attrPattern = Pattern.compile("(\\w+)\\s*=\\s*[\"']([^\"']*)");
    private static Map<String, IAuthFilter> dictFilter;
    private static Map<String, IControlTypeHandler> dictHandler;
    public static final IControlTypeHandler emptyHandler = new IControlTypeHandler() {
        public int doStartTag(String filterType, String srourceCode, String srourceName, String params, PageContext context) {
            return 6;
        }

        public int doEndTag(String filterType, String srourceCode, String srourceName, String params, PageContext context, BodyContent content) {
            try {
                if (content != null) {
                    context.getOut().write(content.getString());
                }
            } catch (IOException var8) {
                AuthTagHelper.logger.error(var8);
            }

            return 6;
        }

        public String getCode() {
            return "none";
        }

        public String getName() {
            return "无控制（正向默认值，反向控制时使用）";
        }
    };

    public AuthTagHelper() {
    }

    @Autowired
    public void setTemplate(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @PostConstruct
    public void cache() {
        dictDefaultControl = new HashMap();
        List all = this.template.queryForList("select t.page \"page\",t.code \"code\",t.defaultcontrol \"default\" from T_S_UIResource t", (Map)null);
        Iterator var2 = all.iterator();

        while(var2.hasNext()) {
            Object o = var2.next();
            Map<String, String> map = (Map)o;
            dictDefaultControl.put((String)map.get("page") + "_" + (String)map.get("code"), map.get("default"));
        }

    }

    public void refresh(String item) {
        this.cache();
    }

    public String getCategory() {
        return "uiresource";
    }

    public String getDescription() {
        return "[权限控制]控件资源（UIResource）";
    }

    public static String getJspPath(ServletRequest request) {
        return ((String)request.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH")).substring(1);
    }

    public static String getDefaultControl(String page, String code) {
        return (String)dictDefaultControl.get(page + "_" + code);
    }

    public static List parse(String jsp) {
        File file = new File(jsp);

        try {
            String jspText = FileUtils.readFileToString(file, "utf-8");
            Matcher m = authPattern.matcher(jspText);

            ArrayList resources;
            HashMap map;
            for(resources = new ArrayList(); m.find(); resources.add(map)) {
                map = new HashMap();
                String properties = m.group(1);
                if (properties != null && !"".equals(properties)) {
                    Matcher mAttr = attrPattern.matcher(properties);

                    while(mAttr.find()) {
                        map.put(mAttr.group(1), mAttr.group(2));
                    }
                }
            }

            return resources;
        } catch (Exception var8) {
            throw new RuntimeException("jsp解析出错了");
        }
    }

    public void setFilters(Map<String, IAuthFilter> filters) {
        dictFilter = filters;
    }

    public void setHandlers(Map<String, IControlTypeHandler> handlers) {
        dictHandler = handlers;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        dictFilter = new HashMap();
        dictHandler = new HashMap();
        Map<String, IAuthFilter> allFilters = context.getBeansOfType(IAuthFilter.class);
        Map<String, IControlTypeHandler> allHandlers = context.getBeansOfType(IControlTypeHandler.class);
        allHandlers.put("emptyHandler", emptyHandler);
        Iterator var4 = allFilters.values().iterator();

        while(var4.hasNext()) {
            IAuthFilter filter = (IAuthFilter)var4.next();
            dictFilter.put(filter.getType(), filter);
        }

        var4 = allHandlers.values().iterator();

        while(var4.hasNext()) {
            IControlTypeHandler handler = (IControlTypeHandler)var4.next();
            dictHandler.put(handler.getCode(), handler);
        }

    }

    public static IAuthFilter getFilter(String type) {
        return (IAuthFilter)dictFilter.get(type);
    }

    public static IControlTypeHandler getHandler(String type) {
        return (IControlTypeHandler)dictHandler.get(type);
    }

    public static List getControlTypes() {
        List types = new ArrayList();
        Iterator var1 = dictHandler.values().iterator();

        while(var1.hasNext()) {
            IControlTypeHandler filter = (IControlTypeHandler)var1.next();
            types.add(filter);
        }

        return types;
    }
}
