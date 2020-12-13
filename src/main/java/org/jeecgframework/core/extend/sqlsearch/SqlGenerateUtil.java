package org.jeecgframework.core.extend.sqlsearch;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import com.sunz.framework.util.StringUtil;

public class SqlGenerateUtil {
	/** 时间查询符号 */
	private static final String END = "_end";
	private static final String BEGIN = "_begin";

	private static final SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * 获取OBJ对应的table名
	 * @param searchObj
	 * @return
	 */
	public static String generateTable(Object searchObj){
		Table table = searchObj.getClass().getAnnotation(Table.class);
		if(StringUtil.isEmpty(table.name())){
			return searchObj.getClass().getSimpleName();
		}else{
			return table.name();
		}
	}
	
	/**
	 * 将Obj对应的属性转为DB中的属性
	 * @param searchObj
	 * @param fields
	 * @param dealfields
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static StringBuffer generateDBFields(Object searchObj,String fields,List dealfields){
		StringBuffer dbFields = new StringBuffer();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(searchObj.getClass());
		String[] fileNames = fields.split(",");
		for(int i=0;i<fileNames.length;i++){
			for(PropertyDescriptor propertyDescriptor:propertyDescriptors){
				String propertyName = propertyDescriptor.getName();
				if(fileNames[i].equals(propertyName)){
					dbFields.append(getDbNameByFieldName(propertyDescriptor)+((i==fileNames.length-1)?"":","));
					dealfields.add(fileNames[i]);
					break;
				}
			}
		}
		
		return dbFields;
	}
	
	
	
	/**
	 * 获取属性对应的数据库列名
	 * @param propertyDescriptor
	 * @return
	 */
	public static String getDbNameByFieldName(PropertyDescriptor propertyDescriptor){
		String propertyName = propertyDescriptor.getName();
		Column column = null;
		Method readMethod = propertyDescriptor.getReadMethod();
		if(readMethod!=null){
			column = readMethod.getAnnotation(Column.class);
			if(column==null){
				Method writeMethod = propertyDescriptor.getWriteMethod();
				if(writeMethod!=null){
					column = writeMethod.getAnnotation(Column.class);
				}
			}
		}
		
		//如果找不到@column，或者@column的name为空，那么数据库列名就是属性名
		if(column==null || StringUtil.isEmpty(column.name())){
			return propertyName;
		}else{
			return column.name();
		}
	}
	
	
}
