//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.dbsupport.TableColumnInfo;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.datatable.event.EventHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/framework/datatable"})
public final class DataTableController extends BaseController {
    IDbMetaHelper dbMetaHelper;
    protected IDatatableService datatableService;
    private static Pattern patternIllegal = Pattern.compile("\\b(UNION|BEGIN|FROM|EXEC|INSERT|SELECT|DELETE|UPDATE)\\b");

    public DataTableController() {
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        this.dbMetaHelper = dbMetaHelper;
    }

    @Autowired
    public void setDatatableService(IDatatableService datableService) {
        this.datatableService = datableService;
    }

    @RequestMapping(
        params = {"tableNames"}
    )
    @ResponseBody
    public ListJsonResult tableNames() {
        return new ListJsonResult(this.dbMetaHelper.getTableNames());
    }

    @RequestMapping(
        params = {"columnInfos"}
    )
    @ResponseBody
    public ListJsonResult columnInfos(@RequestParam(value = "t",required = true) String tablename) {
        List infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        return infos != null && infos.size() != 0 ? new ListJsonResult(infos) : new ListJsonResult("内部错误：未指定操作对象");
    }

    @RequestMapping(
        params = {"add"}
    )
    @ResponseBody
    public JsonResult insert(@RequestParam(value = "t",required = true) String tablename) {
        return this.datatableService.add(tablename, this.toMap());
    }

    @RequestMapping(
        params = {"save"}
    )
    @ResponseBody
    public JsonResult save(@RequestParam(value = "t",required = true) String tablename, String id, String ID) {
        id = this.isStringEmpty(id) ? ID : id;
        return this.isStringEmpty(id) ? new JsonResult("内部错误：未指定数据行") : this.datatableService.save(tablename, id, this.toMap());
    }

    @RequestMapping(
        params = {"delete"}
    )
    @ResponseBody
    public JsonResult delete(@RequestParam(value = "t",required = true) String tablename, String id, String ID) {
        id = this.isStringEmpty(id) ? ID : id;
        return this.isStringEmpty(id) ? new JsonResult("内部错误：未指定数据行") : this.datatableService.delete(tablename, id);
    }

    @RequestMapping(
        params = {"all"}
    )
    @ResponseBody
    public ListJsonResult all(@RequestParam(value = "t",required = true) String tablename, PageParameter page, String order, @RequestParam(defaultValue = "false") Boolean keepCase) {
        if (this.isStringEmpty(tablename)) {
            return new ListJsonResult("内部错误：未指定操作对象");
        } else if (order != null && !"".equals(order) && isIllegal(order)) {
            return new ListJsonResult("不受支持的排序");
        } else {
            EventHelper.dispatchEvent("beforeQuery", tablename, new Object[]{"query", null, keepCase});
            ListJsonResult result = this.datatableService.query(tablename, (String)null, order, (Map)null, page, keepCase);
            EventHelper.dispatchEvent("query", tablename, new Object[]{"query", null, keepCase, result.list()});
            return result;
        }
    }

    @RequestMapping(
        params = {"getById"}
    )
    @ResponseBody
    public JsonResult getById(@RequestParam(value = "t",required = true) String tablename, @RequestParam(required = false) String id, @RequestParam(required = false) String ID, @RequestParam(defaultValue = "false") Boolean keepCase) {
        id = this.isStringEmpty(id) ? ID : id;
        return this.isStringEmpty(id) ? new JsonResult("未指定数据行") : this.datatableService.getById(tablename, id, keepCase);
    }

    @RequestMapping(
        params = {"exactly"}
    )
    @ResponseBody
    public ListJsonResult exactly(@RequestParam(value = "t",required = true) String tablename, String order, PageParameter page, @RequestParam(defaultValue = "false") Boolean keepCase) {
        return this.queryByType(tablename, order, page, "=", keepCase);
    }

    @RequestMapping(
        params = {"likely"}
    )
    @ResponseBody
    public ListJsonResult likely(@RequestParam(value = "t",required = true) String tablename, String order, PageParameter page, @RequestParam(defaultValue = "false") Boolean keepCase) {
        return this.queryByType(tablename, order, page, "like", keepCase);
    }

    private static boolean isIllegal(String sqlPart) {
        return sqlPart.indexOf(";") > -1 ? true : patternIllegal.matcher(sqlPart.toUpperCase()).find();
    }

    @RequestMapping(
        params = {"query"}
    )
    @ResponseBody
    public ListJsonResult query(@RequestParam(value = "t",required = true) String tablename, @RequestParam(value = "clause",required = false) String clause, @RequestParam(value = "order",required = false) String order, PageParameter page, @RequestParam(defaultValue = "false") Boolean keepCase) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            if (clause != null && !"".equals(clause) && isIllegal(clause)) {
                return new ListJsonResult("不受支持的sql条件");
            } else if (order != null && !"".equals(order) && isIllegal(order)) {
                return new ListJsonResult("不受支持的排序");
            } else {
                EventHelper.dispatchEvent("beforeQuery", tablename, new Object[]{"query", clause, keepCase});
                ListJsonResult result = this.datatableService.query(tablename, clause, order, (Map)null, page, keepCase);
                EventHelper.dispatchEvent("query", tablename, new Object[]{"query", clause, keepCase, result.list()});
                return result;
            }
        } else {
            return new ListJsonResult("内部错误：非法的数据表名称");
        }
    }

    private ListJsonResult queryByType(String tablename, String order, PageParameter page, String type, boolean keepCase) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            StringBuilder sb = new StringBuilder();
            Map<String, Object> data = DatatableService.dealParameterMap(this.toMap(), infos);
            Iterator iter = data.entrySet().iterator();

            while(true) {
                boolean isValueNull;
                String field;
                while(true) {
                    if (!iter.hasNext()) {
                        if (sb.length() > 0) {
                            sb.delete(0, 4);
                        }

                        if (order != null && !"".equals(order) && isIllegal(order)) {
                            return new ListJsonResult("不受支持的排序");
                        }

                        String clause = sb.toString();
                        String eventType = "like".equals(type) ? "likely" : "exactly";
                        EventHelper.dispatchEvent("beforeQuery", tablename, new Object[]{eventType, clause, keepCase});
                        ListJsonResult result = this.datatableService.query(tablename, clause, order, data, page, keepCase);
                        EventHelper.dispatchEvent("query", tablename, new Object[]{eventType, clause, keepCase, result.list()});
                        return result;
                    }

                    Entry<String, Object> entry = (Entry)iter.next();
                    isValueNull = entry.getValue() == null || entry.getValue().toString().length() == 0;
                    field = (String)entry.getKey();
                    if (!"like".equals(type)) {
                        break;
                    }

                    if (!isValueNull) {
                        data.put(field, "%" + (String)data.get(field) + "%");
                        break;
                    }
                }

                sb.append(" and (");
                sb.append(this.dbMetaHelper.getObjectStartQualifier());
                sb.append(field);
                sb.append(this.dbMetaHelper.getObjectEndQualifier());
                sb.append(" ");
                if (isValueNull) {
                    sb.append("is null or ");
                    sb.append(this.dbMetaHelper.getObjectStartQualifier());
                    sb.append(field);
                    sb.append(this.dbMetaHelper.getObjectEndQualifier());
                    sb.append("=''");
                } else {
                    sb.append(type);
                    sb.append(" :");
                    sb.append(field);
                }

                sb.append(")");
            }
        } else {
            return new ListJsonResult("内部错误：非法的数据表名称");
        }
    }

    @RequestMapping(
        params = {"uniqueValidate"}
    )
    @ResponseBody
    public JsonResult uniqueValidate(@RequestParam(value = "t",required = true) String tablename, @RequestParam(value = "c",required = true) String columnname, @RequestParam(value = "v",required = true) String columnvalue, String id) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            String colName = null;
            Iterator var7 = infos.iterator();

            TableColumnInfo info;
            while(var7.hasNext()) {
                info = (TableColumnInfo)var7.next();
                if (info.getName().equalsIgnoreCase(columnname)) {
                    colName = info.getName();
                    break;
                }
            }

            if (colName == null) {
                return new JsonResult("内部错误：当前数据表中没有指定的字段");
            } else {
                String sql = "";
                info = null;

                Map data;
                try {
                    sql = "select count(0) count from " + tablename + " t where t." + this.dbMetaHelper.getObjectStartQualifier() + colName + this.dbMetaHelper.getObjectEndQualifier() + "= ?";
                    if (id != null && !"null".equals(id)) {
                        sql = sql + " and t.id <> ?";
                        data = this.commonService.findOneForJdbc(sql, new Object[]{columnvalue, id});
                    } else {
                        data = this.commonService.findOneForJdbc(sql, new Object[]{columnvalue});
                    }
                } catch (Exception var10) {
                    this.logger.error("获取表数据行数时出错", var10);
                    return new JsonResult("查询数据出错了，信息：" + var10.getMessage());
                }

                return new JsonResult(data.get("count"));
            }
        } else {
            return new JsonResult("内部错误：非法的数据表名称");
        }
    }

    @RequestMapping(
        params = {"batchDelete"}
    )
    @ResponseBody
    public JsonResult batchDelete(@RequestParam(value = "t",required = true) String tablename, String[] id, String[] ID, String[] ids, @RequestParam(value = "id[]",required = false) String[] idTraditional, @RequestParam(value = "ID[]",required = false) String[] IDTraditional, @RequestParam(value = "ids[]",required = false) String[] idsTraditional) {
        id = id != null && id.length != 0 ? id : ID;
        id = id != null && id.length != 0 ? id : idTraditional;
        id = id != null && id.length != 0 ? id : IDTraditional;
        id = id != null && id.length != 0 ? id : ids;
        id = id != null && id.length != 0 ? id : idsTraditional;
        if (id != null && id.length != 0) {
            List<String> idList = new ArrayList();
            String[] var9 = id;
            int var10 = id.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                String sid = var9[var11];
                if (sid != null && !"".equals(sid)) {
                    String[] subs = sid.split(",");
                    String[] var14 = subs;
                    int var15 = subs.length;

                    for(int var16 = 0; var16 < var15; ++var16) {
                        String sub = var14[var16];
                        if (sub != null && !"".equals(sub)) {
                            idList.add(sub);
                        }
                    }
                }
            }

            return this.datatableService.batchDelete(tablename, (String[])((String[])idList.toArray()));
        } else {
            return new JsonResult("未指定数据行");
        }
    }

    @RequestMapping(
        params = {"getFieldsStatement"}
    )
    @ResponseBody
    public JsonResult getFieldsStatement(@RequestParam(value = "t",required = true) String tablename, @RequestParam(required = false) String alias, @RequestParam(required = false,defaultValue = "false") boolean upper, @RequestParam(required = false,defaultValue = "false") boolean escape, String prefix, String postfix, @RequestParam(required = false,defaultValue = "_") String fix) {
        List<TableColumnInfo> infos = this.dbMetaHelper.getTableColumnInfos(tablename);
        if (infos != null && infos.size() != 0) {
            String fields = "";
            String fieldQualifier = escape ? "\\\"" : "\"";

            TableColumnInfo cInfo;
            String fieldAlias;
            for(Iterator var11 = infos.iterator(); var11.hasNext(); fields = fields + (this.isStringEmpty(alias) ? "" : alias + ".") + cInfo.getName() + " " + fieldQualifier + fieldAlias + fieldQualifier + ",") {
                cInfo = (TableColumnInfo)var11.next();
                fieldAlias = upper ? cInfo.getName().toUpperCase() : cInfo.getName().toLowerCase();
                if (!this.isStringEmpty(prefix) && fieldAlias.startsWith(prefix)) {
                    fieldAlias = fieldAlias.substring(prefix.length());
                }

                if (!this.isStringEmpty(postfix) && fieldAlias.endsWith(postfix)) {
                    fieldAlias = fieldAlias.substring(0, fieldAlias.length() - postfix.length());
                }

                if (!this.isStringEmpty(fix)) {
                    fieldAlias = fieldAlias.replaceAll(fix, "");
                }
            }

            fields = fields.substring(0, fields.length() - 1);
            return new JsonResult(fields);
        } else {
            return new JsonResult("内部错误：非法的数据表名称");
        }
    }
}
