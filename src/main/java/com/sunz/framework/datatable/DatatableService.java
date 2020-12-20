//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable;

import com.sunz.framework.core.Entity;
import com.sunz.framework.core.GuidGenerator;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.core.util.IPagingQueryService;
import com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher;
import com.sunz.framework.datatable.event.EventHelper;
import com.sunz.framework.util.DateUtil;
import com.sunz.framework.util.StringUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.CommonService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatatableService implements IDatatableService, IDatatableServiceEx, ICacheRefreshable, ApplicationContextAware {
    protected Logger logger = Logger.getLogger(this.getClass());
    protected CommonService commonService;
    IDbMetaHelper dbMetaHelper;
    @Autowired
    IPagingQueryService pagingQueryService;
    private Map<Integer, List<ITableDataConvertor>> dictConvertor = new HashMap();
    private ITableDataConvertor defaultConvertor = new ITableDataConvertor() {
        public int getAbilityType() {
            return 0;
        }

        public Object convert(Object origValue, TableColumnInfo cInfo, String tableName, Boolean refHandled, Integer refType) {
            if (!(origValue instanceof String)) {
                return origValue;
            } else {
                String value = (String)origValue;
                if (value != null && !"".equals(value)) {
                    switch(cInfo.getCommonDatatype()) {
                    case 2:
                    case 3:
                    case 8:
                        return Double.parseDouble(value);
                    case 4:
                    case 5:
                        return Integer.parseInt(value);
                    case 6:
                        return Float.parseFloat(value);
                    case 91:
                    case 92:
                    case 93:
                        return DateUtil.parseDate(value);
                    default:
                        return value;
                    }
                } else {
                    return null;
                }
            }
        }
    };
    private static Map<String, Map> cachedColumnMap = new HashMap();

    public DatatableService() {
    }

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        this.dbMetaHelper = dbMetaHelper;
    }

    public void setPagingQueryService(IPagingQueryService pagingQueryService) {
        this.pagingQueryService = pagingQueryService;
    }

    public static Map dealParameterMap(Map<String, Object> data, List<TableColumnInfo> infos) {
        Map mResult = new HashMap();
        Iterator iter = data.keySet().iterator();

        label40:
        while(iter.hasNext()) {
            String dKey = (String)iter.next();
            Iterator var5 = infos.iterator();

            while(true) {
                while(true) {
                    String rKey;
                    String key;
                    do {
                        if (!var5.hasNext()) {
                            continue label40;
                        }

                        TableColumnInfo info = (TableColumnInfo)var5.next();
                        rKey = info.getName();
                        key = rKey.toUpperCase();
                    } while(!dKey.equalsIgnoreCase(key));

                    Object value = data.get(dKey);
                    if (value != null && value instanceof Object[]) {
                        Object[] values = (Object[])((Object[])value);
                        if (values.length == 0) {
                            mResult.put(rKey, (Object)null);
                        } else if (values.length == 1) {
                            mResult.put(rKey, values[0]);
                        } else {
                            mResult.put(rKey, values);
                        }
                    } else {
                        mResult.put(rKey, value);
                    }
                }
            }
        }

        return mResult;
    }

    @Transactional
    public JsonResult add(String tablename, Map<String, Object> fieldValues) {
        List infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            EventHelper.dispatchEvent("beforeAdd", tablename, new Object[]{fieldValues});
            String id = (String)fieldValues.get("id");
            if (id == null || "".equals(id)) {
                id = (String)fieldValues.get("ID");
            }

            if (id == null || "".equals(id)) {
                id = GuidGenerator.getGuid();
            }

            fieldValues.put("ID", id);
            fieldValues.remove("id");
            Map data = dealParameterMap(fieldValues, infos);

            PrepareStatementInfo statementInfo;
            try {
                statementInfo = this.getInsertStatementInfo(tablename, data, infos);
            } catch (Exception var9) {
                this.logger.info("解析数据时出错:", var9);
                return new JsonResult("解析数据时出错，信息：" + var9.getMessage());
            }

            try {
                this.commonService.executeSql(statementInfo.getSql(), statementInfo.getParameters().toArray());
            } catch (Exception var8) {
                this.logger.info("新增数据出错了", var8);
                return new JsonResult("新增数据出错了，信息：" + var8.getMessage());
            }

            data.put("id", id);
            data.put("ID", id);
            EventHelper.dispatchEvent("add", tablename, new Object[]{id, data, fieldValues});
            return new JsonResult(data);
        } else {
            return new JsonResult("内部错误：非法的数据表名称");
        }
    }

    private String findIdField(List<TableColumnInfo> infos) {
        Iterator var2 = infos.iterator();

        TableColumnInfo cInfo;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            cInfo = (TableColumnInfo)var2.next();
        } while(!"id".equalsIgnoreCase(cInfo.getName()));

        return cInfo.getName();
    }

    @Transactional
    public JsonResult save(String tablename, String id, Map<String, Object> fieldValues) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            String idField = this.findIdField(infos);
            if (idField == null) {
                return new JsonResult("内部错误：无id字段的数据表不允许使用此接口进行保存");
            } else {
                fieldValues.put(idField, id);
                EventHelper.dispatchEvent("beforeSave", tablename, new Object[]{id, fieldValues});
                Map data = dealParameterMap(fieldValues, infos);
                if (data.get(idField) == null) {
                    return new JsonResult("内部错误：当前保存操作没有指定ID");
                } else {
                    if (data.size() > 1) {
                        PrepareStatementInfo statementInfo;
                        try {
                            statementInfo = this.getUpdateStatementInfo(tablename, data, infos);
                        } catch (Exception var10) {
                            this.logger.info("解析数据时出错:", var10);
                            return new JsonResult("解析数据时出错，信息：" + var10.getMessage());
                        }

                        try {
                            this.commonService.executeSql(statementInfo.getSql(), statementInfo.getParameters().toArray());
                        } catch (Exception var9) {
                            this.logger.info("保存数据出错了", var9);
                            return new JsonResult("保存数据出错了，信息：" + var9.getMessage());
                        }

                        Object idValue = data.get(idField);
                        data.put("id", idValue);
                        data.put("ID", idValue);
                    }

                    EventHelper.dispatchEvent("save", tablename, new Object[]{id, data, fieldValues});
                    return new JsonResult(data);
                }
            }
        } else {
            return new JsonResult("内部错误：非法的数据表名称");
        }
    }

    public JsonResult getById(String tablename, String id, Boolean keepCase) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            if (StringUtil.isEmpty(this.findIdField(infos))) {
                return new JsonResult("内部错误：此数据表中没有名称为ID的字段，暂不支持直接操作");
            } else {
                Map map = new HashMap();
                map.put("id", id);
                EventHelper.dispatchEvent("beforeQuery", tablename, new Object[]{"getById", id, keepCase});
                ListJsonResult r = this.query(tablename, "id=:id", (String)null, map, new PageParameter(), keepCase);
                List list = r.list();
                EventHelper.dispatchEvent("query", tablename, new Object[]{"getById", id, keepCase, list});
                return list != null && list.size() != 0 ? new JsonResult(list.get(0)) : new JsonResult("指定数据不存在");
            }
        } else {
            return new JsonResult("内部错误：非法的数据表名称");
        }
    }

    @Transactional
    public JsonResult delete(String tablename, String id) {
        if (id != null && !"".equals(id)) {
            List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
            if (infos != null && infos.size() != 0) {
                if (StringUtil.isEmpty(this.findIdField(infos))) {
                    return new JsonResult("内部错误：此数据表中没有名称为ID的字段，暂不支持直接操作");
                } else {
                    try {
                        EventHelper.dispatchEvent("beforeDelete", tablename, new Object[]{id});
                        Integer effect = this.commonService.executeSql("delete from " + tablename + " where id=?", new Object[]{new SqlParameterValue(1, id)});
                        EventHelper.dispatchEvent("delete", tablename, new Object[]{id, effect});
                        return new JsonResult(effect);
                    } catch (Exception var5) {
                        this.logger.info("删除数据出错了", var5);
                        return new JsonResult("删除数据出错了，信息：" + var5.getMessage());
                    }
                }
            } else {
                return new JsonResult("内部错误：非法的数据表名称");
            }
        } else {
            return new JsonResult("内部错误：当前删除操作没有指定ID");
        }
    }

    public ListJsonResult query(String tablename, String clause, String order, Map params, PageParameter page, boolean keepCase) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            String fields = "";
            if (keepCase) {
                fields = "*";
            } else {
                TableColumnInfo cInfo;
                for(Iterator var9 = infos.iterator(); var9.hasNext(); fields = fields + this.dbMetaHelper.getObjectStartQualifier() + cInfo.getName() + this.dbMetaHelper.getObjectEndQualifier() + " \"" + cInfo.getName().toUpperCase() + "\" ,") {
                    cInfo = (TableColumnInfo)var9.next();
                }

                fields = fields.substring(0, fields.length() - 2);
            }

            String sql = "select " + fields + " from " + tablename;
            if (clause != null && !"".equals(clause)) {
                sql = sql + " where " + clause;
            }

            if (order != null && !"".equals(order)) {
                sql = sql + " order by " + order;
            }

            return this.pagingQueryService.query(sql, params, page);
        } else {
            return new ListJsonResult("内部错误：非法的数据表名");
        }
    }

    private void addConvertor(int type, ITableDataConvertor convertor) {
        List<ITableDataConvertor> list = (List)this.dictConvertor.get(type);
        if (list == null) {
            this.dictConvertor.put(type, list = new ArrayList());
        }

        ((List)list).add(convertor);
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        try {
            Map<String, ITableDataConvertor> all = context.getBeansOfType(ITableDataConvertor.class);
            Iterator var3 = all.values().iterator();

            while(var3.hasNext()) {
                ITableDataConvertor convertor = (ITableDataConvertor)var3.next();
                this.addConvertor(convertor.getAbilityType(), convertor);
            }
        } catch (Exception var5) {
        }

    }

    private SqlParameterValue getSqlParameter(Object value, TableColumnInfo info, String tableName) throws ParseException {
        Boolean refHandled = true;
        Integer refType = info.getCommonDatatype();
        List<ITableDataConvertor> list = (List)this.dictConvertor.get(refType);
        if (list != null) {
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
                ITableDataConvertor convertor = (ITableDataConvertor)var7.next();
                Object formatValue = convertor.convert(value, info, tableName, refHandled, refType);
                if (refHandled) {
                    return new SqlParameterValue(refType, formatValue);
                }
            }
        }

        Object v = this.defaultConvertor.convert(value, info, tableName, refHandled, refType);
        return new SqlParameterValue(refType, v);
    }

    public void refresh(String item) {
        cachedColumnMap.clear();
        ICacheRefreshable metaRefresher = (ICacheRefreshable)this.dbMetaHelper;
        if (metaRefresher != null) {
            metaRefresher.refresh(item);
        }

    }

    public String getCategory() {
        return "datatableservice";
    }

    public String getDescription() {
        return "datatable接口缓存（同时会刷新数据库结构缓存--如果可用）";
    }

    private Map toMap(String tableName, List<TableColumnInfo> infos) {
        tableName = tableName.toUpperCase();
        if (cachedColumnMap.containsKey(tableName)) {
            return (Map)cachedColumnMap.get(tableName);
        } else {
            Map dict = new HashMap();
            Iterator var4 = infos.iterator();

            while(var4.hasNext()) {
                TableColumnInfo info = (TableColumnInfo)var4.next();
                String key = info.getName();
                dict.put(key, info);
            }

            cachedColumnMap.put(tableName, dict);
            return dict;
        }
    }

    public PrepareStatementInfo getInsertStatementInfo(String tablename, Map data, List<TableColumnInfo> infos) throws Exception {
        List<SqlParameterValue> paramList = new ArrayList();
        StringBuilder str = new StringBuilder("insert into ");
        str.append(tablename);
        str.append(" ( ");
        String strParam = " values ( ";
        Iterator<String> iter = data.keySet().iterator();
        Map dict = this.toMap(tablename, infos);

        while(iter.hasNext()) {
            String key = (String)iter.next();
            str.append(this.dbMetaHelper.getObjectStartQualifier());
            str.append(key);
            str.append(this.dbMetaHelper.getObjectEndQualifier());
            str.append(",");
            strParam = strParam + "?,";
            TableColumnInfo info = (TableColumnInfo)dict.get(key);
            paramList.add(this.getSqlParameter(data.get(key), info, tablename));
        }

        str.deleteCharAt(str.length() - 1);
        strParam = strParam.substring(0, strParam.length() - 1);
        str.append(" ) ");
        strParam = strParam + " ) ";
        return new PrepareStatementInfo(str.toString() + strParam, paramList);
    }

    public PrepareStatementInfo getUpdateStatementInfo(String tablename, Map data, List<TableColumnInfo> infos) throws Exception {
        List<SqlParameterValue> paramList = new ArrayList();
        StringBuilder str = new StringBuilder("UPDATE ");
        str.append(tablename);
        str.append(" SET ");
        Iterator<String> iter = data.keySet().iterator();
        Map dict = this.toMap(tablename, infos);

        String idField;
        while(iter.hasNext()) {
            idField = (String)iter.next();
            if (!"id".equalsIgnoreCase(idField)) {
                str.append(this.dbMetaHelper.getObjectStartQualifier());
                str.append(idField);
                str.append(this.dbMetaHelper.getObjectEndQualifier());
                str.append("=?,");
                TableColumnInfo info = (TableColumnInfo)dict.get(idField);
                paramList.add(this.getSqlParameter(data.get(idField), info, tablename));
            }
        }

        str.deleteCharAt(str.length() - 1);
        idField = null;
        Iterator var11 = infos.iterator();

        while(var11.hasNext()) {
            TableColumnInfo cInfo = (TableColumnInfo)var11.next();
            if ("id".equalsIgnoreCase(cInfo.getName())) {
                idField = cInfo.getName();
                break;
            }
        }

        str.append(" WHERE ").append(idField).append("=?");
        paramList.add(this.getSqlParameter(data.get(idField), (TableColumnInfo)dict.get(idField), tablename));
        return new PrepareStatementInfo(str.toString(), paramList);
    }

    public JsonResult batchDelete(String tablename, String[] ids) {
        if (ids != null && ids.length != 0) {
            List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
            if (infos != null && infos.size() != 0) {
                boolean find = false;
                Iterator var5 = infos.iterator();

                while(var5.hasNext()) {
                    TableColumnInfo o = (TableColumnInfo)var5.next();
                    if (o.getName().equalsIgnoreCase("id")) {
                        find = true;
                        break;
                    }
                }

                if (!find) {
                    return new JsonResult("内部错误：此数据表中没有名称为ID的字段，暂不支持直接操作");
                } else {
                    try {
                        SqlParameterValue[] params = new SqlParameterValue[ids.length];
                        String clause = "(";

                        for(int i = 0; i < ids.length; ++i) {
                            params[i] = new SqlParameterValue(1, ids[i]);
                            clause = clause + "?,";
                        }

                        clause = clause.substring(0, clause.length() - 1) + ")";
                        Integer reffict = this.commonService.executeSql("delete from " + tablename + " where id in" + clause, params);
                        return new JsonResult(reffict);
                    } catch (Exception var8) {
                        this.logger.info("删除数据出错了", var8);
                        return new JsonResult("删除数据出错了，信息：" + var8.getMessage());
                    }
                }
            } else {
                return new JsonResult("内部错误：非法的数据表名称");
            }
        } else {
            return new JsonResult("内部错误：批量删除操作没有指定任何ID");
        }
    }

    private String getAnnotationTableName(Object entity) {
        Table anno = (Table)entity.getClass().getAnnotation(Table.class);
        return anno != null ? anno.name() : null;
    }

    public JsonResult add(Entity entity) {
        String tableName = this.getAnnotationTableName(entity);
        if (StringUtil.isEmpty(tableName)) {
            return new JsonResult("内部错误：当前对象无Table（javax.persistence.Table）注解");
        } else {
            Map<String, Object> map = JdbcTemplateRowMapperDispatcher.toNamedParameterMap(entity);
            return this.add(tableName, map);
        }
    }

    public JsonResult save(Entity entity) {
        String tableName = this.getAnnotationTableName(entity);
        if (StringUtil.isEmpty(tableName)) {
            return new JsonResult("内部错误：当前对象无Table（javax.persistence.Table）注解");
        } else {
            Map<String, Object> map = JdbcTemplateRowMapperDispatcher.toNamedParameterMap(entity, false);
            return this.save(tableName, (String)map.get("id"), map);
        }
    }
}
