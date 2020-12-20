package com.sunz.framework.attachment.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

import com.sunz.framework.attachment.AttachHelper;
import com.sunz.framework.attachment.ImageSize;

/**
 *	缩略图处理，不包含路径转换
 *		注意：因缩略图目标大小不一，应为不同大小创建不同的处理器实例
 *
 * @author Xingzhe
 *
 */
@Component // 初始化fileAccessor
public class ImageHandler extends FileHandler{
	ImageSize targetSize;
	public void setSize(ImageSize size) {
		targetSize = size;
	}

	private String getSizeDescription() {
		return "【" + targetSize.toString(" × ") + "】";
	}

	static {
		ImageIO.setUseCache(false);
	}

	private static class ByteArrayStream extends ByteArrayOutputStream{		
		public InputStream toInputStream() {
			return new ByteArrayInputStream(this.buf,0,this.count);
		}
	}
	
	@Override
	public String handle(InputStream stream, String ext, String rawFileName) {
		if(targetSize==null) {
			throw new RuntimeException("未指定图片大小");
		}
		
		if (!AttachHelper.isImage(ext)) {
			throw new RuntimeException("不支持的图片格式，无法转成目标大小" + getSizeDescription());
		}

		// 开始生成缩略图
		try {
			// 2020-11-12 考虑jdk处理的图片效果较差，使用第三方库
			ByteArrayStream out=new ByteArrayStream(); 
			net.coobird.thumbnailator.Thumbnails.of(stream).size(targetSize.getWidth(), targetSize.getHeight()).toOutputStream(out);

			String savePath = getFileName(rawFileName);;
			fileAccessor.save(out.toInputStream(), savePath);
			
			return savePath;
		} catch (Exception e) {
			throw new RuntimeException("将图片转成目标大小" + getSizeDescription() + "时出错", e);
		}
	}

	
	@Override
	public String getCode() {
		return "image"+(targetSize!=null?"_"+targetSize.toString():"");
	}
	
	public String getFileName(String rawFileName) {
		return rawFileName;
	}	
	
	// ******** 构造函数，方便使用 **********
	public ImageHandler(){}
	public ImageHandler(ImageSize size) {
		this.setSize(size);
	}
}
