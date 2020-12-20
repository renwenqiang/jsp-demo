//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.vue;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"framework/vue"})
public class VueController extends BaseController {
    private final String KEY_RESOURCE = "vue.resources";

    public VueController() {
    }

    @RequestMapping(
        params = {"explore"}
    )
    public ModelAndView explore(@RequestParam("explore") String vue) {
        ModelAndView mv = new ModelAndView("framework/vue/explore");
        ComponentSetting setting = VueHelper.getSetting(vue);
        mv.addObject("vuePath", setting.getPath());
        mv.addObject("innerResources", setting.getInnerResources());
        mv.addObject("resources", setting.getResources());
        mv.addObject("vtitle", setting.getTitle());
        return mv;
    }

    @RequestMapping(
        params = {"resources"}
    )
    @ResponseBody
    public JsonResult resources() {
        return new JsonResult(Config.get("vue.resources"));
    }

    @RequestMapping(
        params = {"mresources"}
    )
    @ResponseBody
    public JsonResult mresources() {
        return new JsonResult(Config.get("vue.mobileResources"));
    }
}
