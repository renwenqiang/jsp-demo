//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.security;

import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SecurityInterceptor implements HandlerInterceptor {
    private Logger logger = Logger.getLogger(this.getClass());
    private SecurityResolverHelper resolverHelper;

    public SecurityInterceptor() {
    }

    @Autowired
    public void setResolverHelper(SecurityResolverHelper resolverHelper) {
        this.resolverHelper = resolverHelper;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getServletPath().substring(1);
        boolean hasRight = false;
        String error = null;
        String featurePath = uri;
        int type = this.resolverHelper.getSecurityType(request);
        IUriSecurityResolver resolver = this.resolverHelper.getResolver(type, uri);
        if (resolver == null) {
            hasRight = type == 1;
        } else {
            ValidateInfo info = resolver.validate(request);
            featurePath = info.featurePath;
            hasRight = info.hasRight;
            error = info.msg;
        }

        if (!hasRight && (error == null || "".equals(error))) {
            error = "无权访问";
        }

        if (!hasRight) {
            if (request.getHeader("X-Requested-With") != null) {
                OutputStream out = response.getOutputStream();
                out.write((type == 0 ? "systemIsNotLogin" : "{\"success\":false,\"msg\":\"" + error.replaceAll("\\\"", "\\\"") + "\"}").getBytes("UTF-8"));
                response.setContentType("text/html;charset=UTF-8");
            } else if (type == 0) {
                response.sendRedirect(request.getContextPath() + "/framework/login.do?login&origUrl=" + URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString(), "UTF-8") + "&err=" + URLEncoder.encode(error, "UTF-8"));
            } else {
                request.setAttribute("err", error);
                request.setAttribute("origUrl", request.getRequestURI());
                request.getRequestDispatcher("/noauth.jsp").forward(request, response);
            }

            this.logger.debug(featurePath + "\t" + error);
        }

        return hasRight;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
