package com.sunz.framework.attachment.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sunz.framework.attachment.IAttachmentHandler;
import com.sunz.framework.attachment.IFileAccessor;

/**
 * 	文件类型的处理器（即都需要fileAccessor）
 * 	
 * @author Xingzhe
 *
 */
public abstract class FileHandler implements IAttachmentHandler{
	protected Logger logger=Logger.getLogger(this.getClass());
	protected static IFileAccessor fileAccessor;
	@Autowired
	public void setFileAccessor(IFileAccessor accessor) {
		fileAccessor = accessor;
	}
	
	/**
	 * 	获取加前缀后的文件名，如a/b/c.txt + 32_32 ->a_32_32/b/c.txt
	 * 
	 * @param rawFileName
	 * @param prefix
	 * @return
	 */
	protected String getAdditionalFileName(String rawFileName,String prefix) {
		rawFileName=fileAccessor.normalizeSavePath(rawFileName);
		int index=rawFileName.indexOf(File.separator);
		String rootFolder=rawFileName.substring(0, index);
		return rootFolder+"_"+prefix+rawFileName.substring(index);
	}
}
