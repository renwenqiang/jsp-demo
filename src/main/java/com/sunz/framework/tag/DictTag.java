//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.tag;

import com.sunz.framework.dict.DictHelper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.log4j.Logger;

public class DictTag extends TagSupport {
    protected static final Logger logger = Logger.getLogger(DictTag.class);
    private String items;
    private static final String HTML_IMPORT = "<script type=\"text/javascript\" src=\"" + DictHelper.getJsPath(true) + "\"></script>";

    public DictTag() {
    }

    public void setItems(String items) {
        this.items = items;
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
                content = "<script type=\"text/javascript\" >" + DictHelper.getJsContent(this.items) + "</script>";
            }

            this.pageContext.getOut().print(content);
        } catch (Exception var2) {
            logger.error("数据字典【" + this.items + "】加载出错了", var2);
        }

        return 6;
    }
}
