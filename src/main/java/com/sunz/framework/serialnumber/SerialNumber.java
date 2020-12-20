//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.serialnumber;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_SERIALNUMBER_INFO"
)
public class SerialNumber extends com.sunz.framework.core.Entity {
    private String key;
    private String fixedprefix;
    private String dynamicprefix;
    private int rulelength;
    private String datestyle;
    private String ruleexpress;
    private String rulename;

    public SerialNumber() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFixedprefix() {
        return this.fixedprefix;
    }

    public void setFixedprefix(String fixedprefix) {
        this.fixedprefix = fixedprefix;
    }

    public String getDynamicprefix() {
        return this.dynamicprefix;
    }

    public void setDynamicprefix(String dynamicprefix) {
        this.dynamicprefix = dynamicprefix;
    }

    public int getRulelength() {
        return this.rulelength;
    }

    public void setRulelength(int rulelength) {
        this.rulelength = rulelength;
    }

    public String getDatestyle() {
        return this.datestyle;
    }

    public void setDatestyle(String datestyle) {
        this.datestyle = datestyle;
    }

    public String getRuleexpress() {
        return this.ruleexpress;
    }

    public void setRuleexpress(String ruleexpress) {
        this.ruleexpress = ruleexpress;
    }

    public String getRulename() {
        return this.rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }
}
