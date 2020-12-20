package com.sunz.framework.attachment;

import java.io.InputStream;

/**
 * 	附件处理器
 * 		@remark 原则上处理过程中不应直接操作文件（临时文件除外），而应通过FileAccessor进行
 * @author Xingzhe
 *
 */
public interface IAttachmentHandler {
	
	/**
	 * 	对附件（InputStream）进行处理（如直接保存，做缩略图，转pdf等)
	 * 		@remark 考虑并发问题，本方法不应被直接调用，而应通过@see AttachHelper.executeHandler统一调用
	 * 		@remark 再次强调原则上处理过程中不应直接操作文件（临时文件除外），而应通过FileAccessor进行
	 * 
	 * @param stream		待处理的文件流
	 * @param ext			InputStream的文件类型（后缀名），因stream无法携带此信息而设计此参数
	 * @param rawFileName	由调用方生成的“原始”文件路径--即根据path_expression生成的路径
	 * @return
	 */
	String handle(InputStream stream,String ext,String rawFileName);
	
	/**
	 * 	唯一处理器代号，将反向找到此处理器
	 * @return
	 */
	String getCode();
	
	/**
	 * 获取执行后的路径，目的是在将结果与执行分离
	 * @remark 需保证
	 * 				1.此结果与handle一致
	 * 				2.相同参数执行多次的结果一致
	 * @param rawFileName 原始路径==即由调用方“拟生成”的结果路径
	 * @return
	 */
	String getFileName(String rawFileName);
}
