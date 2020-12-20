//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport;

public class TableColumnInfo {
    String name;
    String comment;
    String datatype;
    long length;
    int precision;
    int scale;
    boolean nullable;
    String defaultValue;
    private int commonDatatype;

    public TableColumnInfo() {
    }

    public String getName() {
        return this.name;
    }

    public String getComment() {
        return this.comment;
    }

    public long getLength() {
        return this.length;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getDatatype() {
        return this.datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getCommonDatatype() {
        return this.commonDatatype;
    }

    public void setCommonDatatype(int commonDatatype) {
        this.commonDatatype = commonDatatype;
    }
}
