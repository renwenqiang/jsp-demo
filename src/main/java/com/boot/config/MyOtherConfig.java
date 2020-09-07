package com.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author WindShadow
 * @verion 2020/8/18.
 */

@Configuration
public class MyOtherConfig extends WebMvcConfigurationSupport {

    //  这里的配置依旧自动加入spring并生效
}
