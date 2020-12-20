//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_SqlStatement"
)
public class SqlStatement extends com.sunz.framework.core.Entity {
    private String key;
    private String description;
    private String sql;
    private String sql_group;

    public SqlStatement() {
    }

    public String getKey() {
        return this.key;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSql() {
        return this.sql;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql_group() {
        return this.sql_group;
    }

    public void setSql_group(String sql_group) {
        this.sql_group = sql_group;
    }
}
