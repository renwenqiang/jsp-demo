package com.sunz.framework.attachment.impl;

import java.io.InputStream;

import org.springframework.stereotype.Component;

/**
 * 	将附件原封不动的保存，即不做任何处理
 * 
 * @author Xingzhe
 *
 */
@Component
public class RawFileHandler extends FileHandler {
	
	@Override
	public String handle(InputStream stream, String ext,String rawFileName) {
		fileAccessor.save(stream,rawFileName);
		return rawFileName;
	}

	@Override
	public String getCode() {
		return "raw";
	}

	@Override
	public String getFileName(String rawFileName) {
		return rawFileName;
	}

}
