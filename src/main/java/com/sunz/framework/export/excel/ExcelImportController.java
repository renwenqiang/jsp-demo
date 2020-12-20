//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.GuidGenerator;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.datatable.IDatatableService;
import com.sunz.framework.dict.DictHelper;
import com.sunz.framework.dict.DictItem;
import com.sunz.framework.export.excel.helper.ExcelReader;
import com.sunz.framework.query.helper.SqlParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping({"framework/import"})
public class ExcelImportController extends BaseController {
    NamedParameterJdbcTemplate jdbcTemplate;
    IDatatableService datatableService;
    private List<Integer> Empty_List_Int = new ArrayList(0);
    private ExcelImportController.IMapConvertor emptyConvertor = new ExcelImportController.IMapConvertor() {
        public Object getValue(String value) {
            return value;
        }

        public Object getKey(String key) {
            return key;
        }
    };

    public ExcelImportController() {
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setDatatableService(IDatatableService datatableService) {
        this.datatableService = datatableService;
    }

    protected List<Integer> toIntList(String str) {
        if (str != null && !"".equals(str)) {
            String[] strs = str.split(",|;");
            List<Integer> list = new ArrayList();

            for(int i = 0; i < strs.length; ++i) {
                list.add(Integer.parseInt(strs[i]));
            }

            return list;
        } else {
            return this.Empty_List_Int;
        }
    }

    protected String trimQuotes(String str, boolean trim) {
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

    protected Map<Object, Object> getFieldMapping(String strMapping, ExcelImportController.IMapConvertor convertor) {
        LinkedHashMap<Object, Object> mResult = new LinkedHashMap();
        if (strMapping != null && !"".equals(strMapping)) {
            if (convertor == null) {
                convertor = this.emptyConvertor;
            }

            String[] list = strMapping.split(",");

            for(int i = 0; i < list.length; ++i) {
                String strMap = list[i];
                if (strMap != null && strMap.trim() != "") {
                    String[] keyValue = strMap.split(":");
                    String key = this.trimQuotes(keyValue[0], true);
                    String value = keyValue.length == 2 ? this.trimQuotes(keyValue[1], false) : key;
                    mResult.put(convertor.getKey(key), convertor.getValue(value));
                }
            }
        }

        return mResult;
    }

    @RequestMapping(
        params = {"preview"}
    )
    @ResponseBody
    public JsonResult preview(MultipartHttpServletRequest multiRequest, int start, int limit) {
        try {
            MultipartFile file = (MultipartFile)multiRequest.getFileMap().values().toArray()[0];
            ExcelReader reader = new ExcelReader(file.getInputStream(), ExcelReader.getExcelType(file.getOriginalFilename()));
            List<Object[]> data = reader.readToArrays(start, limit);
            return new ListJsonResult(data, reader.getRowCount());
        } catch (IOException var7) {
            this.logger.error("", var7);
            return JsonResult.Fail;
        }
    }

    @RequestMapping(
        params = {"import"}
    )
    @ResponseBody
    public JsonResult importExcel(MultipartHttpServletRequest multiRequest, @RequestParam(required = false) String impid, @RequestParam(required = false) String impcode, @RequestParam(required = false) String fieldMap, @RequestParam(required = false) String skipRows, @RequestParam(required = false) String skipColumns, @RequestParam(required = false) String dictFields, @RequestParam(required = false) String sqlid, @RequestParam(value = "t",required = false) String tableName, @RequestParam(required = false) String nullHolder) {
        try {
            String sqlInsert = null;
            Map m;
            if (impid != null && !"".equals(impid) || impcode != null && !"".equals(impcode)) {
                m = this.jdbcTemplate.getJdbcOperations().queryForMap("select fieldmapping \"1\",skiprows \"2\",skipcolumns \"3\",dictfields \"4\",sqlid \"5\",sqldef \"6\" from t_s_excelimport where impcode=? or id=?", new Object[]{impid, impcode});
                if (fieldMap == null || "".equals(fieldMap)) {
                    fieldMap = (String)m.get("1");
                }

                if (skipRows == null || "".equals(skipRows)) {
                    skipRows = (String)m.get("2");
                }

                if (skipColumns == null || "".equals(skipColumns)) {
                    skipColumns = (String)m.get("3");
                }

                if (dictFields == null || "".equals(dictFields)) {
                    dictFields = (String)m.get("4");
                }

                if (sqlid == null || "".equals(sqlid)) {
                    sqlid = (String)m.get("5");
                }

                sqlInsert = (String)m.get("6");
            }

            m = this.getFieldMapping(fieldMap, new ExcelImportController.IMapConvertor() {
                public Object getValue(String value) {
                    return value;
                }

                public Object getKey(String key) {
                    return Integer.parseInt(key);
                }
            });
            if (sqlInsert == null || "".equals(sqlInsert)) {
                sqlInsert = SqlParser.getSql((String)null, sqlid);
            }

            boolean isPre = sqlInsert != null && !"".equals(sqlInsert);
            boolean isTable = tableName != null && !"".equals(tableName);
            if (!isPre && !isTable) {
                return new JsonResult("内部错误：未指定导入逻辑");
            } else {
                MultipartFile file = (MultipartFile)multiRequest.getFileMap().values().toArray()[0];
                ExcelReader reader = new ExcelReader(file.getInputStream(), ExcelReader.getExcelType(file.getOriginalFilename()));
                int count = reader.getRowCount();
                if (count == 0) {
                    return new JsonResult(0);
                } else {
                    List<Integer> ignoreColumns = this.toIntList(skipColumns);
                    List<Integer> ignoreRows = this.toIntList(skipRows);
                    reader.setFieldMapping(m);
                    reader.setIgnoreColumns(ignoreColumns);
                    reader.setIgnoreRows(ignoreRows);
                    reader.setNullHolder(nullHolder);
                    Map dictMap = this.getFieldMapping(dictFields, (ExcelImportController.IMapConvertor)null);
                    Map<String, Map<String, String>> dictHelper = new HashMap();
                    Iterator var22 = dictMap.keySet().iterator();

                    while(true) {
                        String field;
                        String dict;
                        Iterator var28;
                        do {
                            if (!var22.hasNext()) {
                                int bufer = 100;
                                int start = 1;
                              int realCount = 0;// TODO 报错 注释掉了
//                                int realCount = 0;
                                final List<Map<String, Object>> errList = new ArrayList();

                                final ArrayList successList;
                                for(successList = new ArrayList(); start < count; start += bufer) {
                                    List<Map<String, Object>> datas = reader.readToMaps(start, bufer);
                                    var28 = datas.iterator();

                                    while(var28.hasNext()) {
                                        Map<String, Object> d = (Map)var28.next();
                                        realCount++;
                                        Iterator var30 = dictMap.keySet().iterator();

                                        while(var30.hasNext()) {
                                            Object of = var30.next();
//                                            String field = (String)of; // TODO 报错 注释掉了
                                            field = (String)of;
                                            Object ov = d.get(field);
                                            if (ov != null) {
                                                d.put(field, ((Map)dictHelper.get(field)).get(ov));
                                            }
                                        }

                                        if (!d.containsKey("ID")) {
                                            d.put("ID", GuidGenerator.getGuid());
                                        }

                                        String id = d.get("ID").toString();
                                        if (isTable) {
                                            JsonResult jr = this.datatableService.add(tableName, d);
                                            if (jr.isSuccess()) {
                                                successList.add(id);
                                            } else {
                                                this.logger.warn("从Excel文件向【" + tableName + "】导入一条记录出错:" + jr.getMsg());
                                                errList.add(d);
                                            }
                                        } else {
                                            try {
                                                this.jdbcTemplate.update(SqlParser.parseFreemarkerSql(sqlInsert, d, "imp_" + (impid == null ? impcode : impid)), d);
                                                successList.add(id);
                                            } catch (Exception var34) {
                                                this.logger.warn("从Excel文件导入一条记录出错", var34);
                                                errList.add(d);
                                            }
                                        }
                                    }
                                }

                                int finalRealCount = realCount;//TODO 加的 不然返回报错
                                return new JsonResult(new Object() {
                                    public int getTotal() {
                                        return finalRealCount;
                                    }

                                    public List getSuccessList() {
                                        return successList;
                                    }

                                    public List getErrorList() {
                                        return errList;
                                    }
                                });
                            }

                            Object of = var22.next();
                            field = (String)of;
                            dict = (String)dictMap.get(of);
                        } while(dict == null);

                        List<DictItem> items = DictHelper.getSubs((String)dictMap.get(field));
                        Map<String, String> mDict = new HashMap();
                        if (items != null) {
                            var28 = items.iterator();

                            while(var28.hasNext()) {
                                DictItem item = (DictItem)var28.next();
                                mDict.put(item.getText(), item.getCode());
                            }
                        }

                        dictHelper.put(field, mDict);
                    }
                }
            }
        } catch (IOException var35) {
            this.logger.error("", var35);
            return JsonResult.Fail;
        }
    }

    protected interface IMapConvertor {
        Object getKey(String var1);

        Object getValue(String var1);
    }
}
