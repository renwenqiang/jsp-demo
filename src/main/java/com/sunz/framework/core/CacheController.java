//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import com.sun.star.uno.RuntimeException;
import com.sunz.framework.message.IMessageService;
import com.sunz.framework.message.IOnMessageHandler;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping({"framework/cache"})
@Controller
@Lazy
public class CacheController extends BaseController implements ICacheRefreshable, ApplicationContextAware {
    private static Map<String, ICacheRefreshable> dict = new HashMap();
    private ApplicationContext applicationContext;
    private IMessageService mqService;
    private static final String CODE_MQ = "cache-refresh";
    private static String lastRefreshNumber;

    public CacheController() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void findCacheRefresh(ApplicationContext context) {
        Map<String, ICacheRefreshable> all = context.getBeansOfType(ICacheRefreshable.class);
        Iterator var3 = all.values().iterator();

        while(var3.hasNext()) {
            ICacheRefreshable cr = (ICacheRefreshable)var3.next();
            if (cr != null) {
                dict.put(cr.getCategory(), cr);
            }
        }

    }

    @PostConstruct
    public void init() {
        dict.clear();
        this.findCacheRefresh(ContextLoader.getCurrentWebApplicationContext());
        this.findCacheRefresh(this.applicationContext);
    }

    @RequestMapping(
        params = {"lastTime"}
    )
    @ResponseBody
    public JsonResult lastTime(String target, int mode, @RequestParam(defaultValue = "false") boolean recursively, @RequestParam(defaultValue = "true") boolean bubbling, @RequestParam(defaultValue = "false") boolean recordFiles) {
        if (target != null && !"".equals(target)) {
            FileCacheHelper helper = FileCacheHelper.getInstance(target);
            if (!helper.isValid()) {
                helper.setBubbling(bubbling);
                helper.setRecursively(recursively);
                helper.setRecordFiles(recordFiles);
                helper.watch();
                if (!helper.isValid()) {
                    return new JsonResult("无法对指定的目标进行监听，可能的原因是指定的目标不存在");
                }
            }

            return new JsonResult(mode == 0 ? helper.getTargetTime(target) : helper.getTimeMap(target, mode));
        } else {
            return new JsonResult("未指定目标");
        }
    }

    @RequestMapping(
        params = {"manager"}
    )
    public ModelAndView manager() {
        ModelAndView mv = new ModelAndView("framework/cache/manager");
        mv.addObject("all", dict.values());
        mv.addObject("clusterSupport", this.mqService != null);
        return mv;
    }

    @Autowired(
        required = false
    )
    public void setMqService(IMessageService mqService) {
        this.mqService = mqService;
    }

    private static void proxyRefresh(String type, String item) {
        ICacheRefreshable cr = (ICacheRefreshable)dict.get(type);
        if (cr == null) {
            throw new RuntimeException("内部错误：不存在指定类型的缓存");
        } else {
            if ("".equals(item)) {
                item = null;
            }

            try {
                cr.refresh(item);
            } catch (Exception var4) {
                throw new RuntimeException("缓存刷新出错啦，信息：" + var4.getMessage());
            }
        }
    }

    @RequestMapping(
        params = {"refresh"}
    )
    @ResponseBody
    public JsonResult refresh(String type, String item) {
        ICacheRefreshable cr = (ICacheRefreshable)dict.get(type);
        if (cr == null) {
            return new JsonResult("内部错误：不存在指定类型的缓存");
        } else {
            proxyRefresh(type, item);
            if (this.mqService != null) {
                lastRefreshNumber = "" + (new Date()).getTime();
                this.mqService.sendMessage("command", "cache-refresh", lastRefreshNumber, this.isStringEmpty(item) ? type : type + "," + item);
            }

            return JsonResult.Success;
        }
    }

    public void refresh(String item) {
        this.init();
    }

    public String getCategory() {
        return "Caches";
    }

    public String getDescription() {
        return "刷新缓存接口(几乎没有场景需要用到，简单化不支持指定刷新)";
    }

    @Component
    public static class RefreshEventListener implements IOnMessageHandler {
        public RefreshEventListener() {
        }

        public void onMessage(String topic, String code, String bid, byte[] msgContent) {
            if ("command".equals(topic) && "cache-refresh".equals(code) && (CacheController.lastRefreshNumber == null || !CacheController.lastRefreshNumber.equals(bid))) {
                String[] arr = (new String(msgContent, Charset.forName("utf-8"))).split(",", 2);
                CacheController.proxyRefresh(arr[0], arr.length == 2 ? arr[1] : null);
            }

        }
    }
}
