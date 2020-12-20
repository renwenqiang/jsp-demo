//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.mobile;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.WebRootSupport;
import com.sunz.framework.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ListTemplateGenerator extends WebRootSupport implements ICacheRefreshable {
    String SQL_Base = "select code_,type_,express_,css_ from t_s_mobile_listtemplate";
    JdbcTemplate jdbcTemplate;

    public ListTemplateGenerator() {
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            String sql = this.SQL_Base;
            String extendSQL = "select id,type_,express_,css_ from p_news_template";
            if (!StringUtil.isEmpty(extendSQL)) {
                sql = sql + " union all " + extendSQL;
            }

            String jsStart = StringUtil.ifEmpty(Config.get("mobile.listtemplatePre"), "$('<link rel=\"stylesheet\" type=\"text/css\" href=\"mobile/framework/list/templates.css\"></link>').appendTo(\"head\");var m=window.mlistTemplates={},css='';$.each([");
            String tpl = StringUtil.ifEmpty(Config.get("mobile.listtemplateItem"), "['%1$s','%2$s',%3$s],");
            String jsEnd = StringUtil.ifEmpty(Config.get("mobile.listtemplatePost"), ",null],function(i,a){if(!a)return;m[a[0]]=a[1]=='function'?eval('('+a[2]+')'):function(d){return $(tplReplace(a[2],d))}});window.define && define('mobile/framework/list/templates',[],function(){return m})");
            StringBuilder sbCss = new StringBuilder();
            StringBuilder sb = new StringBuilder(jsStart);
            this.jdbcTemplate.query(sql, (rs) -> {
                sb.append(String.format(tpl, rs.getString(1), rs.getString(2), rs.getString(3)));
                sbCss.append(rs.getString(4));
            });
            sb.append(jsEnd);
            File dir = new File(webRoot + "mobile/framework/list/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            (new FileOutputStream(webRoot + "mobile/framework/list/templates.css")).write(sbCss.toString().getBytes("utf-8"));
            (new FileOutputStream(webRoot + "mobile/framework/list/templates.js")).write(sb.toString().getBytes("utf-8"));
        } catch (Exception var9) {
            Logger.getLogger("ListTemplateGenerator").warn("移动端列表模板统一定义js生成出错了，前端列表（部分）可能无法显示！", var9);
        }

    }

    public void refresh(String item) {
        this.init();
    }

    public String getCategory() {
        return "MListTemplates";
    }

    public String getDescription() {
        return "移动端列表（List、SearchList）模板统一定义，因需要生成js文件，不支持单项刷新";
    }
}
