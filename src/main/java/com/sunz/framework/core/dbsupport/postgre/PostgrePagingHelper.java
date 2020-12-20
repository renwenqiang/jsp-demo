//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.postgre;

import com.sunz.framework.core.dbsupport.BasePagingHelper;

public class PostgrePagingHelper extends BasePagingHelper {
    public PostgrePagingHelper() {
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

            return String.format(" %1s OFFSET %2s LIMIT %3s", sql, start, limit);
        }
    }
}
