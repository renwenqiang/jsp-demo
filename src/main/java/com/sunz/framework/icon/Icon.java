package com.sunz.framework.icon;

import java.io.File;
import java.io.FileOutputStream;

import com.sunz.framework.core.Entity;

public class Icon extends Entity {
	private String name;
	private String category;
	private String iconClas; /*为了兼容原表,命名随它莫名其妙吧*/
	private byte[] content;
	private String extend;
	private String path;	
	private boolean rejectCss=false;	
	public boolean isRejectCss() {
		return rejectCss;
	}

	public void setRejectCss(boolean rejectCss) {
		this.rejectCss = rejectCss;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getIconClas() {
		return iconClas;
	}

	public byte[] getContent() {
		return content;
	}

	public String getExtend() {
		return extend;
	}

	public String getPath() {
		return path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setIconClas(String iconCls) {
		this.iconClas = iconCls;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSavePath(String rootPath){
		return rootPath+this.iconClas+"."+this.extend;
	}
	
	public File saveToFile(String rootPath){
		File file=new File(this.getSavePath(rootPath));
		if(file.exists()){
			file.delete();
		}
		try{
			file.createNewFile();
			//new FileImageOutputStream(file).write(this.content);
			new FileOutputStream(file).write(this.content);
		}catch(Exception e){
			throw new RuntimeException("生成图标文件时出错",e);
		}
		
		return file;
	}
}
