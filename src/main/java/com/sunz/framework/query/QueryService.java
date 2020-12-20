//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query;

import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.core.util.IPagingQueryService;
import com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher;
import com.sunz.framework.query.entity.Operation;
import com.sunz.framework.query.entity.Query;
import com.sunz.framework.query.entity.ResultField;
import com.sunz.framework.query.entity.SearchField;
import com.sunz.framework.query.helper.SqlParser;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryService implements IQueryService {
    @Autowired
    IPagingQueryService pagingQueryService;
    NamedParameterJdbcTemplate jdbcTemplate;
    private IDbMetaHelper dbMetaHelper;
    private static final String sqlBase = "select t.* from ${tablename} t where t.queryid=:queryid ";
    private static final String sqlWhite;
    private static final String sqlBlack;
    private static final int RestrictMode_Black = 0;

    public QueryService() {
    }

    public void setPagingQueryService(IPagingQueryService pagingQueryService) {
        this.pagingQueryService = pagingQueryService;
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSql(String sqlStatementid, String sqlkey) {
        if (sqlkey != null && !"".equals(sqlkey) || sqlStatementid != null && !"".equals(sqlStatementid)) {
            String sql = SqlParser.getSql(sqlStatementid, sqlkey);
            if (sql != null && !"".equals(sql)) {
                return sql;
            } else {
                throw new RuntimeException("内部错误：查询【" + (sqlkey != null && !"".equals(sqlkey) ? sqlkey : sqlStatementid) + "】未定义");
            }
        } else {
            throw new RuntimeException("内部错误：未指定查询");
        }
    }

    public ListJsonResult queryList(String freemarkerSql, Map mapParam, PageParameter page, Class cls, String sqlkey) {
        String sql = SqlParser.parseFreemarkerSql(freemarkerSql, mapParam, sqlkey);

        try {
            return this.pagingQueryService.query(sql, mapParam, page, cls);
        } catch (Exception var8) {
            throw new RuntimeException("内部错误：非正确的查询定义【" + sqlkey + "】", var8);
        }
    }

    public <T> T queryObject(String freemarkerSql, Map mapParam, Class<T> cls, String sqlkey) {
        String sql = SqlParser.parseFreemarkerSql(freemarkerSql, mapParam, sqlkey);
        if (cls != null && !Map.class.equals(cls)) {
            try {
//                return this.jdbcTemplate.queryForObject(sql, mapParam, JdbcTemplateRowMapperDispatcher.getRowMapper(cls)); // TODO 原
                return this.jdbcTemplate.queryForObject(sql, (Map<String, ?>) mapParam, JdbcTemplateRowMapperDispatcher.getRowMapper(cls));// TODO 改
            } catch (Exception var7) {
                throw new RuntimeException("内部错误：当前查询没有返回“有且仅有一行数据”,key=" + sqlkey, var7);
            }
        } else {
//            return this.jdbcTemplate.queryForMap(sql, mapParam);// TODO 原
            return (T) this.jdbcTemplate.queryForMap(sql, mapParam); // TODO 改
        }
    }

    public int queryCount(String freemarkerSql, Map mapParam, String sqlkey) {
        String sql = SqlParser.parseFreemarkerSql(freemarkerSql, mapParam, sqlkey);

        try {
            return this.pagingQueryService.queryCount(sql, mapParam);
        } catch (Exception var6) {
            throw new RuntimeException("内部错误：非正确的查询定义【" + sqlkey + "】", var6);
        }
    }

    public int queryCount(String sqlStatementid, String sqlkey, Map mapParam) {
        String sql = this.getSql(sqlStatementid, sqlkey);
        return this.queryCount(sql, mapParam, sqlkey == null ? sqlStatementid : sqlkey);
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        this.dbMetaHelper = dbMetaHelper;
    }

    public Query getUserQuery(String key, TSUser user) {
        Map mapParam = new HashMap();
        mapParam.put("key", key);
        Query qDef = null;

        try {
            qDef = (Query)this.queryObject("select * from T_S_QUERY t where t." + this.dbMetaHelper.getObjectStartQualifier() + "key" + this.dbMetaHelper.getObjectEndQualifier() + "=:key", (Map)mapParam, (Class)Query.class, (String)"_query_uniqued");
        } catch (Exception var9) {
            throw new RuntimeException("指定的查询【" + key + "】不存在");
        }

        if (qDef != null) {
            mapParam.clear();
            mapParam.put("queryid", qDef.getId());
            mapParam.put("userid", user.getId());
            mapParam.put("username", user.getUserName());
            List<TSRole> roles = user.getRoles();
            String roleString = "";

            TSRole role;
            for(Iterator var7 = roles.iterator(); var7.hasNext(); roleString = roleString + "'" + role.getRoleCode() + "',") {
                role = (TSRole)var7.next();
                roleString = roleString + "'" + role.getId() + "',";
            }

            if (roleString.length() > 0) {
                roleString = roleString.substring(0, roleString.length() - 1);
            } else {
                roleString = "''";
            }

            mapParam.put("userroles", roleString);
            mapParam.put("tablename", "T_S_QUERY_SEARCHFIELD");
            qDef.setSearchFields(this.queryList(0 == qDef.getSFRestrictMode() ? sqlBlack : sqlWhite, (Map)mapParam, (PageParameter)(new PageParameter()), (Class)SearchField.class, (String)("_query_searchField_" + (0 == qDef.getSFRestrictMode() ? "_black" : "_white"))).list());
            mapParam.put("tablename", "T_S_QUERY_RESULTFIELD");
            qDef.setResultFields(this.queryList(0 == qDef.getRFRestrictMode() ? sqlBlack : sqlWhite, (Map)mapParam, (PageParameter)(new PageParameter()), (Class)ResultField.class, (String)("_query_resultField_" + (0 == qDef.getRFRestrictMode() ? "_black" : "_white"))).list());
            mapParam.put("tablename", "T_S_QUERY_OPERATION");
            qDef.setOperations(this.queryList(0 == qDef.getOperationRestrictMode() ? sqlBlack : sqlWhite, (Map)mapParam, (PageParameter)(new PageParameter()), (Class)Operation.class, (String)("_query_operation_" + (0 == qDef.getOperationRestrictMode() ? "_black" : "_white"))).list());
        }

        return qDef;
    }

    public int update(String sqlStatementid, String sqlkey, Map mapParam) {
        String freemarkerSql = this.getSql(sqlStatementid, sqlkey);
        sqlkey = sqlStatementid != null && !"".equals(sqlStatementid) ? sqlStatementid : sqlkey;
        String sql = SqlParser.parseFreemarkerSql(freemarkerSql, mapParam, sqlkey);

        try {
            return this.jdbcTemplate.update(sql, mapParam);
        } catch (Exception var7) {
            throw new RuntimeException("内部错误：非正确的sql语句", var7);
        }
    }

    public ListJsonResult queryList(String sqlStatementid, String sqlkey, Map mapParam, PageParameter page, Class cls) {
        return this.queryList(this.getSql(sqlStatementid, sqlkey), mapParam, page, cls, sqlkey == null ? sqlStatementid : sqlkey);
    }

    public ListJsonResult queryList(String sqlStatementid, String sqlkey, Map mapParam, PageParameter page) {
        return this.queryList(sqlStatementid, sqlkey, mapParam, page, Map.class);
    }

    public Map queryObject(String sqlStatementid, String sqlkey, Map mapParam) {
        return (Map)this.queryObject(sqlStatementid, sqlkey, mapParam, Map.class);
    }

    public <T> T queryObject(String sqlStatementid, String sqlkey, Map mapParam, Class<T> cls) {
        return this.queryObject(this.getSql(sqlStatementid, sqlkey), mapParam, cls, sqlkey == null ? sqlStatementid : sqlkey);
    }

    static {
        String sqlBase = "select t.* from ${tablename} t where t.queryid=:queryid ";
        String sqlWhere = " (select tr.RESOURCEID from T_S_QUERY_RESOURCERESTRICT tr where ((tr.TARGETTYPE='User' and (tr.TARGET=:username or tr.TARGET=:userid)) or (tr.TARGETTYPE='Role' and tr.TARGET in (${userroles}))))";
        sqlWhite = sqlBase + " and t.id in " + sqlWhere;
        sqlBlack = sqlBase + " and t.id not in " + sqlWhere;
    }
}
