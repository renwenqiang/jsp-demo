//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.util.IDataRowHandler;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.core.util.IPagingQueryService;
import com.sunz.framework.export.excel.helper.ExcelExporter;
import com.sunz.framework.export.excel.helper.ExportInfoHelper;
import com.sunz.framework.query.IQueryService;
import com.sunz.framework.query.helper.SqlParser;
import com.sunz.framework.util.StringUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller
@RequestMapping({"framework/export"})
public class ExcelExportController extends BaseController {
    IQueryService queryService;
    private IDbMetaHelper dbMetaHelper;
    @Autowired
    IPagingQueryService pagingQueryService;

    public ExcelExportController() {
    }

    @Autowired
    public void setQueryService(IQueryService queryService) {
        this.queryService = queryService;
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        this.dbMetaHelper = dbMetaHelper;
    }

    public void setPagingQueryService(IPagingQueryService pagingQueryService) {
        this.pagingQueryService = pagingQueryService;
    }

    @RequestMapping(
        params = {"searchExcel"}
    )
    public void searchExcel(@RequestParam(value = "k",required = false) String sqlkey, @RequestParam(value = "sqlid",required = false) String sqlid, @RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "-1") int limit, @RequestParam(required = false,defaultValue = "导出") String filename, @RequestParam(required = false,defaultValue = "") String[] fieldMap, @RequestParam(required = false,defaultValue = "") String[] dictFields, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isStringEmpty(sqlkey) && this.isStringEmpty(sqlid)) {
            throw new RuntimeException("内部错误：未指定导出");
        } else {
            ExcelExporter helper = new ExcelExporter();
            helper.setNameMapping(ExportInfo.getFieldMapping(fieldMap.length == 1 && fieldMap[0] != null ? fieldMap[0].split(",") : fieldMap));
            helper.setDictFields(Arrays.asList(dictFields.length == 1 && dictFields[0] != null ? dictFields[0].split(",") : dictFields));
            filename = null != filename && !"".equals(filename) ? filename : sqlkey;
            helper.setSheetName(filename);
            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "utf-8") + ".xls");
            this.toExcel(sqlid, sqlkey, start, limit, helper, response.getOutputStream());
        }
    }

    private void toExcel(String sqlid, String sqlkey, int start, int limit, final ExcelExporter helper, OutputStream stream) throws IOException {
        Map param = this.toMap();
        String cacheKey = StringUtil.ifEmpty(sqlid, sqlkey);
        String sql = SqlParser.getSql(sqlid, sqlkey);
        sql = SqlParser.parseFreemarkerSql(sql, param, cacheKey);
        helper.setCurrentSheet(0);
        this.pagingQueryService.query(sql, param, start, limit, new IDataRowHandler() {
            List<String> fields;
            Map<String, Object> buffer;
            int count;

            public void handleRow(ResultSet rs) {
                try {
                    if (this.fields == null) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        this.count = rsmd.getColumnCount();
                        this.buffer = new HashMap(this.count);
                        this.fields = new ArrayList(this.count + 1);
                        this.fields.add("");

                        for(int i = 1; i <= this.count; ++i) {
                            this.fields.add(StringUtil.ifEmpty(rsmd.getColumnLabel(i), rsmd.getColumnName(i)));
                        }
                    }

                    for(int ix = 1; ix <= this.count; ++ix) {
                        this.buffer.put(this.fields.get(ix), rs.getObject(ix));
                    }

                    helper.input(this.buffer);
                } catch (SQLException var4) {
                    throw new RuntimeException("数据行导出Excel时出错", var4);
                }
            }
        });
        helper.write(stream);
        helper.dispose();
    }

    @RequestMapping(
        params = {"excel"}
    )
    public void excel(@RequestParam(required = false) String key, @RequestParam(required = false) String k, String id, @RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "-1") int limit, String filename, String fieldMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isStringEmpty(key)) {
            key = k;
        }

        if (this.isStringEmpty(key) && this.isStringEmpty(id)) {
            throw new RuntimeException("内部错误：未指定导出");
        } else {
            Map m = new HashMap();
            m.put("key", key);
            m.put("id", id);
            ExportInfo eInfo = (ExportInfo)this.queryService.queryObject("select * from T_S_ExcelExport t where id=:id or " + this.dbMetaHelper.getObjectStartQualifier() + "key" + this.dbMetaHelper.getObjectEndQualifier() + "=:key", m, ExportInfo.class, "_excel_exportInfo_uniqued");
            String sqlid = eInfo.getSqlid();
            if (this.isStringEmpty(sqlid)) {
                throw new RuntimeException("内部错误：当前配置未指定sql语句");
            } else {
                ExcelExporter helper = new ExcelExporter();
                helper.setDictFields(eInfo.getDictFieldList());
                helper.setIgnoreColumns(eInfo.getIgnoreColumnList());
                helper.setIgnoreRows(eInfo.getIgnoreRowList());
                Map fieldMapping = eInfo.getFieldMappingMap();
                fieldMapping.putAll(ExportInfo.getFieldMapping(fieldMap));
                helper.setNameMapping(fieldMapping);
                helper.setStartColumn(eInfo.getStartColumn());
                helper.setStartRow(eInfo.getStartRow());
                filename = this.isStringEmpty(filename) ? eInfo.getDescription() : filename;
                helper.setSheetName(filename);
                response.setContentType("application/x-excel");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "utf-8") + ".xls");
                helper.setTemplate(eInfo.getTemplate());
                this.toExcel(sqlid, (String)null, start, limit, helper, response.getOutputStream());
            }
        }
    }

    @RequestMapping(
        params = {"refresh"}
    )
    @ResponseBody
    public JsonResult refresh(String key) {
        if (!this.isStringEmpty(key)) {
            ExportInfoHelper.refreshItem(key);
        } else {
            ExportInfoHelper.refreshAll();
        }

        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"setting"}
    )
    public String setting() {
        return "framework/export/setting";
    }

    @RequestMapping(
        params = {"delete"}
    )
    @Transactional
    @ResponseBody
    public JsonResult delete(@RequestParam(required = true) String id) {
        if (this.isStringEmpty(id)) {
            return new JsonResult("内部错误：查询未指定");
        } else {
            this.commonService.executeSql("DELETE FROM T_S_QUERY_RESULTFIELD WHERE QUERYID=?", new Object[]{id});
            this.commonService.executeSql("DELETE FROM T_S_EXCELEXPORT WHERE ID=?", new Object[]{id});
            return new JsonResult(id);
        }
    }

    @RequestMapping(
        params = {"deleteTemplate"}
    )
    @ResponseBody
    public JsonResult deleteTemplate(String id) {
        this.commonService.executeSql("UPDATE T_S_EXCELEXPORT SET TEMPLATE=NULL WHERE ID=?", new Object[]{id});
        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"uploadTemplate"}
    )
    @ResponseBody
    public JsonResult uploadTemplate(String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExportInfo expInfo = null;
        Map data = new HashMap();
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            Iterator iter = multiRequest.getFileNames();

            label32:
            while(true) {
                while(true) {
                    MultipartFile file;
                    do {
                        do {
                            if (!iter.hasNext()) {
                                break label32;
                            }

                            file = multiRequest.getFile((String)iter.next());
                        } while(file == null);
                    } while(file.getOriginalFilename().trim() == "");

                    InputStream in = file.getInputStream();
                    byte[] fb = toByteArray(in);
                    if (id != null && !"null".equals(id)) {
                        expInfo = (ExportInfo)this.commonService.get(ExportInfo.class, id);
                        expInfo.setTemplate(fb);
                        this.commonService.updateEntitie(expInfo);
                    } else {
                        expInfo = new ExportInfo();
                        expInfo.setTemplate(fb);
                        this.commonService.save(expInfo);
                    }
                }
            }
        }

        data.put("id", expInfo.getId());
        data.put("ID", expInfo.getId());
        return new JsonResult((String)null, data);
    }

    @RequestMapping(
        params = {"downloadTemplate"}
    )
    public void downloadTemplate(String id, @RequestParam(required = false) String fileName, HttpServletResponse response) {
        try {
            ExportInfo expInfo = (ExportInfo)this.commonService.findUniqueByProperty(ExportInfo.class, "id", id);
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String fname = this.isStringEmpty(fileName) ? expInfo.getDescription() + "-模板.xls" : (fileName.indexOf(".") > 0 ? fileName : fileName + ".xls");
            fname = URLEncoder.encode(fname, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fname);
            OutputStream os = response.getOutputStream();
            os.write(expInfo.getTemplate());
            os.flush();
            os.close();
        } catch (IOException var7) {
            this.logger.error("下载ExcelExport模板出错：", var7);
        }

    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        boolean var3 = false;

        int n;
        while((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        return out.toByteArray();
    }
}
