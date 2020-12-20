//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.security;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.ILoginSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SecurityResolverHelper implements ICacheRefreshable, ApplicationContextAware, ISecurityResolverHelper {
    private Logger logger = Logger.getLogger(this.getClass());
    private ILoginSupport loginSupport;
    private Map<String, IResolverFactory> factories;
    private ApplicationContext context;
    private Map<String, IUriSecurityResolver> openMapping = new HashMap();
    private Map<String, IUriSecurityResolver> loginedMapping = new HashMap();

    public SecurityResolverHelper() {
    }

    @Autowired(
        required = false
    )
    public void setLoginSupport(ILoginSupport loginSupport) {
        this.loginSupport = loginSupport;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public IUriSecurityResolver createResolver(int securityType, String resolverType, String path, String setting, String defaultError, String id) {
        IResolverFactory factory = (IResolverFactory)this.factories.get(resolverType);
        if (factory == null) {
            this.logger.error(path + "配置了不可识别的安全控制处理器:" + resolverType);
            return null;
        } else {
            return factory.create(securityType, path, defaultError, setting, id);
        }
    }

    @PostConstruct
    public void init() {
        this.factories = new HashMap();
        Map<String, IResolverFactory> allFactory = this.context.getBeansOfType(IResolverFactory.class);
        Iterator var2 = allFactory.values().iterator();

        while(var2.hasNext()) {
            IResolverFactory factory = (IResolverFactory)var2.next();
            factory.setResolverHelper(this);
            this.factories.put(factory.getType(), factory);
        }

        this.refresh((String)null);
    }

    public void addMapping(int type, String resolverKey, IUriSecurityResolver resolver) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("路径【" + type + "】【" + resolverKey + "】\t绑定了【" + resolver.getType() + "】类型的权限控制");
        }

        if (type == 0) {
            this.openMapping.put(resolverKey, resolver);
        } else {
            this.loginedMapping.put(resolverKey, resolver);
        }

    }

    public IUriSecurityResolver getResolver(int type, String path) {
        return type == 1 ? (IUriSecurityResolver)this.loginedMapping.get(path) : (IUriSecurityResolver)this.openMapping.get(path);
    }

    public int getSecurityType(HttpServletRequest request) {
        return this.loginSupport.getLoginUser(request) == null ? 0 : 1;
    }

    public void refresh(String item) {
        this.openMapping = new HashMap();
        this.loginedMapping = new HashMap();
        Iterator var2 = this.factories.values().iterator();

        while(var2.hasNext()) {
            IResolverFactory factory = (IResolverFactory)var2.next();
            factory.refresh();
        }

    }

    public String getCategory() {
        return "security";
    }

    public String getDescription() {
        return "url访问权限配置";
    }
}
