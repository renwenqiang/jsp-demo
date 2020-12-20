package com.sunz.framework.attachment.impl;

import org.springframework.stereotype.Component;

/**
 * 	对保存地址进行转换后，再转为pdf
 * 
 * @author Xingzhe
 *
 */
@Component
public class PdfXHandler extends PdfHandler  {
	
	@Override
	public String getCode() {
		return "pdfx";
	}	

	public String getFileName(String rawFileName) {
		String f= getAdditionalFileName(rawFileName, EXT);
		return f.substring(0, f.lastIndexOf(".")+1)+EXT;
	}
}
