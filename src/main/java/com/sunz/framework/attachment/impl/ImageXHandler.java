package com.sunz.framework.attachment.impl;

import org.springframework.stereotype.Component;

import com.sunz.framework.attachment.ImageSize;

/**
 *	先进行路径转换，再生成缩略图
 *
 * @author Xingzhe
 *
 */
@Component // 初始化fileAccessor
public class ImageXHandler extends ImageHandler {
	
	@Override
	public String getCode() {
		return "imagex"+(targetSize!=null?"_"+targetSize.toString():"");
	}
	
	public String getFileName(String rawFileName) {
		return getAdditionalFileName(rawFileName, targetSize.toString());
	}	
	
	// ******** 构造函数，方便使用 **********
	public ImageXHandler() {}	
	public ImageXHandler(ImageSize size) {
		this.setSize(size);
	}
}
