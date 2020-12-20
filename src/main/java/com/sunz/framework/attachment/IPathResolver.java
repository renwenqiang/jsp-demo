package com.sunz.framework.attachment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.GuidGenerator;

/**
 * 	路径字符生成器
 * 		生成指定类型的路径字符串
 * 
 * @author Xingzhe
 *
 */
public interface IPathResolver {
	/**
	 * 	生成路径字符
	 * @param ext TODO
	 * 
	 * @return
	 */
	String resolve(String ext);
	/**
	 * 	处理器代号（唯一）--将根据代号来找到此处理器
	 * 
	 * @return
	 */
	String getCode();
	
	
	// ******************* 内置生成器 ****************	 
	/**
	 * 	上传基准目录
	 */
	static IPathResolver uploadRootResolver=new IPathResolver() {
		final String KEY_UPLOAD_ROOT="file.uploadRoot";
		String root=Config.get(KEY_UPLOAD_ROOT);
		{
			Config.addChangeListener((key)->{
				if(KEY_UPLOAD_ROOT.equals(key))
					root=Config.get(KEY_UPLOAD_ROOT);
			});
		}
		@Override
		public String resolve(String ext) {
			return root;
		}
		
		@Override
		public String getCode() {
			return "root";
		}
	};
	/**
	 * 	日期，=={year}{month}/{day},注意年月是合并的，直接带目录符
	 * 		=DateFormat("yyyyMM"+File.separator+"dd")
	 */
	static IPathResolver dateResolver=new IPathResolver() {		
		@Override
		public String resolve(String ext) {
			Calendar c = Calendar.getInstance();
			StringBuilder sb=new StringBuilder(9/*4+2+1+2*/);
			// year
			sb.append(c.get(Calendar.YEAR));
			// month
			int field=c.get(Calendar.MONTH)+1;
			if(field<=9)sb.append("0");
			sb.append(field);
			// 
			sb.append(File.separator);
			// day
			field=c.get(Calendar.DATE);
			if(field<=9)sb.append("0");
			sb.append(field);
			
			return sb.toString();
		}
		
		@Override
		public String getCode() {
			return "date";
		}
	};
	/**
	 * 	年
	 */
	static IPathResolver yearResolver=new IPathResolver() {		
		@Override
		public String resolve(String ext) {
			return Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		}
		
		@Override
		public String getCode() {
			return "year";
		}
	};
	
	/**
	 * 	月
	 */
	static IPathResolver monthResolver=new IPathResolver() {
		@Override
		public String resolve(String ext) {
			return Integer.toString(Calendar.getInstance().get(Calendar.MONTH)+1);
		}
		
		@Override
		public String getCode() {
			return "month";
		}
	};
	/**
	 * 	日
	 */
	static IPathResolver dayResolver=new IPathResolver() {		
		@Override
		public String resolve(String ext) {
			return Integer.toString(Calendar.getInstance().get(Calendar.DATE));
		}
		
		@Override
		public String getCode() {
			return "day";
		}
	};	
	
	/**
	 * guid
	 */
	static IPathResolver guidResolver=new IPathResolver() {		
		@Override
		public String resolve(String ext) {
			return GuidGenerator.getGuid();
		}
		
		@Override
		public String getCode() {
			return "guid";
		}
	};	
	
}
