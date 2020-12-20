//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.config;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.Config.ConfigInitor;
import com.sunz.framework.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanRegistryProcessor implements BeanDefinitionRegistryPostProcessor {
    Logger logger = Logger.getLogger(Config.class);
    final String K_SpringBean_List = "spring.bean.list";
    final String K_SpringBean_Map = "spring.bean.map";
    final String K_SpringBean_ListMap = "spring.bean.listmap";
    final String K_Version = "config.version";
    final String K_Local = "config.locale";

    public SpringBeanRegistryProcessor() {
    }

    private String[] registerSpringBeans(String beanNames, int mode, ConfigurableListableBeanFactory beanFactory, List<String> specials) {
        String[] registerNames = null;
        if (beanNames != null && beanNames.trim().length() != 0) {
            String[] var6 = registerNames = beanNames.split(",");
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String name = var6[var8];
                specials.add(name);
                Object bean = mode == 0 ? Config.getList(name) : (mode == 1 ? Config.getMap(name) : Config.getListMap(name));
                beanFactory.registerSingleton(name, bean);
            }

            return registerNames;
        } else {
            return registerNames;
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = (PropertySourcesPlaceholderConfigurer)beanFactory.getBean(PropertySourcesPlaceholderConfigurer.class);
        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
        placeholderConfigurer.postProcessBeanFactory(beanFactory);
        Map<String, String> fileConfigs = new HashMap();
        Iterator var4 = placeholderConfigurer.getAppliedPropertySources().iterator();

        while(true) {
            Properties p;
            String k;
            do {
                Object o;
                do {
                    if (!var4.hasNext()) {
                        ConfigInitor helper = (ConfigInitor)beanFactory.getBean(ConfigInitor.class);
                        if (fileConfigs.containsKey("config.locale")) {
                            helper.setUserConfigLocale((String)fileConfigs.get("config.locale"));
                        }

                        helper.init();
                        if (Config.get("config.version") == null) {
                            Iterator var12 = fileConfigs.keySet().iterator();

                            while(var12.hasNext()) {
//                                String k = (String)var12.next();// TODO 原
                                k = (String)var12.next();// TODO 改
                                if (!k.startsWith("log4j.") && !k.startsWith("jdbc.") && Config.get(k) == null) {
                                    Config.set(k, (String)fileConfigs.get(k));
                                }
                            }

                            Config.set("config.version", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
                            this.logger.warn("当前项目数据库第一次使用新功能【系统参数配置】，系统已自动将properties文件中的配置的参数转入数据库，请下次项目启动前手动删除各properties配置文件（log4j和数据库连接配置除外），并去数据库中修改参数注释");
                        }

                        helper.setFileConfigs(fileConfigs);
                        List<String> specials = new ArrayList();
                        this.registerSpringBeans(Config.get("spring.bean.list"), 0, beanFactory, specials);
                        this.registerSpringBeans(Config.get("spring.bean.map"), 1, beanFactory, specials);
                        this.registerSpringBeans(Config.get("spring.bean.listmap"), 2, beanFactory, specials);
                        Map<String, Object> configMap = new HashMap();
                        Iterator var16 = Config.getAllDefinations().iterator();

                        while(var16.hasNext()) {
                            ConfigDefination c = (ConfigDefination)var16.next();
                            if (!specials.contains(c.getGroup())) {
                                k = c.getCode();
                                String v = Config.get(k);
                                if (c.isSpringbean()) {
                                    beanFactory.registerSingleton(k, StringUtil.ifEmpty(v, ""));
                                } else {
                                    configMap.put(k, v == null ? "" : v);
                                }
                            }
                        }

                        MutablePropertySources pss = new MutablePropertySources();
                        pss.addFirst(new MapPropertySource("databaseConfigs", configMap));
                        placeholderConfigurer.setPropertySources(pss);
                        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(false);
                        return;
                    }

                    PropertySource ps = (PropertySource)var4.next();
                    o = ps.getSource();
                } while(!(o instanceof Properties));

                p = (Properties)o;
            } while(p == null);

            Iterator var8 = p.stringPropertyNames().iterator();

            while(var8.hasNext()) {
                k = (String)var8.next();
                fileConfigs.put(k, p.getProperty(k));
            }
        }
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }
}
