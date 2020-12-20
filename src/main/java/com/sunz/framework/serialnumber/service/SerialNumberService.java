//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.serialnumber.service;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.util.IDbMetaHelper;
import com.sunz.framework.query.IQueryService;
import com.sunz.framework.serialnumber.SerialNumber;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.jeecgframework.core.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SerialNumberService implements ISerialNumberService, ICacheRefreshable {
    @Autowired
    protected CommonService commonService;
    private static Map<String, Long> staticRuleMap = new HashMap();
    private static Map<String, SerialNumber> staticSerialMap = new HashMap();
    public static final String PartHolder_Fixed = "固定前缀";
    public static final String PartHolder_Dynamic = "动态前缀";
    public static final String PartHolder_Date = "日期";
    public static final String PartHolder_Serial = "流水号";
    IQueryService queryService;
    private IDbMetaHelper dbMetaHelper;
    private static Pattern patternJobid = Pattern.compile("(\\{\\s*jobid\\s*\\})|(:\\s*jobid\\W)");

    public SerialNumberService() {
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @Autowired
    public void setQueryService(IQueryService queryService) {
        this.queryService = queryService;
    }

    @Autowired
    public void setDbMetaHelper(IDbMetaHelper dbMetaHelper) {
        this.dbMetaHelper = dbMetaHelper;
    }

    @PostConstruct
    public void init() {
        List<SerialNumber> all = this.queryService.queryList("select * from T_S_SERIALNUMBER_INFO t", (Map)null, new PageParameter(), SerialNumber.class, "_serial_rule_all").list();
        Iterator var2 = all.iterator();

        while(var2.hasNext()) {
            SerialNumber info = (SerialNumber)var2.next();
            staticSerialMap.put(info.getKey(), info);
        }

    }

    private SerialNumber getUncachedInfo(final String rulekey) {
        return (SerialNumber)this.queryService.queryObject("select * from T_S_SERIALNUMBER_INFO t where " + this.dbMetaHelper.getObjectStartQualifier() + "key" + this.dbMetaHelper.getObjectEndQualifier() + "=:key", new HashMap<String, String>() {
            {
                this.put("key", rulekey);
            }
        }, SerialNumber.class, "_serial_rule_" + rulekey);
    }

    public String generateNumber(String rulekey, String userid, String jobid) throws Exception {
        String RuleStr = "";
        SerialNumber ruleInfo = this.getDefine(rulekey);
        String ruleOrderStr = ruleInfo.getRuleexpress();
        String[] ruleOrders = ruleOrderStr.split("\\+");

        for(int i = 0; i < ruleOrders.length; ++i) {
            String ruleOrder = ruleOrders[i].toString();
            String lsh;
            if ("固定前缀".equals(ruleOrder)) {
                lsh = ruleInfo.getFixedprefix();
                RuleStr = RuleStr + lsh;
            }

            if ("动态前缀".equals(ruleOrder)) {
                Map<String, Object> mapParam = new HashMap();
                mapParam.put("jobid", jobid);
                mapParam.put("userid", userid);
                String dynamicprefix = (String)this.queryService.queryObject(ruleInfo.getDynamicprefix(), mapParam, String.class, "_secial_dynamic_" + rulekey);
                RuleStr = RuleStr + dynamicprefix;
            }

            if ("日期".equals(ruleOrder)) {
                RuleStr = RuleStr + (new SimpleDateFormat(ruleInfo.getDatestyle())).format(new Date());
            }

            if ("流水号".equals(ruleOrder)) {
                lsh = this.getSerialnum(ruleInfo.getRulelength(), RuleStr, rulekey);
                RuleStr = RuleStr + lsh;
            }
        }

        return RuleStr;
    }

    public String getSerialnum(int roleLength, String RuleStr, String key) throws Exception {
        Long maxnum = 0L;
        synchronized(staticRuleMap) {
            maxnum = staticRuleMap.containsKey(key + RuleStr) ? (Long)staticRuleMap.get(key + RuleStr) + 1L : 0L;
            if (maxnum == 0L) {
                Map map = null;
                if (RuleStr.length() > 0) {
                    map = this.commonService.findOneForJdbc("select MAXNUM from T_S_SERIALNUMBER where RULEPRE= ? and SERIALNUMBERKEY= ?", new Object[]{RuleStr, key});
                } else {
                    map = this.commonService.findOneForJdbc("select MAXNUM from T_S_SERIALNUMBER where RULEPRE is null and SERIALNUMBERKEY= ?", new Object[]{key});
                }

                maxnum = map == null ? 1L : Long.parseLong(map.get("MAXNUM").toString()) + 1L;
            }

            if (maxnum == 1L) {
                this.commonService.executeSql("insert into T_S_SERIALNUMBER (RULEPRE,MAXNUM,SERIALNUMBERKEY) values (?,?,?)", new Object[]{RuleStr, maxnum, key});
            } else if (RuleStr.length() > 0) {
                this.commonService.executeSql("update T_S_SERIALNUMBER set MAXNUM= ? where RULEPRE = ? and SERIALNUMBERKEY= ?", new Object[]{maxnum, RuleStr, key});
            } else {
                this.commonService.executeSql("update T_S_SERIALNUMBER set MAXNUM= ? where RULEPRE is null and SERIALNUMBERKEY= ?", new Object[]{maxnum, key});
            }

            staticRuleMap.put(key + RuleStr, maxnum);
        }

        if (roleLength == 0) {
            return maxnum.toString();
        } else {
            int zlength = roleLength - RuleStr.length();
            if (zlength > 0 && zlength < String.valueOf(maxnum).length()) {
                throw new Exception("业务编号规则流水号已占满，请使用新的规则或重新定义规则");
            } else {
                return String.format("%0" + zlength + "d", maxnum);
            }
        }
    }

    public String dateStyle(String style) {
        Map<String, String> map = new HashMap();
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(1));
        int m = c.get(2) + 1;
        String mouth = String.valueOf(m);
        if (m < 10) {
            mouth = "0" + mouth;
        }

        int d = c.get(5);
        String day = String.valueOf(d);
        if (d < 10) {
            day = "0" + day;
        }

        map.put("yyyy", year);
        map.put("yyyymm", year + mouth);
        map.put("yyyymmdd", year + mouth + day);
        map.put("mmdd", mouth + day);
        return (String)map.get(style);
    }

    public boolean isJobIndependent(String rulekey) {
        SerialNumber ruleInfo = this.getDefine(rulekey);
        return ruleInfo.getRuleexpress().indexOf("动态前缀") < 0 || ruleInfo.getDynamicprefix() == null || "".equals(ruleInfo.getDynamicprefix()) || !patternJobid.matcher(ruleInfo.getDynamicprefix()).find();
    }

    public List<SerialNumber> getAllDefine() {
        return new ArrayList(staticSerialMap.values());
    }

    public SerialNumber getDefine(String rulekey) {
        SerialNumber ruleInfo = (SerialNumber)staticSerialMap.get(rulekey);
        if (ruleInfo == null) {
            ruleInfo = this.getUncachedInfo(rulekey);
            if (ruleInfo == null) {
                throw new RuntimeException("非法的业务编号规则");
            }

            staticSerialMap.put(rulekey, ruleInfo);
        }

        return ruleInfo;
    }

    public void refresh(String item) {
        if (item != null && !"".equals(item)) {
            staticSerialMap.remove(item);
        } else {
            staticSerialMap.clear();
            this.init();
        }

    }

    public String getCategory() {
        return "SerialNumberRule";
    }

    public String getDescription() {
        return "业务编号规则缓存";
    }
}
