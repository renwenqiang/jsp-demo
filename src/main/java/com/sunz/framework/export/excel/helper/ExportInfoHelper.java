//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.export.excel.ExportInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExportInfoHelper implements ICacheRefreshable {
    private static final Logger logger = Logger.getLogger(ExportInfoHelper.class);
    private static Map<String, ExportInfo> m_Dictionary;
    static NamedParameterJdbcTemplate jdbcTemplate;

    public ExportInfoHelper() {
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        ExportInfoHelper.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        refreshAll();
    }

    public static void refreshAll() {
        m_Dictionary = new HashMap();
        List infoList = jdbcTemplate.query("select * from T_S_ExcelExport", new BeanPropertyRowMapper(ExportInfo.class));
        Iterator var1 = infoList.iterator();

        while(var1.hasNext()) {
            Object o = var1.next();
            ExportInfo eInfo = (ExportInfo)o;
            m_Dictionary.put(eInfo.getKey(), eInfo);
        }

    }

    public static void refreshItem(String key) {
        m_Dictionary.remove(key);
    }

    public static ExportInfo getExportInfo(int id) {
        if (m_Dictionary.containsKey(id)) {
            return (ExportInfo)m_Dictionary.get(id);
        } else {
            try {
                ExportInfo eInfo = (ExportInfo)jdbcTemplate.getJdbcOperations().queryForObject("select * from T_S_ExcelExport where id=?", new Object[]{id}, new BeanPropertyRowMapper(ExportInfo.class));
                if (eInfo != null) {
                    m_Dictionary.put(eInfo.getKey(), eInfo);
                }

                return eInfo;
            } catch (Exception var2) {
                logger.error("获取导出配置信息时出错", var2);
                return null;
            }
        }
    }

    public void refresh(String item) {
        if (item != null) {
            refreshItem(item);
        } else {
            refreshAll();
        }

    }

    public String getCategory() {
        return "ExportConfig";
    }

    public String getDescription() {
        return "数据导出配置";
    }
}
