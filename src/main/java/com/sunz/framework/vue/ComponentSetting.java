//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.vue;

import com.sunz.framework.core.Entity;
import java.util.Date;
import javax.persistence.Table;

@Table(
    name = "t_s_vuecomponent"
)
public class ComponentSetting extends Entity {
    String code;
    String path;
    String remark;
    String resources;
    String innerResources;
    Date addTime;
    String title;

    public ComponentSetting() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getResources() {
        return this.resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getInnerResources() {
        return this.innerResources;
    }

    public void setInnerResources(String innerResources) {
        this.innerResources = innerResources;
    }

    public Date getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
