//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.sqlserver;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.dbsupport.BasePagingHelper;
import java.util.HashMap;
import java.util.Map;

public class SqlserverPagingHelper extends BasePagingHelper implements ICacheRefreshable {
    private static Map<String, String> dictCache = new HashMap();

    public SqlserverPagingHelper() {
    }

    public String getPagingSql(String sql, int start, int limit) {
        if (sql == null) {
            return null;
        } else {
            String lsql = sql.toLowerCase();
            int fromIndex = lsql.indexOf("select") + 6;
            sql = sql.substring(fromIndex).trim();
            if (sql.substring(0, 3).equalsIgnoreCase("top")) {
                return "select " + sql;
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (limit < 0 || start + limit < 0) {
                    limit = 2147483647 - start;
                }

                String distinct = "";
                if (sql.substring(0, 8).equalsIgnoreCase("distinct")) {
                    sql = sql.substring(9);
                    distinct = "distinct";
                }

                return String.format("select * from (select row_number() over(order by rn_) rn,t_.* \t \t from (select %4$s top %3$s rn_=1,%1$s) t_  ) t__  where rn > %2$s", sql, start, start + limit, distinct);
            }
        }
    }

    public void refresh(String item) {
        if (item != null && !"".equals(item)) {
            dictCache.remove(item);
        } else {
            dictCache.clear();
        }

    }

    public String getCategory() {
        return "sqlserverPaging";
    }

    public String getDescription() {
        return "分页处理缓存,Sqlserver分页处理过于复杂，进行了缓存";
    }
}
