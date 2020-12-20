//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.tag;

import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;

public class ConfigTag extends TagSupport {
    protected static final Logger logger = Logger.getLogger(ConfigTag.class);
    private String items;
    private String groups;
    private static final String HTML_IMPORT = "<script type=\"text/javascript\" src=\"" + Config.getJsPath(true) + "\"></script>";

    public ConfigTag() {
    }

    public void setItems(String items) {
        this.items = items;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public int doStartTag() throws JspException {
        return 6;
    }

    public int doEndTag() throws JspException {
        try {
            String content;
            if ("all".equalsIgnoreCase(this.items)) {
                content = HTML_IMPORT;
            } else {
                if (this.items == null) {
                    this.items = "";
                }

                if (!StringUtil.isEmpty(this.groups)) {
                    String[] var2 = StringUtil.parseToArray(this.groups);
                    int var3 = var2.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        String item = var2[var4];
                        this.items = this.items + "," + Config.get(item);
                    }
                }

                content = "<script type=\"text/javascript\" >" + Config.getJsContent(StringUtil.parseToArray(this.items), true) + "</script>";
            }

            this.pageContext.getOut().print(content);
        } catch (Exception var6) {
            logger.error("系统配置参数【" + this.items + "】加载出错了", var6);
        }

        return 6;
    }
}
