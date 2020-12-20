//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.datatable.IDatatableService;
import com.sunz.framework.query.entity.ResultField;
import com.sunz.framework.query.entity.SearchField;
import com.sunz.framework.query.helper.SqlParser;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/framework/query"})
public class QueryController extends BaseController {
    IQueryService queryService;
    private IDbMetaHelper dbMetaHelper;
    private IDatatableService datatableService;

    public QueryController() {
    }

    @Autowired
    public void setQueryService(IQueryService queryService) {
        this.queryService = queryService;
    }

    @RequestMapping(
        params = {"object"}
    )
    @ResponseBody
    public JsonResult queryObject(@RequestParam(value = "k",required = false) String sqlkey, @RequestParam(value = "sqlid",required = false) String sqlid) {
        try {
            return new JsonResult(this.queryService.queryObject(sqlid, sqlkey, this.toMap()));
        } catch (Exception var4) {
            this.logger.error(var4.getMessage(), var4);
            return new JsonResult(var4.getMessage());
        }
    }

    @RequestMapping(
        params = {"search"}
    )
    @ResponseBody
    public ListJsonResult search(@RequestParam(value = "k",required = false) String sqlkey, @RequestParam(value = "sqlid",required = false) String sqlid, PageParameter page) {
        try {
            return this.queryService.queryList(sqlid, sqlkey, this.toMap(), page);
        } catch (Exception var5) {
            this.logger.error(var5.getMessage(), var5);
            return new ListJsonResult(var5.getMessage());
        }
    }

    @RequestMapping(
        params = {"update"}
    )
    @ResponseBody
    public JsonResult update(@RequestParam(value = "k",required = false) String sqlkey, @RequestParam(value = "sqlid",required = false) String sqlid) {
        try {
            return new JsonResult(this.queryService.update(sqlid, sqlkey, this.toMap()));
        } catch (Exception var4) {
            this.logger.error(var4.getMessage(), var4);
            return new JsonResult(var4.getMessage());
        }
    }

    @RequestMapping(
        params = {"count"}
    )
    @ResponseBody
    public JsonResult count(@RequestParam(value = "k",required = false) String sqlkey, @RequestParam(value = "sqlid",required = false) String sqlid) {
        try {
            return new JsonResult(this.queryService.queryCount(sqlid, sqlkey, this.toMap()));
        } catch (Exception var4) {
            this.logger.error(var4.getMessage(), var4);
            return new JsonResult(var4.getMessage());
        }
    }

    @RequestMapping(
        params = {"setQuerySql"}
    )
    @ResponseBody
    public JsonResult setQuerySql(@RequestParam(value = "queryid",required = true) String id, @RequestParam(required = true) String sqlid, @RequestParam(defaultValue = "false") boolean immedite, HttpServletRequest request) {
        if (id != null && !"".equals(id)) {
            if (sqlid != null && !"".equals(sqlid)) {
                this.commonService.executeSql("DELETE FROM T_S_QUERY_SEARCHFIELD WHERE QUERYID=?", new Object[]{id});
                this.commonService.executeSql("DELETE FROM T_S_QUERY_RESULTFIELD WHERE QUERYID=?", new Object[]{id});
                String sql = SqlParser.getSql(sqlid, (String)null);
                List<String> sFields = SqlParser.parseForSearch(sql);
                List<Object> rFields = new ArrayList();
                ListJsonResult sample = this.search((String)null, sqlid, new PageParameter(0, 1, 1));
                if (sample.isSuccess()) {
                    List list = sample.list();
                    if (list.size() > 0) {
                        Map mSample = (Map)list.get(0);
                        Iterator iter = mSample.keySet().iterator();

                        while(iter.hasNext()) {
                            rFields.add(iter.next());
                        }
                    }
                }

                Object sResult = sFields;
                Object rResult = rFields;
                if (immedite) {
                    List<Object> osFields = new ArrayList();
                    List<Object> orFields = new ArrayList();

                    int i;
                    String sf;
                    for(i = 0; i < sFields.size(); ++i) {
                        sf = (String)sFields.get(i);
                        SearchField field = new SearchField();
                        field.setName(sf);
                        field.setTitle(sf);
                        field.setQueryid(id);
                        field.setInputType("textbox");
                        field.setOrderIndex(i + 1);
                        this.commonService.save(field);
                        osFields.add(field);
                    }

                    for(i = 0; i < rFields.size(); ++i) {
                        sf = (String)rFields.get(i);
                        ResultField field = new ResultField();
                        field.setName(sf);
                        field.setTitle(sf);
                        field.setQueryid(id);
                        field.setOrderIndex(i + 1);
                        this.commonService.save(field);
                        orFields.add(field);
                    }

                    sResult = osFields;
                    rResult = orFields;
                }

                Map<String, Object> result = new HashMap();
                result.put("search", sResult);
                result.put("result", rResult);
                return new JsonResult(result);
            } else {
                return new JsonResult("内部错误：sql未指定");
            }
        } else {
            return new JsonResult("内部错误：查询未指定");
        }
    }

    @RequestMapping(
        params = {"delete"}
    )
    @Transactional
    @ResponseBody
    public JsonResult delete(@RequestParam(required = true) String id) {
        if (id != null && !"".equals(id)) {
            this.commonService.executeSql("DELETE FROM T_S_QUERY_RESOURCERESTRICT  WHERE RESOURCEID IN ( SELECT ID FROM T_S_QUERY_SEARCHFIELD WHERE QUERYID=? UNION SELECT ID FROM T_S_QUERY_RESULTFIELD WHERE QUERYID=? UNION SELECT ID FROM T_S_QUERY_OPERATION WHERE QUERYID=?)", new Object[]{id, id, id});
            this.commonService.executeSql("DELETE FROM T_S_QUERY_SEARCHFIELD WHERE QUERYID=?", new Object[]{id});
            this.commonService.executeSql("DELETE FROM T_S_QUERY_RESULTFIELD WHERE QUERYID=?", new Object[]{id});
            this.commonService.executeSql("DELETE FROM T_S_QUERY_OPERATION WHERE QUERYID=?", new Object[]{id});
            this.commonService.executeSql("DELETE FROM T_S_QUERY WHERE ID=?", new Object[]{id});
            return new JsonResult(id);
        } else {
            return new JsonResult("内部错误：查询未指定");
        }
    }

    @RequestMapping(
        params = {"setting"}
    )
    public String setting() {
        return "framework/query/setting";
    }

    @RequestMapping(
        params = {"sqlSetting"}
    )
    public String sqlSetting() {
        return "framework/query/sqlSetting";
    }

    @RequestMapping(
        params = {"goQuery"}
    )
    public ModelAndView goQuery(@RequestParam(required = false) String key, @RequestParam(required = false) String k) {
        TSUser user = this.getLoginUser();
        if (user == null) {
            user = this.getLoginSupport().simulateGuestUser();
        }

        ModelAndView vm = new ModelAndView();
        vm.setViewName("framework/query/query");
        if (key == null || "".equals(key)) {
            key = k;
        }

        if (key != null && !"".equals(key)) {
            vm.addObject("queryDef", this.queryService.getUserQuery(key, user));
            return vm;
        } else {
            throw new RuntimeException("查询未指定");
        }
    }

    @RequestMapping(
        params = {"refreshSql"}
    )
    @ResponseBody
    public JsonResult refreshSql(String sqlid, String k) {
        if (k == null && sqlid == null) {
            SqlParser.refreshAll();
        } else {
            if (k != null) {
                SqlParser.refreshItem(k);
            }

            if (sqlid != null) {
                SqlParser.refreshItem(sqlid);
            }
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"testSql"}
    )
    public ModelAndView testSql(String sqlid, String k) {
        ModelAndView mv = new ModelAndView("framework/query/sqlSettingTest");
        if (sqlid != null || k != null) {
            String sql = SqlParser.getSql(sqlid, k);
            List<String> uFields = new ArrayList(new HashSet(SqlParser.parseForSearch(sql)));
            List<String> fFields = new ArrayList();
            Map map = this.toMap();

            for(int i = 0; i < uFields.size(); ++i) {
                if (!map.containsKey(uFields.get(i))) {
                    fFields.add(uFields.get(i));
                }
            }

            mv.addObject("sFields", fFields);
        }

        return mv;
    }

    @RequestMapping(
        params = {"debug"}
    )
    @ResponseBody
    public JsonResult debug(String sqlid, String k) {
        Map sqlParam = this.toMap();
        String sql = SqlParser.getSql(sqlid, k);
        sql = SqlParser.parseFreemarkerSql(sql, sqlParam, this.isStringEmpty(sqlid) ? k : sqlid);
        Map params = SqlParser.getParam(sql, this.toMap());
        Map map = new HashMap();
        map.put("sql", sql);
        map.put("params", params);
        return new JsonResult(map);
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
        params = {"exportSql"}
    )
    public void exportSql(String fileName, PageParameter page, HttpServletResponse response) {
        page.setTotal(1);
        ListJsonResult jr = this.search("sqlSetting", (String)null, page);
        String json = JsonHelper.toJSONString(jr.list());

        try {
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(this.isStringEmpty(fileName) ? (jr.list().size() == 1 ? (String)((Map)jr.list().get(0)).get("name") : "SQL定义导出") + ".sdf" : fileName, "UTF-8"));
            PrintWriter w = response.getWriter();
            w.print(json);
            w.flush();
        } catch (Exception var7) {
            this.logger.error("导出SQL定义到文件出错", var7);
        }

    }

    @RequestMapping(
        params = {"importSql"}
    )
    @ResponseBody
    public JsonResult importSql(MultipartFile file) {
        try {
            Collection<HashMap> array = JsonHelper.toCollection(new String(file.getBytes(), "UTF-8"), HashMap.class);
            List<String> success = new ArrayList();
            List<String> err = new ArrayList();
            Iterator var5 = array.iterator();

            while(var5.hasNext()) {
                Object o = var5.next();
                Map<String, Object> m = (Map)o;
                if (!m.containsKey("key") && !m.containsKey("KEY")) {
                    m.put("KEY", m.containsKey("code") ? m.get("code") : m.get("CODE"));
                }

                if (!m.containsKey("description") && !m.containsKey("DESCRIPTION")) {
                    m.put("DESCRIPTION", m.containsKey("name") ? m.get("name") : m.get("NAME"));
                }

                JsonResult jr = this.datatableService.add("T_S_SQLSTATEMENT", m);
                String k = (String)m.get("key");
                if (this.isStringEmpty(k)) {
                    k = (String)m.get("KEY");
                }

                if (jr.isSuccess()) {
                    success.add(k);
                } else {
                    err.add(k);
                }
            }

            Map result = new HashMap();
            result.put("success", success);
            result.put("err", err);
            return new JsonResult(result);
        } catch (Exception var10) {
            this.logger.error("导入SQL定义出错", var10);
            return JsonResult.Fail;
        }
    }

    private Map export(String id, String k) {
        if (this.isStringEmpty(id) && this.isStringEmpty(k)) {
            throw new RuntimeException("未指定查询");
        } else {
            Map<String, Object> param = this.toMap();
            String clause = this.isStringEmpty(id) ? this.dbMetaHelper.getObjectStartQualifier() + "key" + this.dbMetaHelper.getObjectEndQualifier() + "=:k" : "id=:id";
            ListJsonResult jr = this.datatableService.query("T_S_QUERY", clause, (String)null, param, (PageParameter)null, false);
            if (jr.isSuccess()) {
                Iterator var6 = jr.list().iterator();
                if (var6.hasNext()) {
                    Object o = var6.next();
                    Map<String, Object> query = (Map)o;
                    query.put("searchFields", this.queryService.queryList("select * from t_s_query_searchField where queryid=:ID", query, (PageParameter)null, Map.class, "_sys_query_export_searchFields").list());
                    query.put("resultFields", this.queryService.queryList("select * from t_s_query_resultField where queryid=:ID", query, (PageParameter)null, Map.class, "_sys_query_export_resultFields").list());
                    query.put("operations", this.queryService.queryList("select * from t_s_query_operation where queryid=:ID", query, (PageParameter)null, Map.class, "_sys_query_export_operations").list());
                    query.put("restricts", this.queryService.queryList("select * from t_s_query_resourcerestrict where target in ( select id from t_s_query_searchField where queryid=:ID  union all select id from t_s_query_resultField where queryid=:ID  union all select id from t_s_query_operation where queryid=:ID )", query, (PageParameter)null, Map.class, "_sys_query_export_restricts").list());
                    String sqlid = (String)query.get("SQLID");
                    if (!this.isStringEmpty(sqlid)) {
                        query.put("sqlstatement", new Object[]{this.datatableService.getById("T_S_SQLSTATEMENT", sqlid, false).getData()});
                    }

                    return query;
                }
            }

            return null;
        }
    }

    @RequestMapping(
        params = {"exportToFile"}
    )
    public void exportToFile(String id, String k, String fileName, HttpServletResponse response) {
        Map query = this.export(id, k);
        String json = JsonHelper.toJSONString(query);

        try {
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(this.isStringEmpty(fileName) ? query.get("DESCRIPTION") + ".qdf" : fileName, "UTF-8"));
            PrintWriter w = response.getWriter();
            w.print(json);
            w.flush();
        } catch (Exception var8) {
            this.logger.error("导出查询定义到文件出错", var8);
        }

    }

    @RequestMapping(
        params = {"exportToJson"}
    )
    @ResponseBody
    public JsonResult exportToJson(String id, String k) {
        return new JsonResult(this.export(id, k));
    }

    private void importSlave(Map query, String field, String tableName) {
        Object fValue = query.remove(field);
        if (fValue != null) {
            Collection<Map> array = JsonHelper.json2Collection(fValue, Map.class);
            if (array != null && !array.isEmpty()) {
                Iterator var6 = array.iterator();

                JsonResult jr;
                do {
                    if (!var6.hasNext()) {
                        return;
                    }

                    Object o = var6.next();
                    jr = this.datatableService.add(tableName, (Map)o);
                } while(jr.isSuccess());

                throw new RuntimeException(tableName + "表导入出错：" + jr.getMsg());
            }
        }
    }

    @Transactional
    private void importFromMap(Map query) {
        JsonResult jr = this.datatableService.add("T_S_QUERY", query);
        if (!jr.isSuccess()) {
            throw new RuntimeException("Query表导入出错：" + jr.getMsg());
        } else {
            this.importSlave(query, "searchFields", "T_S_QUERY_SEARCHFIELD");
            this.importSlave(query, "resultFields", "T_S_QUERY_RESULTFIELD");
            this.importSlave(query, "operations", "T_S_QUERY_OPERATION");
            this.importSlave(query, "restricts", "T_S_QUERY_RESOURCERESTRICT");
            this.importSlave(query, "sqlstatement", "T_S_SQLSTATEMENT");
        }
    }

    @RequestMapping(
        params = {"importFromJson"}
    )
    @ResponseBody
    public JsonResult importFromJson(String json) {
        Map query = null;

        try {
            query = (Map)JsonHelper.toBean(json, HashMap.class);
        } catch (Exception var4) {
            return new JsonResult("非正确的查询定义文件");
        }

        this.importFromMap(query);
        return new JsonResult(query);
    }

    @RequestMapping(
        params = {"importFromFile"}
    )
    @ResponseBody
    public JsonResult importFromFile(MultipartFile file) {
        try {
            return this.importFromJson(new String(file.getBytes(), "UTF-8"));
        } catch (Exception var3) {
            this.logger.error("从文件导入查询定义出错", var3);
            return JsonResult.Fail;
        }
    }
}
