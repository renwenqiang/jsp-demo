//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.security;

import com.sunz.framework.core.MappedParameterSupport;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseUriSecurityResolver implements IUriSecurityResolver, IResolverFactory {
    protected Logger logger = Logger.getLogger(this.getClass());
    protected static ISecurityResolverHelper resolverHelper;
    protected final String Char_Union = "&";
    protected final String Char_WildCard = "*";
    protected static final String Char_Split = "=";
    protected String path;
    protected String error;
    protected int securityType;
    protected String id;
    private static MappedParameterSupport mappedParameterSupport;

    public BaseUriSecurityResolver() {
    }

    public void setResolverHelper(ISecurityResolverHelper helper) {
        resolverHelper = helper;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Autowired(
        required = false
    )
    public void setMappedParameterSupport(MappedParameterSupport mSupport) {
        mappedParameterSupport = mSupport;
    }

    protected static MappedParameterSupport getMappedParameterSupport() {
        if (mappedParameterSupport == null) {
            mappedParameterSupport = new MappedParameterSupport() {
                protected void setExtendMappedParameter(Map<String, Object> map, HttpServletRequest request) {
                }
            };
        }

        return mappedParameterSupport;
    }

    protected static Map toMap(HttpServletRequest request) {
        return getMappedParameterSupport().getMappedParameter(request);
    }

    protected boolean isMatched(HttpServletRequest request, String paramName, String paramValue) {
        return paramValue != null && !paramValue.equals("") ? paramValue.equals(request.getParameter(paramName)) : request.getParameter(paramName) != null;
    }

    public void init(int type, String path, String err) {
        this.securityType = type;
        this.path = path;
        this.error = err;
    }

    public IUriSecurityResolver create(int type, String path, String defaultError, String setting, String id) {
        try {
            IUriSecurityResolver resolver = (IUriSecurityResolver)this.getClass().newInstance();
            resolver.init(type, path, defaultError);
            resolver.setSetting(setting);
            resolver.setId(id);
            return resolver;
        } catch (Exception var7) {
            this.logger.error("", var7);
            return null;
        }
    }

    public void refresh() {
    }
}
