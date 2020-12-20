//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport.sqlserver;

import com.sunz.framework.core.dbsupport.BaseDbMetaHelper;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IProcedureHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqlserverMetaHelper extends BaseDbMetaHelper implements IProcedureHelper {
    private static final String sql_table_columns = "select tc.name , \tcast(tcc.VALUE as varchar) comment, \ttt.name datatype, \ttc.length, \ttc.xprec precision, \ttc.xscale, \ttc.isnullable nullable, \ttc.cdefault defaultValue  from syscolumns tc  left join systypes tt on tt.xusertype=tc.xusertype  left join sys.extended_properties tcc on tcc.major_id=object_id(?) and tcc.minor_id=tc.colid  where tc.id=object_id(?)";
    private static Map<String, Integer> dictType = null;

    public SqlserverMetaHelper() {
        this.dataTypeMapping = dictType;
    }

    public List getTableColumnInfos(String tableName) {
        if (!dictColumnInfos.containsKey(tableName)) {
            List<TableColumnInfo> listColumnInfo = this.jdbcTemplate.getJdbcOperations().query("select tc.name , \tcast(tcc.VALUE as varchar) comment, \ttt.name datatype, \ttc.length, \ttc.xprec precision, \ttc.xscale, \ttc.isnullable nullable, \ttc.cdefault defaultValue  from syscolumns tc  left join systypes tt on tt.xusertype=tc.xusertype  left join sys.extended_properties tcc on tcc.major_id=object_id(?) and tcc.minor_id=tc.colid  where tc.id=object_id(?)", new Object[]{tableName, tableName}, this.tableColumnRowMapper);
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
        return "[";
    }

    public String getObjectEndQualifier() {
        return "]";
    }

    private List getObjects(String type) {
        return this.jdbcTemplate.getJdbcOperations().queryForList("select ts.name NAME,cast(tc.VALUE as varchar) COMMENTS  from sysObjects ts  left join sys.extended_properties tc on tc.major_id=ts.id and minor_id=0  where ts.xtype='" + type + "'");
    }

    public List getTableNames() {
        return this.getObjects("U");
    }

    public List getViewNames() {
        return this.getObjects("V");
    }

    public List getProcedureNames() {
        return this.getObjects("P");
    }

    public List getFunctionNames() {
        return this.getObjects("FN");
    }

    public List getTriggerNames() {
        return this.getObjects("TR");
    }

    private String getObjectDeclare(String name) {
        return (String)this.jdbcTemplate.getJdbcOperations().queryForObject("SELECT ISNULL((SELECT text FROM syscomments WHERE id = ( SELECT id FROM sysobjects WHERE name = ?)),NULL)", new Object[]{name}, String.class);
    }

    public String getProcedureDeclare(String proceName) {
        return this.getObjectDeclare(proceName);
    }

    public String getViewDeclare(String viewName) {
        return this.getObjectDeclare(viewName);
    }

    public String getFunctionDeclare(String funName) {
        return this.getObjectDeclare(funName);
    }

    public String getTriggerDeclare(String triggerName) {
        return this.getObjectDeclare(triggerName);
    }

    public boolean exists(String objName) {
        return (Boolean)this.jdbcTemplate.getJdbcOperations().queryForObject("SELECT ISNULL((select 1 from sysobjects where name=?),0)", new Object[]{objName}, Boolean.class);
    }

    public List parseParameter(String procName, String procDeclare) {
        List<Object[]> result = new ArrayList();
        String uDeclare = procDeclare.toUpperCase();
        int start = uDeclare.indexOf("PROCEDURE");
        int end = uDeclare.indexOf("BEGIN");
        String usefull = procDeclare.substring(start + 9, end);
        int to = usefull.toUpperCase().indexOf("DECLARE");
        if (to > -1) {
            usefull = usefull.substring(0, to);
        }

        to = usefull.toUpperCase().lastIndexOf("AS");
        int from = usefull.indexOf("@");
        if (from < 0) {
            return result;
        } else {
            usefull = usefull.substring(from, to);
            String[] all = usefull.split(",");
            String[] var11 = all;
            int var12 = all.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                String p = var11[var13];
                if (p != null) {
                    String[] pset = p.trim().split("\\s+");
                    String name = pset[0];
                    boolean isOut = p.toUpperCase().indexOf("OUTPUT") > 0;
                    String type = pset[1].equalsIgnoreCase("AS") ? pset[2] : pset[1];
                    String defaultValue = null;
                    int eqIndex = p.indexOf("=");
                    if (eqIndex > -1) {
                        defaultValue = p.substring(eqIndex + 1);
                        defaultValue = defaultValue.replaceFirst("(?i)(\\s+OUTPUT)?(\\s+READONLY)?\\s$", "");
                    }

                    result.add(new Object[]{name, type, isOut, defaultValue});
                }
            }

            return result;
        }
    }

    static {
        dictType = new HashMap();
        dictType.put("bigint", -5);
        dictType.put("binary", -2);
        dictType.put("bit", -7);
        dictType.put("char", 1);
        dictType.put("date", 91);
        dictType.put("uniqueidentifier", 12);
        dictType.put("money", 3);
        dictType.put("smallmoney", 3);
        dictType.put("geography", -2);
        dictType.put("geometry", -5);
        dictType.put("datetime", 93);
        dictType.put("datetime2", 93);
        dictType.put("datetimeoffset", 93);
        dictType.put("sql_variant", -3);
        dictType.put("sysname", 12);
        dictType.put("decimal", 3);
        dictType.put("float", 6);
        dictType.put("hierarchyid", 12);
        dictType.put("image", -2);
        dictType.put("int", 4);
        dictType.put("nchar", -15);
        dictType.put("ntext", -16);
        dictType.put("numeric", 2);
        dictType.put("nvarchar", -9);
        dictType.put("real", 7);
        dictType.put("smalldatetime", 92);
        dictType.put("smallint", 5);
        dictType.put("text", -1);
        dictType.put("time", 92);
        dictType.put("timestamp", 93);
        dictType.put("tinyint", -6);
        dictType.put("varbinary", -3);
        dictType.put("varchar", 12);
        dictType.put("xml", 2009);
    }
}
