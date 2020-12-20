//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.postgre;

import com.sunz.framework.core.dbsupport.BaseDbMetaHelper;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IProcedureHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostgreMetaHelper extends BaseDbMetaHelper implements IProcedureHelper {
    private static final String sql_table_columns = "select  \t\tta.attname \"name\", \t\t tt.typname \"datatype\", \t\t COALESCE(( \t\t \tinformation_schema._pg_char_max_length ( \t\t \t\tinformation_schema._pg_truetypid (ta.*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta.*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"length\", \t\t coalesce((information_schema._pg_numeric_precision( \t\t \t\tinformation_schema._pg_truetypid (ta .*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta .*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"precision\", \t\t coalesce(( \t\t \tinformation_schema._pg_numeric_scale ( \t\t \t\tinformation_schema._pg_truetypid (ta.*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta.*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"scale\", \t\t not ta.attnotnull \"nullable\", \t\t td.adsrc \"defaultValue\", \t\t tp.description \"comment\"  from pg_attribute ta   \t\tinner join pg_class tcls on tcls.oid=ta.attrelid  \t\tinner join pg_type tt on tt.oid=ta.atttypid  \t\tleft  join pg_attrdef td on td.adrelid=ta.attrelid and td.adnum=ta.attnum   \t\tleft  join pg_description tp on tp.objoid=ta.attrelid and tp.objsubid=ta.attnum  where ta.attnum>0 and ta.attisdropped = 'f' \t\tand tcls.relname=?";
    private static Map<String, Integer> dictType = null;

    public PostgreMetaHelper() {
        this.dataTypeMapping = dictType;
    }

    public void init() {
    }

    private String getObjectDeclare(String sql, String name) {
        if (sql != null && name != null) {
            Map<String, String> map = new HashMap();
            map.put("name", name);
            List<String> rs = this.jdbcTemplate.queryForList(sql, map, String.class);
            return rs.size() > 0 ? (String)rs.get(0) : null;
        } else {
            return null;
        }
    }

    public String getProcedureDeclare(String proceName) {
        return this.getFunctionDeclare(proceName);
    }

    public String getFunctionDeclare(String funName) {
        return this.getObjectDeclare("select pg_get_functiondef(oid) from pg_proc where proname=:name", funName);
    }

    public String getTriggerDeclare(String seqName) {
        return this.getObjectDeclare("select pg_get_triggerdef(oid) from  pg_trigger  where tgname=:name ", seqName);
    }

    public String getViewDeclare(String viewName) {
        return this.getObjectDeclare("select pg_get_viewdef(relfilenode) from pg_class where relname=:name", viewName);
    }

    public boolean exists(String objName) {
        if (objName == null) {
            return false;
        } else {
            Map map = new HashMap();
            map.put("name", objName);
            return (Boolean)this.jdbcTemplate.queryForObject("select coalesce((select 1 from information_schema.TABLES t where t.TABLE_NAME=:name ), (select 1 from pg_proc t where proname=:name), 0)", map, Boolean.class);
        }
    }

    public List getTableNames() {
        return this.jdbcTemplate.queryForList("select td.description \"COMMENTS\",tp.relname \"NAME\"   from pg_class  tp   inner join pg_namespace tn on tn.oid=tp.relnamespace   inner join pg_database tdb on tdb.datdba =tp.relowner and tdb.datname=current_database()   left join pg_description td on td.objoid= tp.oid and td.objsubid=0   where relkind='r'", (Map)null);
    }

    public List getViewNames() {
        return this.jdbcTemplate.queryForList("select tcls.relname \"NAME\",td.description  \"COMMENTS\" from pg_views tv  INNER JOIN pg_class tcls on tcls.relname =tv.viewname  left join pg_description td on td.objoid= tcls.oid ", (Map)null);
    }

    public List getProcedureNames() {
        return this.getFunctionNames();
    }

    public List getFunctionNames() {
        return this.jdbcTemplate.queryForList("select td.description \"COMMENTS\",tp.proname \"NAME\" from pg_proc tp   inner join pg_namespace tn on tn.oid=tp.pronamespace  inner join pg_database tdb on tdb.datdba =tp.proowner and tdb.datname=current_database()  left join pg_description td on td.objoid= tp.oid", (Map)null);
    }

    public List getTriggerNames() {
        return this.jdbcTemplate.queryForList("select td.description \"COMMENTS\",tt.tgname \"NAME\"  from pg_trigger tt   left join pg_description td on td.objoid= tt.oid  where tt.tgisinternal=false", (Map)null);
    }

    public List getTableColumnInfos(String tableName) {
        tableName = tableName.toLowerCase();
        if (!dictColumnInfos.containsKey(tableName)) {
            List<TableColumnInfo> listColumnInfo = this.jdbcTemplate.getJdbcOperations().query("select  \t\tta.attname \"name\", \t\t tt.typname \"datatype\", \t\t COALESCE(( \t\t \tinformation_schema._pg_char_max_length ( \t\t \t\tinformation_schema._pg_truetypid (ta.*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta.*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"length\", \t\t coalesce((information_schema._pg_numeric_precision( \t\t \t\tinformation_schema._pg_truetypid (ta .*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta .*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"precision\", \t\t coalesce(( \t\t \tinformation_schema._pg_numeric_scale ( \t\t \t\tinformation_schema._pg_truetypid (ta.*, tt.*), \t\t \t\tinformation_schema._pg_truetypmod (ta.*, tt.*) \t\t \t) \t\t )::information_schema.cardinal_number,-1) \"scale\", \t\t not ta.attnotnull \"nullable\", \t\t td.adsrc \"defaultValue\", \t\t tp.description \"comment\"  from pg_attribute ta   \t\tinner join pg_class tcls on tcls.oid=ta.attrelid  \t\tinner join pg_type tt on tt.oid=ta.atttypid  \t\tleft  join pg_attrdef td on td.adrelid=ta.attrelid and td.adnum=ta.attnum   \t\tleft  join pg_description tp on tp.objoid=ta.attrelid and tp.objsubid=ta.attnum  where ta.attnum>0 and ta.attisdropped = 'f' \t\tand tcls.relname=?", new Object[]{tableName}, this.tableColumnRowMapper);
            Iterator var3 = listColumnInfo.iterator();

            while(var3.hasNext()) {
                TableColumnInfo info = (TableColumnInfo)var3.next();
                info.setCommonDatatype(this.getCommonDatatype(info.getDatatype()));
            }

            dictColumnInfos.put(tableName, listColumnInfo);
        }

        return (List)dictColumnInfos.get(tableName);
    }

    public String getObjectStartQualifier() {
        return "\"";
    }

    public String getObjectEndQualifier() {
        return this.getObjectStartQualifier();
    }

    public List parseParameter(String procName, String procDeclare) {
        String uDeclare = procDeclare.toUpperCase();
        int start = uDeclare.indexOf("FUNCTION");
        int end = uDeclare.indexOf("RETURNS");
        String usefull = procDeclare.substring(start + 8, end);
        int from = usefull.indexOf("(");
        int to = usefull.lastIndexOf(")");
        usefull = usefull.substring(from + 1, to);
        String[] all = usefull.split(",");
        List<Object[]> result = new ArrayList();
        String[] var11 = all;
        int var12 = all.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            String p = var11[var13];
            if (p != null) {
                String[] pset = p.trim().split("\\s+");
                String temp = pset[0].toUpperCase();
                int nameIndex = 1;
                int typeIndex = 2;
                if (!temp.equals("IN") && !temp.equals("OUT") && !temp.equals("INOUT")) {
                    nameIndex = 0;
                    typeIndex = 1;
                }

                String name = pset[nameIndex];
                if (name.startsWith("\"")) {
                    name = name.substring(1, name.length() - 1);
                }

                String type = pset[typeIndex];
                int eqIndex;
                if (pset.length > typeIndex + 1) {
                    for(eqIndex = typeIndex + 1; eqIndex < pset.length; ++eqIndex) {
                        type = type + pset[eqIndex];
                    }
                }

                eqIndex = type.indexOf("=");
                String defaultValue = null;
                if (eqIndex > -1) {
                    defaultValue = type.substring(eqIndex + 1);
                    type = type.substring(0, eqIndex);
                }

                result.add(new Object[]{name, type, temp.equals("OUT") || temp.equals("INOUT"), defaultValue});
            }
        }

        return result;
    }

    static {
        dictType = new HashMap();
        dictType.put("int2vector", -2);
        dictType.put("oidvector", -2);
        dictType.put("regproc", 4);
        dictType.put("oid", 4);
        dictType.put("tid", 4);
        dictType.put("xid", 4);
        dictType.put("cid", 4);
        dictType.put("bool", 16);
        dictType.put("bytea", -2);
        dictType.put("char", 1);
        dictType.put("name", 1);
        dictType.put("int8", -5);
        dictType.put("int2", 5);
        dictType.put("int4", 4);
        dictType.put("text", -1);
        dictType.put("json", -1);
        dictType.put("xml", -1);
        dictType.put("point", -2);
        dictType.put("path", 12);
        dictType.put("box", -2);
        dictType.put("polygon", -2);
        dictType.put("line", -2);
        dictType.put("float4", 6);
        dictType.put("float8", 3);
        dictType.put("abstime", 92);
        dictType.put("reltime", 92);
        dictType.put("unknown", 1111);
        dictType.put("circle", -2);
        dictType.put("money", 3);
        dictType.put("macaddr", 12);
        dictType.put("inet", 12);
        dictType.put("bpchar", 12);
        dictType.put("varchar", 12);
        dictType.put("date", 91);
        dictType.put("time", 92);
        dictType.put("timestamp", 93);
        dictType.put("timestamptz", 93);
        dictType.put("timetz", 92);
        dictType.put("bit", -7);
        dictType.put("varbit", -7);
        dictType.put("numeric", 2);
    }
}
