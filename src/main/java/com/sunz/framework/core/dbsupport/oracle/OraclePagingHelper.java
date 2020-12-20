//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.oracle;

import com.sunz.framework.core.dbsupport.BasePagingHelper;

public class OraclePagingHelper extends BasePagingHelper {
    public OraclePagingHelper() {
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

            return String.format("select * from ( select t_.*,rownum rn from ( %1$s ) t_ where rownum <= %3$s ) where  rn> %2$s", sql, start, start + limit);
        }
    }
}
