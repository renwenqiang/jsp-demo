//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.vue;

import com.sunz.framework.core.GuidGenerator;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher;
import com.sunz.framework.util.StringUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

public class VueHelper {
    protected static NamedParameterJdbcTemplate jdbcTemplate;
    private static Map<String, ComponentSetting> cache;

    public VueHelper() {
    }

    public static ComponentSetting getSetting(String codeOrPath) {
        return cache.containsKey(codeOrPath) ? (ComponentSetting)cache.get(codeOrPath) : addSetting(codeOrPath);
    }

    public static String getRealPath(String codeOrPath) {
        if (cache.containsKey(codeOrPath)) {
            return ((ComponentSetting)cache.get(codeOrPath)).getPath();
        } else {
            addSetting(codeOrPath);
            return codeOrPath;
        }
    }

    public static ComponentSetting addSetting(String path) {
        ComponentSetting s = new ComponentSetting();
        s.setCode(path);
        s.setPath(path);
        s.setRemark("【系统】首次访问自动添加");
        s.setAddTime(new Date());
        return addSetting(s);
    }

    public static ComponentSetting addSetting(ComponentSetting s) {
        if (StringUtil.isEmpty(s.getId())) {
            s.setId(GuidGenerator.getGuid());
        }

        cache.put(s.getCode(), s);
        cache.put(s.getPath(), s);
        return s;
    }

    @Component
    public static class innitor implements ICacheRefreshable {
        public innitor() {
        }

        @Autowired
        @Qualifier("namedParameterJdbcTemplate")
        public void setTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
            VueHelper.jdbcTemplate = jdbcTemplate;
            this.init();
        }

        @PostConstruct
        public void init() {
            VueHelper.cache = new HashMap();
            List<ComponentSetting> list = VueHelper.jdbcTemplate.query("select * from t_s_vuecomponent", JdbcTemplateRowMapperDispatcher.getRowMapper(ComponentSetting.class));
            Iterator var2 = list.iterator();

            while(var2.hasNext()) {
                ComponentSetting s = (ComponentSetting)var2.next();
                VueHelper.cache.put(s.getCode(), s);
                VueHelper.cache.put(s.getPath(), s);
            }

        }

        public void refresh(String item) {
            this.init();
        }

        public String getCategory() {
            return "vuecomponentsetting";
        }

        public String getDescription() {
            return "Vue组件映射和参数配置";
        }
    }
}
