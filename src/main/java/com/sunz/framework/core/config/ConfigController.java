//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.config;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping({"framework/config"})
@Controller
public class ConfigController extends BaseController {
    private static long expiresTime = (new Date()).getTime() + -1702967296L;

    public ConfigController() {
    }

    @RequestMapping(
        params = {"get"}
    )
    @ResponseBody
    public JsonResult get(@RequestParam("get") String k) {
        ConfigDefination c = Config.getDefination(k);
        if (c == null) {
            return new JsonResult("指定的配置项不存在");
        } else {
            return !c.isForeground() ? new JsonResult("无权访问指定的配置项") : new JsonResult(Config.get(k));
        }
    }

    @RequestMapping(
        params = {"set"}
    )
    @ResponseBody
    public JsonResult set(@RequestParam("set") String k, String value) {
        Config.set(k, value);
        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"js"}
    )
    public void js(String items, String groups, HttpServletResponse response) {
        if (items == null) {
            items = "";
        }

        if (groups != null) {
            String[] var4 = groups.split(",");
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String item = var4[var6];
                items = items + "," + Config.get(item);
            }
        }

        try {
            response.getWriter().write(Config.getJsContent(items.split(","), false));
            response.setDateHeader("expires", expiresTime);
            response.setContentType("text/javascript;charset=utf-8");
        } catch (IOException var8) {
            this.logger.error("获取配置js出错", var8);
        }

    }

    @RequestMapping(
        params = {"manager"}
    )
    public ModelAndView manager() {
        ModelAndView mv = new ModelAndView("framework/config/manager");
        return mv;
    }

    @RequestMapping(
        params = {"all"}
    )
    @ResponseBody
    public ListJsonResult all() {
        ListJsonResult jr = new ListJsonResult();
        Collection<ConfigDefination> list = Config.getAllDefinations();
        jr.setData(list);
        jr.setTotal(list.size());
        return jr;
    }

    @RequestMapping(
        params = {"getResources"}
    )
    public ModelAndView getResources() {
        ModelAndView mv = new ModelAndView("framework/config/resources");
        return mv;
    }
}
