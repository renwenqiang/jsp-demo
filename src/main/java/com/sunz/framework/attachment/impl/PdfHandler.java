package com.sunz.framework.attachment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.stereotype.Component;

import com.sunz.framework.attachment.AttachHelper;
import com.sunz.framework.core.Config;
import com.sunz.framework.util.StringUtil;

/**
 * pdf转换，不做保存地址转换
 * 
 * @author Xingzhe
 *
 */
@Component
public class PdfHandler extends FileHandler {

	protected static String EXT="pdf";
	protected static OfficeManager officeManager;
	protected static OfficeDocumentConverter converter;
	protected static Logger logger=Logger.getLogger(PdfHandler.class);
	public static void startService() {
		if(officeManager==null) {			// 准备启动服务
			try {
				logger.info("启动openoffice转换服务....");
				DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
				// 设置OpenOffice安装目录
				configuration.setOfficeHome(Config.get("file.openofficeHome"));
				// 设置转换端口，默认为8100
				configuration.setPortNumbers(Integer.parseInt(StringUtil.ifEmpty(Config.get("file.openofficePort"), "8100")));
				// 设置任务执行超时，默认为5分钟
				configuration.setTaskExecutionTimeout(1000 * Long.parseLong(StringUtil.ifEmpty(Config.get("file.openofficeTaskTimeout"), "300")));
				// 设置任务队列超时，默认为24小时
				configuration.setTaskQueueTimeout(1000 * Long.parseLong(StringUtil.ifEmpty(Config.get("file.openofficeQueueTimeout"), "86400")));
				officeManager = configuration.buildOfficeManager();
				officeManager.start(); // 启动服务
				logger.info("openoffice转换服务启动成功!");
				
				converter = new OfficeDocumentConverter(officeManager);		// 创建转换器
				
			} catch (Exception e) {
				logger.error("openoffice转换服务启动失败!",e);
			}
		}
	}

	public static void stopService() {
		logger.info("关闭openoffice转换服务....");
		if (officeManager != null) {
			officeManager.stop();
		}
		logger.info("关闭openoffice转换成功!");
	}
	
	@Override
	protected void finalize() throws Throwable {
		stopService();
		super.finalize();
	}
	
	@Override
	public String handle(InputStream stream, String ext, String rawFileName) {
		String pdfFile=getFileName(rawFileName);		
		if(!fileAccessor.exists(pdfFile)){
			if(EXT.equals(ext)) {	// 本来就是pdf，直接保存
				fileAccessor.save(stream, pdfFile);
			}else {	// 转换
				if(AttachHelper.canConverToPdf(ext)) {
					try {
						/*XWPFDocument doc=new XWPFDocument(stream);
						PdfOptions options = PdfOptions.create();
						PdfConverter.getInstance().convert(doc, out, options);*/
						startService();
						// 因为API仅支持File作为参数，准备临时文件
						File tmpSource= File.createTempFile("pdf_from_", "."+ext);
						int bufferLen=1024,byteCount =0;
						byte[] buffer=new byte[bufferLen];
						FileOutputStream file=new FileOutputStream(tmpSource);
						while ((byteCount=stream.read(buffer))!=-1) {
							file.write(buffer,0,byteCount);
						}
						file.flush();
						file.close();
						
						File tmpTo= File.createTempFile("pdf_to_", "."+EXT);
						logger.info("正在转换Pdf【"+rawFileName+"】...");
						converter.convert(tmpSource, tmpTo);
						logger.info("Pdf转换完成");
						
						fileAccessor.save(new FileInputStream(tmpTo), pdfFile);
						// 删除临时文件
						tmpSource.delete();
						tmpTo.delete();
					}catch(Exception exp) {
						throw new RuntimeException("【"+rawFileName+"】转换为PDF出错：", exp);
					}
				}else {
					throw new RuntimeException("不支持将当前文件类型转为pdf");
				}
			}
		}
		return pdfFile;
	}
	
	@Override
	public String getCode() {
		return EXT;
	}
	public String getFileName(String rawFileName) {
		return rawFileName.substring(0, rawFileName.lastIndexOf(".")+1)+EXT;
	}
	
}
