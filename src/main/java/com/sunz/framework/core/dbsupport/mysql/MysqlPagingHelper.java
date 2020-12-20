//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.mysql;

import com.sunz.framework.core.dbsupport.BasePagingHelper;

public class MysqlPagingHelper extends BasePagingHelper {
    public MysqlPagingHelper() {
    }

    public String getPagingSql(String sql, int start, int limit) {
        if (sql == null) {
            return null;
        } else {
            if (start < 0) {
                start = 0;
            }

            if (limit < 0 || start + limit < 0) {
                limit = 2147483647 - start;
            }

            return String.format(" %1$s LIMIT %2$s,%3$s ", sql, start, limit);
        }
    }
}
