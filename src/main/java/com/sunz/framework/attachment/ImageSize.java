package com.sunz.framework.attachment;

import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;

/**
 * 	图片尺寸定义
 * 
 * @author Xingzhe
 *
 */
public class ImageSize {
	
	public ImageSize(int w,int h) {
		this.width=w;
		this.height=h;
	}
	
	int width;
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	int height;
	
	private static String Char_ImageSize=StringUtil.ifEmpty(Config.get("file.imageSizeChar"), "_");
	@Override
	public String toString() {
		return toString(Char_ImageSize);
	}
	
	public String toString(String connectChar) {
		return width+connectChar+height;
	}
}
