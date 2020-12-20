//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.dict;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.WebRootSupport;
import com.sunz.framework.core.Config.ChangeHandler;
import com.sunz.framework.util.StringUtil;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class DictHelper extends WebRootSupport implements ICacheRefreshable {
    protected static final Logger logger = Logger.getLogger(DictHelper.class);
    private static Map<String, DictItem> dict;
    private static List<DictItem> all;
    public static final String Cood_Root = "all";
    private static NamedParameterJdbcTemplate jdbcTemplate;
    static final String sql = " select tt.id,tt.typecode  \"code\",tt.typename \"text\",tt.remark ,tt.orderindex , tt.typegroupid parentid,'t' origin from t_s_type tt ";
    static final String KEY_CONFIG_EXTENDSQL = "dictExtendSqls";
    static List<String> extendSqls = Config.getList("dictExtendSqls");
    private static String jsPath = "webpage/framework/resource/js/dict.js";
    private static Long updateTime = (new Date()).getTime();
    private static String Char_JSString;
    private static String Char_Splitor_Item;
    private static String Char_Splitor_Value;
    private static String template;

    public DictHelper() {
        Config.addChangeListener((key) -> {
            extendSqls = Config.getList("dictExtendSqls");
        });
    }

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate template) {
        jdbcTemplate = template;
    }

    @Autowired(
        required = false
    )
    @Qualifier("dictExtendSqls")
    public void setDictExtendSqls(List<String> dictExtendSqls) {
        extendSqls = dictExtendSqls;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    private static String getSql() {
        String s = "select ttemp.*,ttemp.orderindex \"order\" from (  select tt.id,tt.typecode  \"code\",tt.typename \"text\",tt.remark ,tt.orderindex , tt.typegroupid parentid,'t' origin from t_s_type tt ";
        if (extendSqls != null) {
            s = s + " union all select te.*,'e' from ( select '' a,'' b ,'' c ,'' d ,0 e ,'' f from t_s_type where 1=2";
            int i = 0;

            for(int len = extendSqls.size(); i < len; ++i) {
                s = s + " union all " + (String)extendSqls.get(i);
            }

            s = s + ") te";
        }

        s = s + ") ttemp order by parentid,orderindex asc ";
        return s;
    }

    public static void refresh() {
        List<DictItem> list = jdbcTemplate.query(getSql(), new BeanPropertyRowMapper(DictItem.class));
        all = new ArrayList(list.size());
        dict = new HashMap(list.size() * 2);
        DictItem root = new DictItem();
        root.setId("all");
        root.setCode("all");
        root.setText("所有字典项");
        add(root);
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            DictItem item = (DictItem)var2.next();
            add(item);
        }

        addComplete();
        generateJs();
    }

    public static void addComplete() {
        DictItem root = (DictItem)dict.get("all");
        Iterator var1 = all.iterator();

        while(true) {
            DictItem item;
            do {
                if (!var1.hasNext()) {
                    return;
                }

                item = (DictItem)var1.next();
            } while(item == root);

            if (null == item.getParentid() || "".equals(item.getParentid()) || !dict.containsKey(item.getParentid())) {
                item.setParentid("all");
            }

            DictItem pItem = (DictItem)dict.get(item.getParentid());
            if (pItem.getChildren() == null) {
                pItem.setChildren(new ArrayList());
            }

            if (!pItem.getChildren().contains(item)) {
                pItem.getChildren().add(item);
            }
        }
    }

    public static void add(DictItem item) {
        all.add(item);
        dict.put(item.getId(), item);
        dict.put(item.getCode(), item);
    }

    public static void add(String id, String code, String name, String remark, int order, String pid) {
        DictItem item = new DictItem();
        item.setCode(code);
        item.setId(id);
        item.setText(name);
        item.setOrder(order);
        item.setRemark(remark);
        item.setParentid(pid);
        add(item);
    }

    public static List<DictItem> getSubs(String pcode) {
        DictItem item = getItem(pcode);
        return item == null ? null : item.getChildren();
    }

    public static DictItem getItem(String code) {
        return (DictItem)dict.get(code);
    }

    public static String getText(String code) {
        DictItem item = getItem(code);
        return item == null ? null : item.getText();
    }

    public static List<DictItem> getAll() {
        return all;
    }

    public void refresh(String item) {
        refresh();
    }

    public String getCategory() {
        return "dict";
    }

    public String getDescription() {
        return "数据字典（注：考虑到扩展字典，不支持单项刷新)";
    }

    public static String getJsPath(boolean avoidCache) {
        return avoidCache ? jsPath + "?t=" + updateTime : jsPath;
    }

    private static void generateJs() {
        try {
            (new FileOutputStream(webRoot + jsPath)).write(getJsContent("all").getBytes("utf-8"));
            updateTime = (new Date()).getTime();
        } catch (Exception var1) {
            logger.warn("数据字典js生成出错了，此错误可能会使得那些要求了所有字典项的页面无法正常使用！，路径：" + webRoot, var1);
        }

    }

    private static void appendItem(StringBuilder sb, DictItem item) {
        sb.append(item.toXString(template));
        List<DictItem> subs = item.getChildren();
        if (subs != null) {
            Iterator var3 = subs.iterator();

            while(var3.hasNext()) {
                DictItem sub = (DictItem)var3.next();
                appendItem(sb, sub);
            }

        }
    }

    public static String getJsContent(String items) {
        StringBuilder sb = new StringBuilder("var D=window.D||new (function $d(){var a=this,b={},x=[];a.ROOT='all';a.all=function(){return x;};a.add=function(j,h,e,c,d){/* id code text pid order*/if($.isArray(j)){return $.map(j,function(s){return a.add(s);})} else if(Object.prototype.toString.call(j) === '[object Object]'){ var l=!b[j.id]&&!b[j.code],g=l?a.add(j.id,j.value||j.code,j.text,j.pid||j.parentid,j.index||j.order):j;if(l&&j.children)a.add(j.children); return g;} c=c||a.ROOT;var m=b[j]||b[h],l=m&&m.pid,g=b[h]=b[j]=$.extend(m||{},{text:e,value:h,id:j,pid:c,index:d});if(m){var p= b[l].children;p.splice(p.indexOf(m),1);}else{x.push(g);}var f=b[c]=b[c]||a.add(c,c,c);if(g==f){g.id!=a.ROOT && console.error('发现错误项（父项指向自己）',g)}else{var i=f.children=f.children||[];i.push(g);i.sort(function(o,q){return o.index-q.index})}return g};a.get=function(c,black){if(c&&!b[c])a.dynload(c);var def=[];if(black){if(typeof black=='string')def=[{value:null,text:black}];else if(typeof black=='object')def=[black]; else def=[{value:null,text:'所有'}]; } return def.concat(c?((b[c]||{}).children||[]):[]);};a.getItem=function(c,p){return c==null?null:(p?$.grep(a.get(p),function(i,d){return d.id==c||d.code==c;}):b[c]);};a.getText=function(c,p){return c==null?'':(a.getItem(c,p)||{}).text;};a.createNew=function(){return new $d();};a.dynload=function(pk,url){if(!pk)return;$.ajax(url||('framework/dict.do?xsubs&item='+pk.trim()),{cache:true,async:false,success:function(jr){if(jr.success){a.add(jr.data);}}});};})();");
        if (items != null && !"".equals(items)) {
            sb.append(Char_JSString);
            String[] dicts = StringUtil.parseToArray(items);
            String[] var3 = dicts;
            int var4 = dicts.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String dict = var3[var5];
                if (dict != null && !"".equals(dict)) {
                    dict = dict.trim();
                    DictItem item = getItem(dict);
                    if (item == null) {
                        logger.error("字典项【" + dict + "】未定义，可能影响页面功能，请检查相关配置");
                    } else {
                        appendItem(sb, item);
                    }
                }
            }

            sb.append(Char_JSString);
            sb.append(".split(");
            sb.append(Char_JSString);
            sb.append(Char_Splitor_Item);
            sb.append(Char_JSString);
            sb.append(").forEach(function(v,i){v&&D.add.apply(D,v.split(");
            sb.append(Char_JSString);
            sb.append(Char_Splitor_Value);
            sb.append(Char_JSString);
            sb.append("));});");
        }

        return sb.toString();
    }

    static {
        ChangeHandler h = (k) -> {
            if (StringUtil.isEmpty(k) || k.startsWith("dict.")) {
                Char_JSString = StringUtil.ifEmpty(Config.get("dict.jsStringChar"), "`");
                Char_Splitor_Item = StringUtil.ifEmpty(Config.get("dict.itemSplitor"), ";");
                Char_Splitor_Value = StringUtil.ifEmpty(Config.get("dict.valueSplitor"), "'");
                template = "%1$s" + Char_Splitor_Value + "%2$s" + Char_Splitor_Value + "%3$s" + Char_Splitor_Value + "%4$s" + Char_Splitor_Value + "%5$s" + Char_Splitor_Item;
            }

        };
        Config.addChangeListener(h);
        h.onChange((String)null);
    }
}
