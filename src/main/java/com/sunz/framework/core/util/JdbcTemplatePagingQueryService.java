//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.util;

import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service("jdbcTemplatePagingQueryService")
public class JdbcTemplatePagingQueryService implements IPagingQueryService {
    static final Logger logger = Logger.getLogger("JdbcTemplatePagingQueryService");
    @Autowired
    IPagingHelper pagingHelper;
    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    NamedParameterJdbcTemplate template;

    public JdbcTemplatePagingQueryService() {
    }

    public void setTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }

    public void setPagingHelper(IPagingHelper pagingHelper) {
        this.pagingHelper = pagingHelper;
    }

    public ListJsonResult query(String sql, Map param, PageParameter page, Class cls) {
        if (sql != null && !"".equals(sql)) {
            if (page == null) {
                page = new PageParameter();
            }

            List list = null;
            if (page.getLimit() != 0) {
                String realSql = sql;
                if (page.isPagingNeeded()) {
                    realSql = this.pagingHelper.getPagingSql(sql, page.getStart(), page.getLimit());
                }

                if (cls != null && !Map.class.equals(cls)) {
                    list = this.template.query(realSql, param, JdbcTemplateRowMapperDispatcher.getRowMapper(cls));
                } else {
                    list = this.template.queryForList(realSql, param);
                }
            }

            int total = page.getTotal();
            if (page.isTotalNeeded()) {
                total = (Integer)this.template.queryForObject(this.pagingHelper.getCountSql(sql), param, Integer.class);
            } else if (total <= 0) {
                total = list.size();
            }

            page.setTotal(total);
            return new ListJsonResult(list, total);
        } else {
            return new ListJsonResult("未指定sql语句");
        }
    }

    public ListJsonResult query(String sql, Map param, PageParameter page) {
        return this.query(sql, param, page, (Class)null);
    }

    public int queryCount(String sql, Map param) {
        return (Integer)this.template.queryForObject(this.pagingHelper.getCountSql(sql), param, Integer.class);
    }

    public void query(String sql, Map param, int start, int limit, IDataRowHandler extractor) {
        PageParameter page = new PageParameter(start, limit);
        if (page.isPagingNeeded()) {
            sql = this.pagingHelper.getPagingSql(sql, start, limit);
        }

        this.template.query(sql, param, (rs) -> {
            extractor.handleRow(rs);
        });
    }
}
