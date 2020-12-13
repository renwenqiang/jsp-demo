package org.jeecgframework.core.util;

import java.util.HashMap;
import java.util.List;
import com.sunz.framework.core.JsonHelper;


/**
 * JSON和JAVA的POJO的相互转换
 * 
 * JSONHelper.java
 */
public final class JSONHelper {
	/***
	 * 	兼容代码，不建议使用，而建议使用toBean
	 * 
	 * @param object
	 * @return
	 */
	@Deprecated
	public static HashMap toHashMap(String json) {
		return (HashMap) JsonHelper.toBean(json, HashMap.class);
	}
	

	/***
	 * 	将JSON对象数组转换为传入类型的List
	 * 	兼容代码，不建议使用，而建议使用toCollection
	 * @param <T>
	 * @param jsonArray
	 * @param objectClass
	 * @return
	 */
	@Deprecated
	public static <T> List<T> toList(String jsonArray, Class<T> objectClass) {
		return (List<T>) JsonHelper.toCollection(jsonArray, objectClass);
	}

	public static String listtojson(String[] fields, int total, List list) throws Exception {
		Object[] values = new Object[fields.length];
		String jsonTemp = "{\"total\":" + total + ",\"rows\":[";
		for (int j = 0; j < list.size(); j++) {
			jsonTemp = jsonTemp + "{\"state\":\"closed\",";
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].toString();
				values[i] = org.jeecgframework.tag.core.easyui.TagUtil.fieldNametoValues(fieldName, list.get(j));
				jsonTemp = jsonTemp + "\"" + fieldName + "\"" + ":\"" + values[i] + "\"";
				if (i != fields.length - 1) {
					jsonTemp = jsonTemp + ",";
				}
			}
			if (j != list.size() - 1) {
				jsonTemp = jsonTemp + "},";
			} else {
				jsonTemp = jsonTemp + "}";
			}
		}
		jsonTemp = jsonTemp + "]}";
		return jsonTemp;
	}

}
