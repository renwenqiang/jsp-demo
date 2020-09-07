package com.boot.config;

import com.boot.exception.NullAnnotationException;
import com.boot.util.BaseAnnotationUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * 基本SpringMvc缺省配置类，在项目一级目录中新建类继承本类，即可实现全部SpringMvc默认配置，无xml开发
 * RootConfig类与WebConfig类为配置类，考虑到其与外界无关性，将其设为访问权限为protected内部类
 * @see RootConfig
 * @see WebConfig
 * @author WindShadow
 * @verion 2020/8/16.
 */

public abstract class BaseSpringMvcDefaultConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    {
        // 构造结束后自动获取当前类所在包，并修改RootConfig和WebConfig的class的@ComponentScan注解属性
        String basePackage = this.getPackageName();
        try {
            appointComponentScanBasePackage(new String[]{basePackage});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定扫描基本包路径
     * @param basePackages
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NullAnnotationException
     */
    private static void appointComponentScanBasePackage(String[] basePackages) throws NoSuchFieldException, IllegalAccessException, NullAnnotationException {

        String paramName = "basePackages";
        BaseAnnotationUtils.changeAnnotaionField(RootConfig.class, ComponentScan.class,paramName,basePackages);
        BaseAnnotationUtils.changeAnnotaionField(WebConfig.class, ComponentScan.class,paramName,basePackages);
    }

    /**
     * 获取当前对象所属类所在包名
     * @return
     */
    private String getPackageName() {

        return this.getClass().getPackage().getName();
    }

    /**
     * 相当于添加了Spring的容器
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {


        return new Class[]{RootConfig.class};
    }

    /**
     * 相当于添加了SpringMVC的容器
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    /**
     * 相当于配置了DispatcherServlet的 <url-pattern>/</url-pattern>
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * spring容器配置类扫描非Controller的bean
     */
    @ComponentScan(excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebConfig.class),
            @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
    })
    @Configuration
    protected static class RootConfig {}

    /**
     * springmvc配置类，只扫描Controller
     */
    @ComponentScan(useDefaultFilters = false,
            includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
    @EnableAspectJAutoProxy
    @EnableWebMvc
    @Configuration
    protected static class WebConfig extends WebMvcConfigurationSupport {}
}
