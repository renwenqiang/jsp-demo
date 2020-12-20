//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable;

import java.util.List;
import org.springframework.jdbc.core.SqlParameterValue;

public class PrepareStatementInfo {
    String sql;
    List<SqlParameterValue> parameters;

    public PrepareStatementInfo() {
    }

    public PrepareStatementInfo(String sql, List<SqlParameterValue> params) {
        this.sql = sql;
        this.parameters = params;
    }

    public String getSql() {
        return this.sql;
    }

    public List<SqlParameterValue> getParameters() {
        return this.parameters;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setParameters(List<SqlParameterValue> parameters) {
        this.parameters = parameters;
    }
}
