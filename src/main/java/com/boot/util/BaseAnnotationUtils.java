package com.boot.util;

import com.boot.exception.NullAnnotationException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 基本注解工具类，继承Spring的AnnotationUtils扩展
 * @author WindShadow
 * @verion 2020/8/17.
 */

public abstract class BaseAnnotationUtils extends AnnotationUtils {

    public BaseAnnotationUtils() {
    }

    private static final String FIELD_MENMBER = "memberValues";

    /**
     * 修改class注解属性
     * @param targetClass 目标class
     * @param aClass 注解class
     * @param fieldName 预修改的注解的属性名称
     * @param fieldValue 预修改的注解的属性值
     * @param <A> 注解类型
     */
    public static <A extends Annotation> void changeAnnotaionField(Class<?> targetClass, Class<A> aClass, String fieldName, Object fieldValue)
            throws NullAnnotationException, NoSuchFieldException, IllegalAccessException {

        A annotation = targetClass.getAnnotation(aClass);
        if (annotation == null) {

            // 找不到注解则抛出异常
            throw new NullAnnotationException("<" + aClass.getName() + "> was not found on [" + targetClass.getName() + "]");
        }
        // 调用处理器，每一个被代理的实例都有一个调用处理器
        InvocationHandler annotationInvocationHandler = Proxy.getInvocationHandler(annotation);
        // 获取代理对象memberValues属性的属性对象
        Field field = annotationInvocationHandler.getClass().getDeclaredField(FIELD_MENMBER);
        // 打破私有
        field.setAccessible(true);
        // 获取代理对象的memberValues属性值
        Map<String,Object> memberMap = (Map<String, Object>) field.get(annotationInvocationHandler);
        // 重新设置值
        memberMap.put(fieldName,fieldValue);
    }

}
