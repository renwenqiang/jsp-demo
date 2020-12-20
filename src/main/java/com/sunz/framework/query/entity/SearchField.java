//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_Query_SearchField"
)
public class SearchField extends com.sunz.framework.core.Entity {
    private String queryid;
    private String name;
    private String title;
    private String inputType;
    private String styles;
    private int orderIndex;

    public SearchField() {
    }

    public String getQueryid() {
        return this.queryid;
    }

    public void setQueryid(String queryid) {
        this.queryid = queryid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInputType() {
        return this.inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getStyles() {
        return this.styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public int getOrderIndex() {
        return this.orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
