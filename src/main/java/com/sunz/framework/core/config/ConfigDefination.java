//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.config;

import com.sunz.framework.core.Entity;
import com.sunz.framework.util.StringUtil;
import java.util.regex.Pattern;

public class ConfigDefination extends Entity {
    String name;
    String code;
    String value;
    String remark;
    boolean foreground;
    boolean persist;
    String locale;
    boolean background;
    boolean springbean;
    String group;
    boolean system;
    public static Pattern patternInclude = Pattern.compile("\\$\\{\\s*(\\S+)\\s*\\}");
    private boolean incudeResolved = false;

    public ConfigDefination() {
    }

    public boolean isSpringbean() {
        return this.springbean;
    }

    public void setSpringbean(boolean springbean) {
        this.springbean = springbean;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isBackground() {
        return this.background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isForeground() {
        return this.foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    public boolean isPersist() {
        return this.persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isSystem() {
        return this.system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isIncudeResolved() {
        return this.incudeResolved;
    }

    public void setIncudeResolved(boolean resolvedIncude) {
        this.incudeResolved = resolvedIncude;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        this.incudeResolved = StringUtil.isEmpty(this.value) || !patternInclude.matcher(this.value).find();
    }
}
