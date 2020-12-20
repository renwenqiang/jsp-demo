//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.dict;

import com.sunz.framework.util.StringUtil;
import java.util.List;

public class DictItem {
    String id;
    String code;
    String text;
    String remark;
    String parentid;
    int order;
    List<DictItem> children;
    private String origin;
    String xString;

    public DictItem() {
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DictItem> getChildren() {
        return this.children;
    }

    public void setChildren(List<DictItem> children) {
        this.children = children;
    }

    public String getParentid() {
        return this.parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getRemark() {
        return this.remark;
    }

    public int getOrder() {
        return this.order;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String toXString(String template) {
        return StringUtil.isEmpty(this.xString) ? (this.xString = String.format(template, this.getId(), this.getCode(), this.getText(), StringUtil.ifEmpty(this.getParentid(), ""), this.getOrder())) : this.xString;
    }
}
