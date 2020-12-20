//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import com.alibaba.fastjson.JSONObject;
import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Scope("prototype")
@Controller
@RequestMapping({"/framework/dingLogin"})
public class DingDingLoginController extends BaseController {
    @Autowired
    private SystemService systemService;
    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    protected DingDingService dingDingService;

    public DingDingLoginController() {
    }

    @RequestMapping(
        params = {"login"}
    )
    public ModelAndView oaLogin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = null;
        TSUser user = this.getLoginUser();
        String config;
        if (user == null) {
            mv = new ModelAndView("framework/dingtalk/exsample/login");
        } else {
            config = "framework/dingtalk/exsample/login";
            mv = new ModelAndView(config);
            mv.addObject("loginuser", user);
        }

        config = "";

        try {
            config = DingDingUtil.getConfig(request, false);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        mv.addObject("dingConfig", config);
        return mv;
    }

    @RequestMapping(
        params = {"js"}
    )
    public ModelAndView jsApi(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = null;
        mv = new ModelAndView("framework/dingtalk/sunzdingtalk");
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
            config = DingDingUtil.getConfig(request, true);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        mv.addObject("dingConfig", config);
        return mv;
    }

    @RequestMapping(
        params = {"checkusercode"}
    )
    @ResponseBody
    public JsonResult checkUserByCode(String code, HttpServletRequest request, HttpServletResponse response) {
        JsonResult jr = new JsonResult();
        jr.setSuccess(false);
        jr.setMsg("用户不存在");
        HttpSession session = request.getSession();
        Map<String, Object> resultMap = new HashMap();
        TSUser user = null;
        if (code != null && !code.equals("")) {
            JSONObject userInfo = null;

            try {
                userInfo = DingDingUtil.getDingUserInfoByCode(code);
                resultMap.put("dinguserinfo", userInfo);
                String id = userInfo.getString("userid");
                List<DingDingUser> list = this.dingDingService.getDingUserListByDingUserIds(id);
                jr.setSuccess(false);
                jr.setMsg("用户不存在");
                if (list != null && list.size() > 0) {
                    DingDingUser duser = (DingDingUser)list.get(0);
                    String userId = duser.getT_s_id();
                    if (userId != null) {
                        user = (TSUser)this.commonService.get(TSUser.class, userId);
                        if (user != null && user != null && user.getStatus() != 0) {
                            jr.setSuccess(true);
                            jr.setMsg("登录成功");
                            Object extendInfo = this.getLoginSupport().getExtendInfo(user);
                            jr.setData(extendInfo);
                            this.getLoginSupport().saveLoginInfo(request, user, extendInfo);
                        }

                        this.systemService.addLog("钉钉用户登录", Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
                    } else {
                        jr.setSuccess(false);
                        jr.setMsg("用户没权限访问系统");
                    }
                } else {
                    jr.setSuccess(false);
                    jr.setMsg("用户没权限访问系统");
                }
            } catch (Exception var14) {
                var14.printStackTrace();
                jr.setSuccess(false);
                jr.setMsg("用户异常");
            }
        }

        return jr;
    }
}
