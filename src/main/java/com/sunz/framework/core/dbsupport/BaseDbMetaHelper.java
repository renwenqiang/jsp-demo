//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.dbsupport;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.util.IDbMetaHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class BaseDbMetaHelper implements IDbMetaHelper, ICacheRefreshable {
    protected Logger logger = Logger.getLogger(this.getClass());
    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected RowMapper<TableColumnInfo> tableColumnRowMapper = new BeanPropertyRowMapper(TableColumnInfo.class);
    protected Map<String, Integer> dataTypeMapping;
    protected static Map<String, List<TableColumnInfo>> dictColumnInfos = new HashMap();
    private List<BaseDbMetaHelper.IMetaChangeHandler> changeHandlers;

    public BaseDbMetaHelper() {
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.init();
    }

    @Autowired(
        required = false
    )
    @Qualifier("exDbdataTypeMapping")
    public void setExDbdataTypeMapping(Map<String, Integer> exMapping) {
        if (this.dataTypeMapping != null) {
            this.dataTypeMapping.putAll(exMapping);
        }
    }

    public int getCommonDatatype(String rawtype) {
        if (this.dataTypeMapping.containsKey(rawtype)) {
            return (Integer)this.dataTypeMapping.get(rawtype);
        } else {
            throw new RuntimeException("不可识别的数据库数据类型" + rawtype);
        }
    }

    public void init() {
    }

    public void refresh(String tablename) {
        if (tablename != null) {
            dictColumnInfos.remove(tablename);
        } else {
            dictColumnInfos.clear();
        }

        if (this.changeHandlers != null) {
            Iterator var2 = this.changeHandlers.iterator();

            while(var2.hasNext()) {
                BaseDbMetaHelper.IMetaChangeHandler handler = (BaseDbMetaHelper.IMetaChangeHandler)var2.next();
                handler.onChanged(this);
            }
        }

    }

    public String getDescription() {
        return "数据库结构数据缓存(数据表字段信息)";
    }

    public String getCategory() {
        return "DBMeta";
    }

    @Autowired(
        required = false
    )
    public void setChangeHandlers(List<BaseDbMetaHelper.IMetaChangeHandler> changeHandlers) {
        this.changeHandlers = changeHandlers;
    }

    public void addMetaChangeHandler(BaseDbMetaHelper.IMetaChangeHandler handler) {
        if (this.changeHandlers == null) {
            this.changeHandlers = new ArrayList();
        }

        this.changeHandlers.add(handler);
    }

    public interface IMetaChangeHandler {
        void onChanged(IDbMetaHelper var1);
    }
}
