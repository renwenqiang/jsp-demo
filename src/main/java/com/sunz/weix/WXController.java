//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.weix;

import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.util.IPagingQueryService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Scope("prototype")
@Controller
@RequestMapping({"/framework/wx"})
public class WXController extends BaseController {
    @Autowired
    IPagingQueryService pagingQueryService;

    public WXController() {
    }

    public void setPagingQueryService(IPagingQueryService pagingQueryService) {
        this.pagingQueryService = pagingQueryService;
    }

    @RequestMapping(
        params = {"js"}
    )
    public ModelAndView jsApi(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = null;
        mv = new ModelAndView("framework/weixin/sunzweixin");
        TSUser user = this.getLoginUser();
        if (user == null) {
            mv.addObject("islogin", false);
            mv.addObject("logindata", "''");
        } else {
            mv.addObject("loginuser", user);
            mv.addObject("loginuserstr", "{'userName':'" + user.getUserName() + "','id':'" + user.getId() + "'}");
            mv.addObject("islogin", true);
            JsonResult jr = new JsonResult();
            jr.setSuccess(true);
            jr.setData(this.getLoginSupport().getLoginExtendInfo(request));
            String logindata = JSONObject.toJSONString(jr);
            mv.addObject("logindata", logindata);
        }

        String config = "''";

        try {
            config = WeixUtil.getConfig(request, true);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        mv.addObject("jsApiList", Config.get("weix.jsApiList"));
        mv.addObject("wxConfig", config);
        return mv;
    }
}
