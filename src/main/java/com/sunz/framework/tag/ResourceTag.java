//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.tag;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.util.StringUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

public class ResourceTag extends TagSupport {
    private static Map<String, String> dictResource;
    private static String DefaultTheme = "sunzbase";
    protected static final Logger logger = Logger.getLogger(ResourceTag.class);
    private String items = null;
    private String theme = null;

    public ResourceTag() {
    }

    public static void setResources(Map<String, String> resources) {
        dictResource = resources;
    }

    public static Map getResources() {
        return dictResource;
    }

    public static void setDefaultTheme(String theme) {
        DefaultTheme = theme;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void release() {
        super.release();
        this.theme = null;
        this.items = null;
    }

    public int doEndTag() throws JspException {
        if (!StringUtil.isEmpty(this.items)) {
            String curTheme = this.theme;
            if (curTheme == null) {
                curTheme = (String)this.pageContext.findAttribute("theme");
                if (curTheme == null) {
                    curTheme = DefaultTheme;
                }
            }

            try {
                JspWriter writer = this.pageContext.getOut();
                Iterator var3 = Arrays.asList(StringUtil.parseToArray(this.items)).iterator();

                while(var3.hasNext()) {
                    String k = (String)var3.next();
                    if (dictResource.containsKey(k)) {
                        writer.print(((String)dictResource.get(k)).replaceAll("\\{theme\\}", curTheme));
                    }
                }
            } catch (Exception var5) {
                logger.error("资源加载出错了", var5);
            }
        }

        return 6;
    }

    @Component
    public static class ResourceInitor implements ICacheRefreshable {
        public ResourceInitor() {
        }

        @Autowired
        @Qualifier("resources")
        public void setResources(Map<String, String> resources) {
            if (resources == null) {
                resources = new HashMap();
            }

            ResourceTag.dictResource = (Map)resources;
        }

        @Autowired(
            required = false
        )
        @Qualifier("defaultTheme")
        public void setDefaultTheme(String theme) {
            if (theme != null && !"".equals(theme)) {
                ResourceTag.DefaultTheme = theme;
            }
        }

        @PostConstruct
        public void init() {
            Config.addChangeListener((key) -> {
                this.refreshFromConfig();
            });
        }

        private void refreshFromConfig() {
            this.setResources(Config.getMap("resources"));
            this.setDefaultTheme(Config.get("defaultTheme"));
        }

        public void refresh(String item) {
            Config.refreshItem((String)null);
            this.refreshFromConfig();
        }

        public String getCategory() {
            return "ResourceTag";
        }

        public String getDescription() {
            return "统一资源配置(z:resource标签)";
        }
    }
}
