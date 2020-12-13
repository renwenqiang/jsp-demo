package com.sunz.framework.util;

/**
 * 	字符串相关帮助类，最主要提供isEmpty方法
 * 
 * @author Xingzhe
 *
 */
public class StringUtil {
	
	/**
	 * 	统一判断==null或==""
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str==null||"".equals(str);
	}
	
	/*public static boolean isEmpty(Object str) {
		return str==null|| str.toString().length()==0;
	}*/
	/**
	 * 	判断不为空（null或toString()为空），
	 * 	不建议使用，仅为兼容旧代码
	 * @param str
	 * @return
	 */
	@Deprecated
	public static boolean isNotEmpty(Object str) {
		return str!=null && str.toString().length()>0;
	}	
	/**
	 * 	第一个参数不为空则返回自己，否则返回第二个==SQL.nvl==C#.??
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static String ifEmpty(String str,String defaultValue) {
		return isEmpty(str)?defaultValue:str;
	}
	
	/**
	 * 	以非正则的方式替换字符串--String.replaceAll是正则方式的
	 * 
	 * @param strSource
	 * @param strOld
	 * @param strNew
	 * @return
	 */
	public static String replaceAll(String strSource, String strOld, String strNew) {
		if (isEmpty(strSource)) {
			return null;
		}
		if(isEmpty(strOld))
			throw new RuntimeException("内部错误：未指定被替换字符串");
			
		if(isEmpty(strNew))
			return strSource;
		
		int index=strSource.indexOf(strOld);
		if(index<0)
			return strSource;
		
		StringBuilder sb=new StringBuilder();
		int start=0,olen=strOld.length();
		do {
			sb.append(strSource.substring(start, index));
			sb.append(strNew);
			start=index+olen;
		}while((index=strSource.indexOf(strOld,start))>-1);
		
		sb.append(strSource.substring(start));
		
		return sb.toString();
		//int index=strSource.indexOf(strOld);
		//return index<0?strSource:(strSource.substring(0, index)+(isEmpty(strNew)?"":strNew)+replaceAll(strSource.substring(index+strOld.length()),strOld,strNew));
	}
	
	
	/**
	 * 统一提供字符串解析为数组
	 * 支持json格式
	 * 支持逗号或封号或｜分隔
	 *	["a","b",...]
	 *	"a","b",...
	 *	a,b,....
	 *	a|b|....
	 * @param arrString
	 * @return
	 */
	public static String[] parseToArray(String arrString){
		if(isEmpty(arrString))
			return null;
		
		arrString=arrString.trim();
		if(arrString.startsWith("[")){
			return parseToArray(arrString.substring(1, arrString.length()-1));
		}else if(arrString.startsWith("\"")){
			return arrString.substring(1, arrString.length()-1).split("\"\\s*,\\s*\"");//"\\\",\\\""
		}else{
			return arrString.split("\\s*(,|;|\\|)\\s*");
		}
	}
}
