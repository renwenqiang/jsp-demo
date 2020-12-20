//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.auth.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_UIResource_Step"
)
public class UIResourceStep extends com.sunz.framework.core.Entity {
    private String resourceid;
    private String jobkey;
    private String stepkey;
    private String controlType;

    public UIResourceStep() {
    }

    public String getResourceid() {
        return this.resourceid;
    }

    public String getJobkey() {
        return this.jobkey;
    }

    public String getStepkey() {
        return this.stepkey;
    }

    public String getControlType() {
        return this.controlType;
    }

    public void setResourceid(String resourceid) {
        this.resourceid = resourceid;
    }

    public void setJobkey(String jobkey) {
        this.jobkey = jobkey;
    }

    public void setStepkey(String stepkey) {
        this.stepkey = stepkey;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }
}
