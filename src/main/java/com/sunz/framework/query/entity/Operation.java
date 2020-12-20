//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_Query_Operation"
)
public class Operation extends com.sunz.framework.core.Entity {
    private String queryid;
    private String text;
    private String icon;
    private String jsHandler;
    private int orderIndex;

    public Operation() {
    }

    public String getQueryid() {
        return this.queryid;
    }

    public void setQueryid(String queryid) {
        this.queryid = queryid;
    }

    public int getOrderIndex() {
        return this.orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getText() {
        return this.text;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getJsHandler() {
        return this.jsHandler;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setJsHandler(String jsHandler) {
        this.jsHandler = jsHandler;
    }
}
