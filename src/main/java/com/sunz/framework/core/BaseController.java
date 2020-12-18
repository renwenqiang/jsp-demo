//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import com.sunz.framework.util.StringUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.CommonService;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class BaseController {
    protected Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    protected CommonService commonService;
    private ILoginSupport loginSupport;
    private MappedParameterSupport mappedParameterSupport;

    public BaseController() {
    }

    protected boolean isAjax() {
        return isAjax(this.getRequest());
    }

    protected static boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null;
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @Autowired(
        required = false
    )
    public void setLoginSupport(ILoginSupport loginSupport) {
        this.loginSupport = loginSupport;
    }

    protected ILoginSupport getLoginSupport() {
        if (this.loginSupport == null) {
            this.loginSupport = new BaseLoginSupport();
        }

        return this.loginSupport;
    }

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    protected TSUser getLoginUser() {
        return this.getLoginSupport().getLoginUser(this.getRequest());
    }

    protected Object getLoginExtendInfo() {
        return this.getLoginSupport().getLoginExtendInfo(this.getRequest());
    }

    @Autowired(
        required = false
    )
    public void setMappedParameterSupport(MappedParameterSupport mappedParameterSupport) {
        this.mappedParameterSupport = mappedParameterSupport;
    }

    protected MappedParameterSupport getMappedParameterSupport() {
        if (this.mappedParameterSupport == null) {
            this.mappedParameterSupport = new MappedParameterSupport() {
                protected void setExtendMappedParameter(Map<String, Object> map, HttpServletRequest request) {
                }
            };
        }

        return this.mappedParameterSupport;
    }

    protected Map toMap() {
        return this.getMappedParameterSupport().getMappedParameter(this.getRequest());
    }

    protected boolean isStringEmpty(String target) {
        return StringUtil.isEmpty(target);
    }
}
