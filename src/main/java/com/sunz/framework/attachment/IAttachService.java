package com.sunz.framework.attachment;

import java.io.InputStream;
import java.util.List;

/**
 * 	附件服务
 * 
 * 	应解决格式转换及缩略图等问题
 * 	不允许直接指定路径进行保存，必须由此接口统一运算后得出
 * 
 * @author Xingzhe
 *
 */
public interface IAttachService {
	
	/**
	 * 	保存文件
	 * 	需注意的是，要在此解决文件缩略图、格式转换等问题
	 * 
	 * @param data 		附/文件内容
	 * @param ext 		附/文件后缀--指InputStream的文件类型（后缀名）
	 * @param type 		预见定义类型（需先在T_S_Attachment_Type表中进行定义再使用)，由此值去计算存储（相对）路径，并对文件进行一些后处理（如图片生成缩略图、word/excel转pdf等)
	 * @param attach 	准备向数据库进行记录的信息，传null将不写入数据库
	 * 
	 * @return 返回保存路径，进行过后处理的需要返回所有路径，如缩略图：原文件路径、尺寸x路径、尺寸y路径
	 */
	String[] save(InputStream stream,String ext,String type,Attachment attach);
	
	/**
	 * 	删除指定的附件信息
	 * 
	 * @param attachid 附件id
	 * @param fileCascade 是否删除文件
	 * @return
	 */
	int delete(String attachid,boolean fileCascade);
	
	/**
	 * 	获取指定的附件
	 * 
	 * @param id
	 * @return
	 */
	Attachment getAttachment(String id);
	/**
	 * 	获取指定bid下的附件
	 * 
	 * @param bid
	 * @param btype
	 * @return
	 */
	List<Attachment> getBusinessAttachments(String bid ,String btype);
	
	/**
	 * 	读取指定id的附件<br>
	 * 	或以文件的相对路径（即save方法返回的路径)来读取文件--无需要读数据库，优先级高
	 * 
	 * @param relativeFileName
	 * @return
	 */
	byte[] read(String attachid,String relativeFileName);
	
	/**
	 * 	以Stream方式读取指定id的附件<br>
	 * 	或相对路径（即save方法返回的路径)的文件--无需要读数据库，优先级高
	 * @see ::read(String relativeFileName)
	 * 
	 * @param relativeFileName
	 * @return
	 */
	InputStream readToStream(String attachid,String relativeFileName);
	
	/**
	 * 	对Pdf的支持
	 * 
	 * @param relativeFileName --无需要读数据库，优先级高
	 * @return
	 */
	InputStream readToPdf(String attachid,String relativeFileName);
	
	/**
	 * 	对图片支持
	 * 
	 * @param relativeFileName --无需要读数据库，优先级高
	 * @param width
	 * @param height
	 * @return
	 */
	InputStream readImageToSize(String attachid,String relativeFileName,int width,int height);
}
