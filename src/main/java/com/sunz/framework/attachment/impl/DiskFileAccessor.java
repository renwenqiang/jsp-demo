package com.sunz.framework.attachment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import com.sunz.framework.attachment.IFileAccessor;
import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;

/**
 * 	磁盘文件系统实现（支持windows和linux）
 * 
 * @author Xingzhe
 *
 */
public class DiskFileAccessor implements IFileAccessor {

	final String KEY_UPLOAD_ROOT="file.uploadRoot";
	String root;
	@PostConstruct
	public void init(){
		Config.ChangeHandler handler=(key)->{
			if(StringUtil.isEmpty(key) || KEY_UPLOAD_ROOT.equals(key)) {
				root=normalizeSavePath(Config.get(KEY_UPLOAD_ROOT));
				if(!root.endsWith(File.separator))
					root=root+File.pathSeparatorChar;
				
				File froot=new File(root);
				if(! froot.exists())
					throw new RuntimeException("【用于文件上传的】【磁盘文件存取】根目录配置（配置项file.uploadRoot）不正确：当前机器不存在【"+root+"】目录");
				formatRoot=froot.getPath();
			}
		};
		Config.addChangeListener(handler);
		handler.onChange(KEY_UPLOAD_ROOT);
	}

	String	regOld="/".equals(File.separator)?"\\\\":"/",
			regNew="/".equals(File.separator)?"/":"\\\\";
	
	
	String toSavePath(String path) {
		return root +normalizeSavePath(path);
	}
	
	@Override
	public String normalizeSavePath(String path) {
		return  path.replaceAll(regOld, regNew);
	}
	
	@Override
	public String toWebUrl(String path) {
		if(path.startsWith(root))
			path=path.substring(root.length());
		
		return path.replaceAll("\\\\", "/");
	}
	
	private static String formatRoot;	// 用于权限较验
	// 权限较验，不允许访问root以外的路径
	private static boolean checkAuth(File file) {
		try {
			file=file.getCanonicalFile();
		}catch (Exception e) {
			return false;
		}
		while((file=file.getParentFile())!=null) {
			if(formatRoot.equals(file.getPath()))
				return true;
		}
		return false;
	}
	private File getSafeFile(String fileName) {
		File file=new File(toSavePath(fileName));
		if(checkAuth(file))
			return file;
		throw new RuntimeException("非法的文件路径"+fileName);
	}
	
	File createFile(String fileName) throws IOException {
		File file=getSafeFile(fileName);		
		File fParent=file.getParentFile();
		if(!fParent.exists())
			fParent.mkdirs();
		if(!file.exists())
			file.createNewFile();
		
		return file;
	}
	
	@Override
	public String save(byte[] data, String fileName) {
		try {
			FileOutputStream stream=new FileOutputStream(createFile(fileName));
			stream.write(data);
			stream.flush();
			stream.close();
		}catch (Exception e) {
			throw new RuntimeException("文件【"+fileName+"】写入磁盘出错",e);
		}
		return toWebUrl(fileName);
	}

	@Override
	public String save(InputStream stream, String fileName) {
		try {
			int bufferLen=1024,byteCount =0;
			byte[] buffer=new byte[bufferLen];
			FileOutputStream file=new FileOutputStream(createFile(fileName));
			while ((byteCount=stream.read(buffer))!=-1) {
				file.write(buffer,0,byteCount);
			}			
			file.flush();
			file.close();
		}catch (Exception e) {
			throw new RuntimeException("文件【"+fileName+"】写入磁盘出错",e);
		}
		return toWebUrl(fileName);
	}

	@Override
	public byte[] read(String fileName) {
		try {
			InputStream stream=readToStream(fileName);
			byte[] data=new byte[stream.available()];
			stream.read(data);
			stream.close();
			return data;
		}catch (Exception e) {			
			throw e instanceof RuntimeException?(RuntimeException)e:new RuntimeException("磁盘文件【"+fileName+"】读取出错",e);
		}
	}

	@Override
	public InputStream readToStream(String fileName) {
		File file=getSafeFile(fileName);
		if(!file.exists())
			throw new RuntimeException("文件【"+fileName+"】不存在");
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("磁盘文件【"+fileName+"】读取出错",e);
		}
	}

	@Override
	public boolean exists(String fileName) {
		return getSafeFile(fileName).exists();
	}

	@Override
	public boolean delete(String fileName) {
		return getSafeFile(fileName).delete();
	}

}
