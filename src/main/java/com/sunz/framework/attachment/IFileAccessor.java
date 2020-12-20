package com.sunz.framework.attachment;

import java.io.InputStream;

/**
 * 	文件存取，将文件存入指定路径，或将指定路径文件读出，引入本接口的目的是为支持Ftp、分布式等非磁盘文件系统
 * 		文件路径与文件存取方式一一对应
 * 		实现类不应打Spring标签--原因是应当允许对特定文件系统如分布式文件系统等或特定方式如Ftp等进行实现
 * 
 * @author Xingzhe
 *
 */
public interface IFileAccessor {
	/**
	 * 	将路径规范化处理，即处理成可直接传递给save方法的路径，对于磁盘文件系统来说，主要处理斜杠/和反斜杠\
	 * 		注意返回的不是保存的绝对路径，仅仅是对path的规范化处理
	 * @param path
	 * @return
	 */
	String normalizeSavePath(String path);
	
	/**
	 * 	将路径转为web路径，可在前端配合file.downloadRootf直接访问（需要配置并启动相应服务）或传入以下的read方法
	 */
	String toWebUrl(String path);
	
	/**
	 * 	保存文件数据
	 * @param data
	 * @param fileName
	 */
	String save(byte[] data,String fileName);
	
	/**
	 * 	以Stream方式保存文件数据
	 * 
	 * @param stream
	 * @param fileName
	 */
	String save(InputStream stream,String fileName);
	
	/**
	 * 	来读取文件
	 * 
	 * @param fileName 
	 * @return
	 */
	byte[] read(String fileName);
	
	/**
	 * 	以Stream方式读取文件
	 * @see ::read(String fileName)
	 * 
	 * @param fileName
	 * @return
	 */
	InputStream readToStream(String fileName);
	
	/**
	 * 	将目标路径读取为OutputStream（以便写入）
	 * 
	 * @param fileName
	 * @return
	OutputStream readToOutput(String fileName);
	 */
	
	/**
	 * 	允许判断是否存在指定文件
	 * 
	 * @param fileName
	 * @return
	 */
	boolean exists(String fileName);
	
	/**
	 * 	删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	boolean delete(String fileName);
}
