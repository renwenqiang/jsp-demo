//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.print.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_PrintElementSetting"
)
public class PrintElementSetting extends com.sunz.framework.core.Entity {
    private String formid;
    private String element;
    private String setting;

    public PrintElementSetting() {
    }

    public String getFormid() {
        return this.formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public String getElement() {
        return this.element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getSetting() {
        return this.setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
