//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(
    name = "T_S_ExcelExport"
)
public class ExportInfo extends com.sunz.framework.core.Entity {
    private String key;
    private String description;
    private byte[] template;
    private String sqlid;
    private String fieldMapping;
    private Integer startRow;
    private Integer startColumn;
    private String ignoreRows;
    private String ignoreColumns;
    private String dictFields;
    private static List<String> Empty_List_String = Arrays.asList();
    private static List<Integer> Empty_List_Int = Arrays.asList();

    public ExportInfo() {
    }

    public String getKey() {
        return this.key;
    }

    public String getDescription() {
        return this.description;
    }

    public byte[] getTemplate() {
        return this.template;
    }

    public String getSqlid() {
        return this.sqlid;
    }

    public Integer getStartRow() {
        return this.startRow;
    }

    public Integer getStartColumn() {
        return this.startColumn;
    }

    public String getIgnoreRows() {
        return this.ignoreRows;
    }

    public String getIgnoreColumns() {
        return this.ignoreColumns;
    }

    public String getDictFields() {
        return this.dictFields;
    }

    public static List<String> getEmpty_List_String() {
        return Empty_List_String;
    }

    public static List<Integer> getEmpty_List_Int() {
        return Empty_List_Int;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public void setSqlid(String sqlid) {
        this.sqlid = sqlid;
    }

    public void setFieldMapping(String fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    public String getFieldMapping() {
        return this.fieldMapping;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    public void setIgnoreRows(String ignoreRows) {
        this.ignoreRows = ignoreRows;
    }

    public void setIgnoreColumns(String ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public void setDictFields(String dictFields) {
        this.dictFields = dictFields;
    }

    public static void setEmpty_List_String(List<String> empty_List_String) {
        Empty_List_String = empty_List_String;
    }

    public static void setEmpty_List_Int(List<Integer> empty_List_Int) {
        Empty_List_Int = empty_List_Int;
    }

    @Transient
    public List<String> getDictFieldList() {
        return this.dictFields != null && !"".equals(this.dictFields) ? Arrays.asList(this.dictFields.split(",")) : Empty_List_String;
    }

    @Transient
    public List<Integer> getIgnoreRowList() {
        if (this.ignoreRows != null && !"".equals(this.ignoreRows)) {
            String[] strs = this.ignoreRows.split(",");
            List<Integer> list = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                list.add(Integer.parseInt(strs[i]));
            }

            return list;
        } else {
            return Empty_List_Int;
        }
    }

    @Transient
    public List<Integer> getIgnoreColumnList() {
        if (this.ignoreColumns != null && !"".equals(this.ignoreColumns)) {
            String[] strs = this.ignoreColumns.split(",");
            List<Integer> list = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                list.add(Integer.parseInt(strs[i]));
            }

            return list;
        } else {
            return Empty_List_Int;
        }
    }

    @Transient
    public Map getFieldMappingMap() {
        return getFieldMapping(this.fieldMapping);
    }

    private static String trimQuotes(String str, boolean trim) {
        if (str != null && !"".equals(str)) {
            if (trim) {
                str = str.trim();
            }

            if (str.startsWith("\"")) {
                str = str.substring(1);
            }

            if (str.startsWith("'")) {
                str = str.substring(1);
            }

            if (str.endsWith("\"")) {
                str = str.substring(0, str.length() - 1);
            }

            if (str.endsWith("'")) {
                str = str.substring(0, str.length() - 1);
            }

            return str;
        } else {
            return str;
        }
    }

    public static Map getFieldMapping(String[] arr) {
        LinkedHashMap mResult = new LinkedHashMap();

        for(int i = 0; i < arr.length; ++i) {
            String strMap = arr[i];
            if (strMap != null && strMap.trim() != "") {
                String[] keyValue = strMap.split(":");
                String key = trimQuotes(keyValue[0], true);
                String value = keyValue.length > 1 ? trimQuotes(keyValue[1], false) : key;
                mResult.put(key, value);
            }
        }

        return mResult;
    }

    public static Map getFieldMapping(String strMapping) {
        return (Map)(strMapping != null && !"".equals(strMapping) ? getFieldMapping(strMapping.split(",")) : new LinkedHashMap());
    }
}
