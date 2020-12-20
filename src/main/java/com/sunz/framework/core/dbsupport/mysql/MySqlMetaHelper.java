//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.mysql;

import com.sunz.framework.core.dbsupport.BaseDbMetaHelper;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IProcedureHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MySqlMetaHelper extends BaseDbMetaHelper implements IProcedureHelper {
    private String loginDatabase;
    private static final String sql_table_columns = "select  t.COLUMN_NAME \"name\", t.DATA_TYPE \"datatype\", IFNULL(t.CHARACTER_MAXIMUM_LENGTH,-1) \"length\", IFNULL(t.NUMERIC_PRECISION,-1) \"precision\", IFNULL(t.NUMERIC_SCALE,-1) \"scale\", t.IS_NULLABLE \"nullable\", t.COLUMN_DEFAULT \"defaultValue\", t.COLUMN_COMMENT \"comment\" from information_schema.COLUMNS t where t.TABLE_SCHEMA=? and t.TABLE_NAME=?";
    private static Map<String, Integer> dictType = null;

    public MySqlMetaHelper() {
        this.dataTypeMapping = dictType;
        this.loginDatabase = null;
    }

    public void init() {
        this.loginDatabase = (String)this.jdbcTemplate.queryForObject("select database()", (Map)null, String.class);
    }

    private String getObjectDeclare(String sql, String name) {
        if (sql != null && name != null) {
            Map<String, String> map = new HashMap();
            map.put("name", name);
            map.put("database", this.loginDatabase);
            List<String> rs = this.jdbcTemplate.queryForList(sql, map, String.class);
            return rs.size() > 0 ? (String)rs.get(0) : null;
        } else {
            return null;
        }
    }

    public String getProcedureDeclare(String proceName) {
        return this.getObjectDeclare("select CONCAT('Create Procedure ',t.name,'(',t.param_list,')',char(10),t.body) from mysql.proc t where t.db=:database and t.name=:name", proceName);
    }

    public String getFunctionDeclare(String funName) {
        return this.getObjectDeclare("select CONCAT('Create Function ',t.name,'(',t.param_list,') RETURNS',t.returns,char(10),t.body) from mysql.proc t where t.db=:database and t.name=:name", funName);
    }

    public String getTriggerDeclare(String seqName) {
        return this.getObjectDeclare("select CONCAT('CREATE TRIGGER ',t.TRIGGER_NAME,' ', t.ACTION_TIMING,' ', t.EVENT_MANIPULATION,' ON ', t.EVENT_OBJECT_TABLE,' FOR EACH ' , t.ACTION_ORIENTATION,char(10), t.ACTION_STATEMENT ) from information_schema.TRIGGERS t  WHERE T.GRIGGER_SCHEMA=:database and T.TRIGGER_NAME=:name", seqName);
    }

    public String getViewDeclare(String viewName) {
        return this.getObjectDeclare("select t.TABLE_NAME from INFORMATION_SCHEMA.Views t where t.TABLE_SCHEMA=:database", viewName);
    }

    public boolean exists(String objName) {
        if (objName == null) {
            return false;
        } else {
            Map map = new HashMap();
            map.put("name", objName);
            map.put("database", this.loginDatabase);
            return (Boolean)this.jdbcTemplate.queryForObject("select ifnull((select 1 from information_schema.TABLES t where t.TABLE_SCHEMA=:database and t.TABLE_NAME=:name ), ifnull((select 1 from mysql.proc t where t.db=:database and t.name=:name), 0))", map, Boolean.class);
        }
    }

    private List getList(String sql) {
        Map map = new HashMap();
        map.put("database", this.loginDatabase);
        return this.jdbcTemplate.queryForList(sql, map);
    }

    public List getTableNames() {
        return this.getList("select t.TABLE_NAME NAME,t.TABLE_COMMENT COMMENTS  from INFORMATION_SCHEMA.Tables t where t.TABLE_SCHEMA=:database");
    }

    public List getViewNames() {
        return this.getList("select t.TABLE_NAME NAME,'' COMMENTS  from INFORMATION_SCHEMA.Views t where t.TABLE_SCHEMA=:database");
    }

    public List getProcedureNames() {
        return this.getList("select t.NAME from mysql.proc t where t.db=:database");
    }

    public List getFunctionNames() {
        return this.getList("select t.NAME from mysql.func t where t.db=:database");
    }

    public List getTriggerNames() {
        return this.getList("select * from INFORMATION_SCHEMA.TRIGGERS t WHERE t.GRIGGER_SCHEMA=:database");
    }

    public List getTableColumnInfos(String tableName) {
        if (!dictColumnInfos.containsKey(tableName)) {
            List<TableColumnInfo> listColumnInfo = this.jdbcTemplate.getJdbcOperations().query("select  t.COLUMN_NAME \"name\", t.DATA_TYPE \"datatype\", IFNULL(t.CHARACTER_MAXIMUM_LENGTH,-1) \"length\", IFNULL(t.NUMERIC_PRECISION,-1) \"precision\", IFNULL(t.NUMERIC_SCALE,-1) \"scale\", t.IS_NULLABLE \"nullable\", t.COLUMN_DEFAULT \"defaultValue\", t.COLUMN_COMMENT \"comment\" from information_schema.COLUMNS t where t.TABLE_SCHEMA=? and t.TABLE_NAME=?", new Object[]{this.loginDatabase, tableName}, this.tableColumnRowMapper);
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
        return "`";
    }

    public String getObjectEndQualifier() {
        return this.getObjectStartQualifier();
    }

    public List parseParameter(String procName, String procDeclare) {
        String uDeclare = procDeclare.toUpperCase();
        int start = uDeclare.indexOf("PROCEDURE");
        int end = uDeclare.indexOf("BEGIN");
        String usefull = procDeclare.substring(start + 9, end);
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

                String type = pset[typeIndex];
                if (pset.length > typeIndex + 1) {
                    for(int i = typeIndex + 1; i < pset.length; ++i) {
                        type = type + pset[i];
                    }
                }

                String name = pset[nameIndex];
                if (name.startsWith("`")) {
                    name = name.substring(1, name.length() - 2);
                }

                result.add(new Object[]{name, type, temp.equals("OUT") || temp.equals("INOUT")});
            }
        }

        return result;
    }

    static {
        dictType = new HashMap();
        dictType.put("char", 1);
        dictType.put("varchar", 12);
        dictType.put("datetime", 93);
        dictType.put("bigint", -5);
        dictType.put("int", 4);
        dictType.put("integer", 4);
        dictType.put("mediumint", 4);
        dictType.put("bit", -7);
        dictType.put("real", 7);
        dictType.put("numeric", 2);
        dictType.put("decimal", 3);
        dictType.put("timestamp", 93);
        dictType.put("longblob", -4);
        dictType.put("tinyint", -6);
        dictType.put("double", 8);
        dictType.put("tinyblob", -2);
        dictType.put("blob", 2004);
        dictType.put("date", 91);
        dictType.put("smallint", 5);
        dictType.put("time", 92);
        dictType.put("float", 6);
        dictType.put("mediumtext", -16);
        dictType.put("binary", -2);
        dictType.put("varbinary", -3);
        dictType.put("text", 12);
        dictType.put("longtext", 12);
        dictType.put("year", 4);
        dictType.put("set", 12);
        dictType.put("enum", 4);
    }
}
