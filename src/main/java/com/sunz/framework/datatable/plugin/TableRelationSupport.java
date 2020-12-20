//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable.plugin;

import com.sunz.framework.core.GuidGenerator;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.datatable.IDatatableService;
import com.sunz.framework.datatable.event.EventHelper;
import com.sunz.framework.datatable.event.IAddHandler;
import com.sunz.framework.datatable.event.IDeleteHandler;
import com.sunz.framework.datatable.event.ISaveHandler;
import com.sunz.framework.util.StringUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TableRelationSupport implements ICacheRefreshable {
    private IDatatableService datatableService;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Map<String, List<TableRelationSupport.RelationInfo>> relationMapping = new HashMap();

    public TableRelationSupport() {
    }

    @Autowired
    public void setDatatableService(IDatatableService datatableService) {
        this.datatableService = datatableService;
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = namedParameterJdbcTemplate;
    }

    public void initMapping() {
        this.relationMapping.clear();
        String sql = "select tableName_,foreign_,attribute_name_,relation_,many_,force_one_,delete_cascade_ from t_s_datatable_relation";
        this.jdbcTemplate.query(sql, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                TableRelationSupport.RelationInfo info;
                Object list;
                for(; rs.next(); ((List)list).add(info)) {
                    info = new TableRelationSupport.RelationInfo();
                    info.tableName = rs.getString(1);
                    info.subTable = rs.getString(2);
                    info.fieldName = rs.getString(3);
                    info.relation = rs.getString(4);
                    info.oneToMany = rs.getBoolean(5);
                    info.forceOneToOne = rs.getBoolean(6);
                    info.deleteCascade = rs.getBoolean(7);
                    list = (List)TableRelationSupport.this.relationMapping.get(info.tableName);
                    if (list == null) {
//                        TableRelationSupport.this.relationMapping.put(info.tableName, list = new ArrayList()); // TODO 原
                        TableRelationSupport.this.relationMapping.put(info.tableName, (List<RelationInfo>) (list = new ArrayList()));
                    }
                }

                return null;
            }
        });
    }

    private List<TableRelationSupport.RelationInfo> getRelationInfos(String tableName) {
        return (List)this.relationMapping.get(tableName.toLowerCase());
    }

    private void resolveRelationData(Map<String, Object> target, Map<String, Object> parentValues, TableRelationSupport.RelationInfo info) {
        String k;
        if (info.relatianMaps == null) {
            String[] fieldMaps = StringUtil.parseToArray(info.relation);
            info.relatianMaps = new ArrayList(fieldMaps.length);
            String[] var5 = fieldMaps;
            int var6 = fieldMaps.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                k = var5[var7];
                if (!StringUtil.isEmpty(k)) {
                    String[] fm = k.split("\\s*(:|=)\\s*");
                    info.relatianMaps.add(fm.length > 1 ? fm : new String[]{fm[0], "id"});
                }
            }
        }

        boolean assertX = true;
        Iterator var11 = info.relatianMaps.iterator();

        while(true) {
            while(var11.hasNext()) {
                String[] mapping = (String[])var11.next();
                Iterator var13 = parentValues.keySet().iterator();

                while(var13.hasNext()) {
                    k = (String)var13.next();
                    if (mapping[1].equalsIgnoreCase(k)) {
                        target.put(mapping[0], parentValues.get(k));
                        assertX = false;
                        break;
                    }
                }
            }

            if (assertX) {
                throw new RuntimeException("关联关系未正确定义（格式为子表字段=父表字段），无法处理关联表");
            }

            return;
        }
    }

    private List addSub(Map<String, Object> rawValues, TableRelationSupport.RelationInfo info, Map<String, Object> parentValues) {
        String curKey = info.fieldName + ".";
        int len = curKey.length();
        Map<String, Object> publicMap = new HashMap();
        List<Map<String, Object>> subParams = new ArrayList();
        if (rawValues.containsKey(info.fieldName)) {
            Object raw = rawValues.get(info.fieldName);
            Object[] jsons = raw != null && raw instanceof Object[] ? (Object[])((Object[])raw) : new Object[]{raw};
            Object[] var10 = jsons;
            int var11 = jsons.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                Object o = var10[var12];
                String json = (String)o;
                if (!StringUtil.isEmpty(json)) {
                    subParams.add((Map)JsonHelper.toBean(json, HashMap.class));
                }
            }
        }

        Iterator var15 = rawValues.keySet().iterator();

        while(var15.hasNext()) {
            String k = (String)var15.next();
            if (k.startsWith(curKey)) {
                publicMap.put(k.substring(len), rawValues.get(k));
            }
        }

        if (subParams.size() == 0) {
            subParams.add(new HashMap());
        }

        List subResults = new ArrayList();
        Iterator var18 = subParams.iterator();

        while(true) {
            Map map;
            do {
                if (!var18.hasNext()) {
                    parentValues.put(info.fieldName, subResults.size() == 1 ? subResults.get(0) : subResults);
                    return subResults;
                }

                map = (Map)var18.next();
                map.putAll(publicMap);
            } while(map.size() <= 0 && !info.forceOneToOne);

            if (!map.containsKey("id") || !map.containsKey("ID")) {
                String subId = GuidGenerator.getGuid();
                map.put("id", subId);
            }

            this.resolveRelationData(map, parentValues, info);
            JsonResult jr = this.datatableService.add(info.subTable, map);
            if (!jr.isSuccess()) {
                throw new RuntimeException("属性【" + info.fieldName + "】保存出错：" + jr.getMsg());
            }

            subResults.add(jr.getData());
        }
    }

    private String toClause(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        Iterator var3 = param.keySet().iterator();

        while(var3.hasNext()) {
            String k = (String)var3.next();
            sb.append("AND ").append(k).append("=:").append(k).append(",");
        }

        return sb.substring(4, sb.length() - 1);
    }

    @PostConstruct
    public void init() {
        this.initMapping();
        EventHelper.registerEventHandler(new IAddHandler() {
            public void onAdd(String tableName, String id, Map<String, Object> fieldValues, Map<String, Object> rawValues) {
                List<TableRelationSupport.RelationInfo> infos = TableRelationSupport.this.getRelationInfos(tableName);
                if (infos != null) {
                    Iterator var6 = infos.iterator();

                    while(var6.hasNext()) {
                        TableRelationSupport.RelationInfo info = (TableRelationSupport.RelationInfo)var6.next();
                        TableRelationSupport.this.addSub(rawValues, info, fieldValues);
                    }
                }

            }
        });
        EventHelper.registerEventHandler(new ISaveHandler() {
            public void onSave(String tableName, String id, Map<String, Object> fieldValues, Map<String, Object> rawValues) {
                List<TableRelationSupport.RelationInfo> infos = TableRelationSupport.this.getRelationInfos(tableName);
                if (infos != null) {
                    Iterator var6 = infos.iterator();

                    while(var6.hasNext()) {
                        TableRelationSupport.RelationInfo info = (TableRelationSupport.RelationInfo)var6.next();
                        Map<String, Object> deleteParam = new HashMap();
                        TableRelationSupport.this.resolveRelationData(deleteParam, fieldValues, info);
                        String sqlDelete = "delete from " + info.subTable + " where " + TableRelationSupport.this.toClause(deleteParam);
                        TableRelationSupport.this.jdbcTemplate.update(sqlDelete, deleteParam);
                        TableRelationSupport.this.addSub(rawValues, info, fieldValues);
                    }
                }

            }
        });
        // TODO 报错 注释掉了
//        EventHelper.registerEventHandler((tableName, type, arg, keepCase, results) -> {
//            if ("getById".equals(type) && results != null && results.size() > 0) {
//                Map<String, Object> bean = (Map)results.get(0);
//                List<TableRelationSupport.RelationInfo> infos = this.getRelationInfos(tableName);
//                if (infos != null) {
//                    Iterator var8 = infos.iterator();
//
//                    while(var8.hasNext()) {
//                        TableRelationSupport.RelationInfo info = (TableRelationSupport.RelationInfo)var8.next();
//                        Map<String, Object> queryParam = new HashMap();
//                        this.resolveRelationData(queryParam, bean, info);
//                        ListJsonResult jr = this.datatableService.query(info.subTable, this.toClause(queryParam), (String)null, queryParam, (PageParameter)null, keepCase);
//                        if (jr.isSuccess()) {
//                            String attributeName = info.fieldName;
//                            if (info.oneToMany) {
//                                bean.put(attributeName, jr.list());
//                            } else if (jr.list().size() == 1) {
//                                bean.put(attributeName, jr.list().get(0));
//                            } else if (info.forceOneToOne) {
//                                throw new RuntimeException("表关联（属性【" + info.fieldName + "】)数据完整性不正确，期望【1】行但实际【" + jr.list().size() + "】行");
//                            }
//                        }
//                    }
//                }
//            }
//
//        });
        EventHelper.registerEventHandler(new IDeleteHandler() {
            public void onDelete(String tableName, final String[] ids, Integer effect) {
                List<TableRelationSupport.RelationInfo> infos = TableRelationSupport.this.getRelationInfos(tableName);
                if (infos != null) {
                    Iterator var5 = infos.iterator();

                    while(true) {
                        TableRelationSupport.RelationInfo info;
                        do {
                            if (!var5.hasNext()) {
                                return;
                            }

                            info = (TableRelationSupport.RelationInfo)var5.next();
                        } while(!info.deleteCascade);

                        Map<String, Object> deleteParam = new HashMap();
                        TableRelationSupport.this.resolveRelationData(deleteParam, new HashMap<String, Object>() {
                            {
                                this.put("id", ids[0]);
                            }
                        }, info);
                        String sqlDelete = "delete from " + info.subTable + " where " + TableRelationSupport.this.toClause(deleteParam);
                        Map<String, Object>[] args = new Map[ids.length];
                        args[0] = deleteParam;

                        for(int i = 1; i < ids.length; ++i) {
                            args[i] = new HashMap();
                            args[i].put("id", ids[i]);
                        }

                        TableRelationSupport.this.jdbcTemplate.batchUpdate(sqlDelete, args);
                    }
                }
            }
        });
    }

    public void refresh(String item) {
        this.initMapping();
    }

    public String getCategory() {
        return "datatable_relation";
    }

    public String getDescription() {
        return "Datatable关联关系刷新（数量级很小，不支持单项刷新）";
    }

    private static class RelationInfo {
        String tableName;
        String subTable;
        String fieldName;
        String relation;
        boolean oneToMany;
        boolean forceOneToOne;
        boolean deleteCascade;
        List<String[]> relatianMaps;

        private RelationInfo() {
        }
    }
}
