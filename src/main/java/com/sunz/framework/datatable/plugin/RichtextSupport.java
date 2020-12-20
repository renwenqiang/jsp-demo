//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable.plugin;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.Config.ChangeHandler;
import com.sunz.framework.datatable.event.EventHelper;
import com.sunz.framework.datatable.event.IBeforeAddHandler;
import com.sunz.framework.util.StringUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RichtextSupport {
    String Field_Name_BID = StringUtil.ifEmpty(Config.get("datatable.richtext.field.bid"), "bid_");
    String Field_Name_Data = StringUtil.ifEmpty(Config.get("datatable.richtext.field.data"), "data_");
    String Field_Name_Type = StringUtil.ifEmpty(Config.get("datatable.richtext.field.type"), "btype_");
    List<String> aspectTables;

    public RichtextSupport() {
        String key = "datatable.richtext.tables";
        ChangeHandler handler = (k) -> {
            if (k == null || key.equals(k)) {
                this.aspectTables = Arrays.asList(StringUtil.parseToArray(Config.get(key).toLowerCase()));
            }

        };
        Config.addChangeListener(handler);
        handler.onChange(key);
    }

    private boolean shouldAspect(String tableName) {
        return this.aspectTables.contains(tableName.toLowerCase());
    }

    @PostConstruct
    public void init() {
        IBeforeAddHandler intercept = (tableName, fieldValues) -> {
            if (this.shouldAspect(tableName)) {
                Iterator var3 = fieldValues.keySet().iterator();

                while(var3.hasNext()) {
                    String k = (String)var3.next();
                    if (this.Field_Name_Data.equalsIgnoreCase(k)) {
                        try {
                            fieldValues.put(k, URLDecoder.decode(new String(Base64.getDecoder().decode((String)fieldValues.get(k)), "utf-8"), "utf-8"));
                            break;
                        } catch (UnsupportedEncodingException var6) {
                            throw new RuntimeException("富文本解析出错", var6);
                        }
                    }
                }

                boolean hasBType = false;
                Iterator var8 = fieldValues.keySet().iterator();

                while(var8.hasNext()) {
                    String kx = (String)var8.next();
                    if (this.Field_Name_Type.equalsIgnoreCase(kx)) {
                        hasBType = true;
                        break;
                    }
                }

                if (!hasBType) {
                    fieldValues.put(this.Field_Name_Type, fieldValues.get("richtextType"));
                }
            }

        };
        EventHelper.registerEventHandler(intercept);
        // TODO 下面报错 注释掉了
//        EventHelper.registerEventHandler((tableName, id, fieldValues) -> {
//            intercept.onBeforeAdd(tableName, fieldValues);
//        });
    }
}
