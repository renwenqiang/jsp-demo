//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.auth.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_UIResource"
)
public class UIResource extends com.sunz.framework.core.Entity {
    private String code;
    private String name;
    private String page;
    private String remark;
    private String type;
    private String defaultcontrol;

    public UIResource() {
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getPage() {
        return this.page;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultcontrol() {
        return this.defaultcontrol;
    }

    public void setDefaultcontrol(String defaultcontrol) {
        this.defaultcontrol = defaultcontrol;
    }
}
