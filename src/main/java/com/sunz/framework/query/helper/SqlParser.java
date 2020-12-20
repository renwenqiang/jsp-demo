//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query.helper;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.util.IDbMetaHelper;
import freemarker.core.StopException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlParser implements ICacheRefreshable {
    private static final Logger logger = Logger.getLogger(SqlParser.class);
    private static Configuration config = new Configuration();
    private static Map<String, Template> templateCache = new HashMap();
    private static NamedParameterJdbcTemplate jdbcTemplate;
    private static IDbMetaHelper dbMetaHelper;
    private static Pattern patternInclude = Pattern.compile("@(\\w+)");
    private static List<String> includeList = new ArrayList();
    private static Map<String, List<String>> dependenceMap = new HashMap();
    private static Map<String, String> sqlCache = new HashMap();
    private static String sql_Meta = "select * from T_S_SqlStatement t";
    private static ResultSetExtractor convertor = new ResultSetExtractor() {
        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            HashMap map = new HashMap();

            while(rs.next()) {
                String id = rs.getString("id");
                String key = rs.getString("key");
                String value = rs.getString("sql");
                if (value != null) {
                    if (value.indexOf("@") > -1) {
                        SqlParser.includeList.add(id);
                        SqlParser.includeList.add(key);
                    }

                    Matcher m = SqlParser.patternArray.matcher(value);
                    int lastPosition = 0;
                    String sb = "";

                    ArrayList arrKeys;
                    for(arrKeys = new ArrayList(); m.find(); lastPosition = m.end()) {
                        String placeHolder = m.group(1) == null ? m.group(2) : m.group(1);
                        sb = sb + value.substring(lastPosition, m.start()) + ":" + placeHolder;
                        arrKeys.add(placeHolder);
                    }

                    if (arrKeys.size() > 1) {
                        String[] arr = new String[arrKeys.size()];
                        arrKeys.toArray(arr);
                        SqlParser.dictArray.put(id, arr);
                        SqlParser.dictArray.put(key, arr);
                    } else {
                        SqlParser.dictArray.remove(id);
                        SqlParser.dictArray.remove(key);
                    }

                    value = sb + value.substring(lastPosition);
                }

                map.put(id, value);
                map.put(key, value);
            }

            Map<String, String[]> extArray = new HashMap();
            Iterator var13 = SqlParser.dictArray.entrySet().iterator();

            while(var13.hasNext()) {
                Entry<String, String[]> entry = (Entry)var13.next();
                SqlParser.resolveArrayInclude(extArray, (String)entry.getKey(), (String[])entry.getValue());
            }

            SqlParser.dictArray.putAll(extArray);
            return map;
        }
    };
    private static Map<String, String[]> dictArray = new HashMap();
    private static Pattern patternArray = Pattern.compile(":\\[\\]\\s*(\\w+)|:\\s*(\\w+)\\[\\]");
    private static Pattern patternDym = Pattern.compile(":\\s*(\\w+)");
    private static Pattern patternStatic = Pattern.compile("[#$]\\{\\s*(\\w+)\\s*\\}");

    public SqlParser() {
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate template) {
        jdbcTemplate = template;
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        SqlParser.dbMetaHelper = dbMetaHelper;
    }

    @PostConstruct
    public void init() {
        initSqlStatement();
    }

    private static String resolveInclude(String sql, String k) {
        if (sql != null && !"".equals(sql)) {
            if (!includeList.contains(k)) {
                return sql;
            } else {
                StringBuffer sb = new StringBuffer();
                Matcher m = patternInclude.matcher(sql);
                int lastPosition = 0;

                String subKey;
                while(m.find()) {
                    subKey = m.group(1);
                    List<String> relyList = (List)dependenceMap.get(subKey);
                    if (relyList == null) {
                        relyList = new ArrayList();
                        dependenceMap.put(subKey, relyList);
                    }

                    if (!((List)relyList).contains(k)) {
                        ((List)relyList).add(k);
                    }

                    sb.append(sql.substring(lastPosition, m.start()));
                    lastPosition = m.end();
                    sb.append(getSql((String)null, subKey));
                }

                sb.append(sql.substring(lastPosition));
                subKey = sb.toString();
                sqlCache.put(k, subKey);
                includeList.remove(k);
                return subKey;
            }
        } else {
            return null;
        }
    }

    private static void resolveArrayInclude(Map<String, String[]> extArray, String k, String[] value) {
        if (dependenceMap.containsKey(k)) {
            Iterator var3 = ((List)dependenceMap.get(k)).iterator();

            while(var3.hasNext()) {
                String pk = (String)var3.next();
                extArray.put(pk, value);
                resolveArrayInclude(extArray, pk, value);
            }
        }

    }

    public static void supportInArray(Map mapParam, String key) {
        if (mapParam != null) {
            String[] fields = null;
            Object oFields = mapParam.get("listFields");
            if (oFields != null) {
                if (oFields instanceof String[]) {
                    fields = (String[])((String[])oFields);
                } else if (oFields instanceof String) {
                    fields = ((String)oFields).split(",");
                }
            }

            String[] inner;
            int outLen;
            if (dictArray.containsKey(key)) {
                inner = (String[])dictArray.get(key);
                if (fields == null) {
                    fields = inner;
                } else {
                    outLen = fields.length;
                    fields = (String[])Arrays.copyOf(fields, fields.length + inner.length);
                    System.arraycopy(inner, 0, fields, outLen, inner.length);
                }
            }

            if (fields != null && fields.length > 0) {
                inner = fields;
                outLen = fields.length;

                for(int var6 = 0; var6 < outLen; ++var6) {
                    String f = inner[var6];
                    Object ov = mapParam.get(f);
                    if (ov != null && !(ov instanceof Collection)) {
                        List<Object> objects = Arrays.asList((Object[])(ov instanceof Object[] ? (Object[])((Object[])ov) : (ov instanceof String ? ((String)ov).split(",") : new Object[]{ov})));
                        mapParam.put(f, objects);
                    }
                }
            }

        }
    }

    public static void register(String sqlkey, String freeMarkerSql) {
        if (!sqlCache.containsKey(sqlkey)) {
            if (freeMarkerSql != null) {
                sqlCache.put(sqlkey, freeMarkerSql);
                if (freeMarkerSql.indexOf("@") > -1) {
                    includeList.add(sqlkey);
                }

            }
        }
    }

    public static void initSqlStatement() {
        sqlCache.putAll((Map)jdbcTemplate.query(sql_Meta, convertor));
    }

    public static String getSql(String sqlid, String sqlkey) {
        String key = sqlkey != null && !"".equals(sqlkey) ? sqlkey : sqlid;
        if (key != null && !"".equals(key)) {
            String sql = sqlkey == null ? null : (String)sqlCache.get(sqlkey);
            if (sql == null && sqlid != null) {
                sql = (String)sqlCache.get(sqlid);
            }

            if (sql == null) {
                String sqlPost = " where t.id=? or t." + dbMetaHelper.getObjectStartQualifier() + "key" + dbMetaHelper.getObjectEndQualifier() + "=?";
                Map<String, String> map = (Map)jdbcTemplate.getJdbcOperations().query(sql_Meta + sqlPost, new Object[]{sqlid, sqlkey}, convertor);
                sqlCache.putAll(map);
                sql = (String)map.get(key);
            }

            return resolveInclude(sql, key);
        } else {
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public static String parseSql(String cacheKey, String freemarkerSql, Map param) {
        return parseFreemarkerSql(freemarkerSql, param, cacheKey);
    }

    public static String parseFreemarkerSql(String freemarkerSql, Map param, String cacheKey) {
        if (freemarkerSql != null && !"".equals(freemarkerSql)) {
            if (cacheKey == null) {
                cacheKey = freemarkerSql;
            }

            try {
                Template fltTemplate = (Template)templateCache.get(cacheKey);
                if (fltTemplate == null) {
                    fltTemplate = new Template(cacheKey, new StringReader(freemarkerSql), config, "utf-8");
                    templateCache.put(cacheKey, fltTemplate);
                }

                Writer writer = new StringWriter();
                fltTemplate.process(param, writer);
                supportInArray(param, cacheKey);
                return writer.toString();
            } catch (StopException var5) {
                throw new RuntimeException(var5.getMessageWithoutStackTop());
            } catch (Exception var6) {
                logger.error("解析sql模板出错\r\n" + var6.getMessage());
                throw new RuntimeException("sql模板解析出错");
            }
        } else {
            return null;
        }
    }

    public static Map getParam(String sql, Map mRaw) {
        Map<String, Object> map = new HashMap();
        Matcher m = patternDym.matcher(sql);

        while(m.find()) {
            String ognl_key = m.group(1);

            try {
                map.put(ognl_key, Ognl.getValue(ognl_key, mRaw));
            } catch (OgnlException var7) {
                String err = "解析sql动态参数出错\r\n" + var7.getMessage();
                logger.error(err);
                throw new RuntimeException(err);
            }
        }

        return map;
    }

    public static List<String> parseForSearch(String sql) {
        List<String> results = new ArrayList();
        Matcher m = patternDym.matcher(sql);

        while(m.find()) {
            results.add(m.group(1));
        }

        m = patternStatic.matcher(sql);

        while(m.find()) {
            String mg = m.group(1);
            results.add(mg);
        }

        return results;
    }

    public static void refreshItem(String key) {
        templateCache.remove(key);
        sqlCache.remove(key);
        List<String> relyList = (List)dependenceMap.get(key);
        if (relyList != null) {
            Iterator var2 = relyList.iterator();

            while(var2.hasNext()) {
                String relyKey = (String)var2.next();
                refreshItem(relyKey);
            }
        }

    }

    public static void refreshAll() {
        templateCache.clear();
        initSqlStatement();
    }

    public void refresh(String item) {
        if (item != null) {
            refreshItem(item);
        } else {
            refreshAll();
        }

    }

    public String getCategory() {
        return "SqlStatement";
    }

    public String getDescription() {
        return "Sql语句定义";
    }
}
