//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.tag;

import com.sunz.framework.auth.AuthTagHelper;
import com.sunz.framework.auth.IAuthFilter;
import com.sunz.framework.auth.IControlTypeHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class AuthFilterTag extends BodyTagSupport {
    private final String Type_Default = "step";
    String code;
    String name;
    String type;
    String params;
    String defaultControl;
    private IControlTypeHandler typeHandler;

    public AuthFilterTag() {
    }

    public void setDefault(String def) {
        this.defaultControl = def;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int doEndTag() {
        return this.typeHandler.doEndTag(this.type, this.code, this.name, this.params, this.pageContext, this.bodyContent);
    }

    public int doStartTag() {
        if (this.type == null) {
            this.type = "step";
        }

        IAuthFilter filter = AuthTagHelper.getFilter(this.type);
        String control = filter.getControlType(this.code, this.params, (HttpServletRequest)this.pageContext.getRequest());
        if (control == null) {
            control = AuthTagHelper.getDefaultControl(AuthTagHelper.getJspPath(this.pageContext.getRequest()), this.code);
            if (control == null) {
                control = this.defaultControl;
            }
        }

        if (control == null) {
            this.typeHandler = AuthTagHelper.emptyHandler;
        } else {
            this.typeHandler = AuthTagHelper.getHandler(control);
        }

        if (this.typeHandler == null) {
            this.typeHandler = AuthTagHelper.emptyHandler;
        }

        return this.typeHandler.doStartTag(this.type, this.code, this.name, this.params, this.pageContext);
    }
}
