//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.oracle;

import com.sunz.framework.core.dbsupport.BaseDbMetaHelper;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IProcedureHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OracleMetaHelper extends BaseDbMetaHelper implements IProcedureHelper {
    private final String Type_Name_Procedure;
    private final String Type_Name_Function;
    private final String Type_Name_Trigger;
    private final String Type_Name_View;
    private static final String sql_table_columns = "select ttc.comments \"tablecomment\",        tc.COLUMN_NAME \"name\",\t\ttc.DATA_TYPE \"datatype\",\t\ttc.DATA_LENGTH \"length\",\t\tnvl(tc.DATA_PRECISION,-1) \"precision\",\t\tnvl(tc.DATA_SCALE,-1) \"scale\", \t\t(case tc.NULLABLE when 'Y' then 1 else 0 end) \"nullable\",\t\ttc.DATA_DEFAULT \"defaultValue\"      ,tcc.comments \"comment\"from user_tab_comments ttc left join user_tab_columns tc on tc.TABLE_NAME=ttc.table_name left join user_col_comments tcc on tcc.table_name=tc.TABLE_NAME and tcc.column_name=tc.COLUMN_NAME where ttc.table_name=?";
    private static Map<String, Integer> dictType = null;

    public OracleMetaHelper() {
        this.dataTypeMapping = dictType;
        this.Type_Name_Procedure = "PROCEDURE";
        this.Type_Name_Function = "FUNCTION";
        this.Type_Name_Trigger = "TRIGGER";
        this.Type_Name_View = "VIEW";
    }

    private String getObjectDeclare(String type, String name) {
        if (type != null && name != null) {
            Map<String, String> map = new HashMap();
            map.put("type", type);
            map.put("name", name);
            return (String)this.jdbcTemplate.queryForObject(" select (select dbms_metadata.GET_DDL(:type, :name)  from user_objects t where t.OBJECT_NAME = :name and t.OBJECT_TYPE = :type) from dual ", map, String.class);
        } else {
            return null;
        }
    }

    public String getProcedureDeclare(String proceName) {
        return this.getObjectDeclare("PROCEDURE", proceName);
    }

    public String getFunctionDeclare(String funName) {
        return this.getObjectDeclare("FUNCTION", funName);
    }

    public String getTriggerDeclare(String seqName) {
        return this.getObjectDeclare("TRIGGER", seqName);
    }

    public String getViewDeclare(String viewName) {
        return this.getObjectDeclare("VIEW", viewName);
    }

    public boolean exists(String objName) {
        if (objName == null) {
            return false;
        } else {
            objName = objName.toUpperCase();
            String sql = "select nvl((select 1 from user_objects where object_name=:obj),0) from dual";
            Map map = new HashMap();
            map.put("obj", objName);
            return (Boolean)this.jdbcTemplate.queryForObject(sql, map, Boolean.class);
        }
    }

    public List getTableNames() {
        return this.jdbcTemplate.queryForList("select tt.table_name name,ttc.comments from user_all_tables tt left join user_tab_comments ttc on ttc.table_name=tt.table_name", (Map)null);
    }

    public List getViewNames() {
        return this.jdbcTemplate.queryForList("select tv.VIEW_NAME name,ttc.comments from user_views tv left join user_tab_comments ttc on ttc.table_name=tv.VIEW_NAME", (Map)null);
    }

    public List getProcedureNames() {
        return this.jdbcTemplate.queryForList("select tp.object_name name,'' comments from user_procedures tp where tp.object_type='PROCEDURE'", (Map)null);
    }

    public List getFunctionNames() {
        return this.jdbcTemplate.queryForList("select tp.object_name name,'' comments from user_procedures tp where tp.object_type='FUNCTION'", (Map)null);
    }

    public List getTriggerNames() {
        return this.jdbcTemplate.queryForList("select * from user_triggers", (Map)null);
    }

    public List getTableColumnInfos(String tableName) {
        tableName = tableName.toUpperCase();
        if (!dictColumnInfos.containsKey(tableName)) {
            List<TableColumnInfo> listColumnInfo = this.jdbcTemplate.getJdbcOperations().query("select ttc.comments \"tablecomment\",        tc.COLUMN_NAME \"name\",\t\ttc.DATA_TYPE \"datatype\",\t\ttc.DATA_LENGTH \"length\",\t\tnvl(tc.DATA_PRECISION,-1) \"precision\",\t\tnvl(tc.DATA_SCALE,-1) \"scale\", \t\t(case tc.NULLABLE when 'Y' then 1 else 0 end) \"nullable\",\t\ttc.DATA_DEFAULT \"defaultValue\"      ,tcc.comments \"comment\"from user_tab_comments ttc left join user_tab_columns tc on tc.TABLE_NAME=ttc.table_name left join user_col_comments tcc on tcc.table_name=tc.TABLE_NAME and tcc.column_name=tc.COLUMN_NAME where ttc.table_name=?", new Object[]{tableName}, this.tableColumnRowMapper);
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
        return "";
    }

    public String getObjectEndQualifier() {
        return this.getObjectStartQualifier();
    }

    public List parseParameter(String procName, String procDeclare) {
        String uDeclare = procDeclare.toUpperCase();
        String usefull = "";
        char bracketS = 40;
        char bracketE = 41;
        int from = procDeclare.indexOf(bracketS) + 1;
        int bcount = 0;
        int end = uDeclare.length();

        for(int i = from; i < end; ++i) {
            char c = procDeclare.charAt(i);
            if (c == bracketS) {
                ++bcount;
            } else if (c == bracketE) {
                if (bcount == 0) {
                    break;
                }

                --bcount;
            }

            usefull = usefull + c;
        }

        String[] all = usefull.split(",");
        List<Object[]> result = new ArrayList();
        String[] var12 = all;
        int var13 = all.length;

        for(int var14 = 0; var14 < var13; ++var14) {
            String p = var12[var14];
            if (p != null) {
                String[] pset = p.trim().split("\\s+");
                String temp = pset[1].toUpperCase();
                int nameIndex = 0;
                int typeIndex = 2;
                if (!temp.equals("IN") && !temp.equals("OUT")) {
                    nameIndex = 0;
                    typeIndex = 1;
                }

                String type = pset[typeIndex];
                if (pset.length > typeIndex + 1) {
                    for(int i = typeIndex + 1; i < pset.length; ++i) {
                        type = type + pset[i];
                    }
                }

                result.add(new Object[]{pset[nameIndex], type, temp.equals("OUT")});
            }
        }

        return result;
    }

    static {
        dictType = new HashMap();
        dictType.put("VARCHAR", 12);
        dictType.put("VARCHAR2", 12);
        dictType.put("NVARCHAR", 12);
        dictType.put("NVARCHAR2", 12);
        dictType.put("CHAR", 1);
        dictType.put("NCHAR", -15);
        dictType.put("BLOB", 2004);
        dictType.put("CLOB", 2005);
        dictType.put("NCLOB", 2005);
        dictType.put("DATE", 93);
        dictType.put("DECIMAL", 3);
        dictType.put("DOUBLE", 8);
        dictType.put("DOUBLE PRECISION", 8);
        dictType.put("FLOAT", 6);
        dictType.put("INTEGER", 4);
        dictType.put("NUMBER", 2);
        dictType.put("NUMERIC", 2);
        dictType.put("OID", -8);
        dictType.put("RAW", -8);
        dictType.put("UROWID", -8);
        dictType.put("REAL", 7);
        dictType.put("SMALLINT", 5);
        dictType.put("TIME", 92);
        dictType.put("TIME WITH TZ", 92);
        dictType.put("TIMESTAMP", 93);
        dictType.put("TIMESTAMP WITH LOCAL TZ", 93);
        dictType.put("TIMESTAMP WITH TZ", 93);
        dictType.put("VARYING ARRAY", -3);
    }
}
