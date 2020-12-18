//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunz.framework.util.StringUtil;
import java.util.Collection;

public class JsonHelper {
    private static SerializerFeature[] serializerFeatures;

    public JsonHelper() {
    }

    private static SerializerFeature[] getConfigedFeautres() {
        if (serializerFeatures == null) {
            String[] features = StringUtil.parseToArray(StringUtil.ifEmpty(Config.get("json.fastjson.features"), "WriteDateUseDateFormat"));
            serializerFeatures = new SerializerFeature[features.length];

            for(int i = 0; i < features.length; ++i) {
                serializerFeatures[i] = SerializerFeature.valueOf(features[i]);
            }
        }

        return serializerFeatures;
    }

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, getConfigedFeautres());
    }

    public static <T> Collection<T> toCollection(String json, Class<T> valueClz) {
        return JSON.parseArray(json, valueClz);
    }

    public static <T> T toBean(String json, Class<T> beanClz) {
        return JSON.parseObject(json, beanClz);
    }

    public static <T> T json2Bean(Object jsonObject, Class<T> clazz) {
        if (jsonObject instanceof String) {
            return toBean((String)jsonObject, clazz);
        } else {
            JSONObject json = (JSONObject)jsonObject;
            return json.toJavaObject(clazz);
        }
    }

    public static <T> Collection<T> json2Collection(Object jsonObject, Class<T> clazz) {
        if (jsonObject instanceof String) {
            return toCollection((String)jsonObject, clazz);
        } else {
            JSONArray json = (JSONArray)jsonObject;
            return json.toJavaList(clazz);
        }
    }
}
