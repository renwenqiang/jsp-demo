package org.jeecgframework.core.extend.swftools;

import java.io.File;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.jeecgframework.core.util.FileUtils;

/**
 * OFFICE文档转换服务类
 * 
 * @author jeecg
 * 
 */
public class OpenOfficePDFConverter implements PDFConverter {
	
	static Logger logger=Logger.getLogger(OpenOfficePDFConverter.class);

	private static OfficeManager officeManager;
	/** OpenOffice安装根目录 */
	private static String OFFICE_HOME = ConStant.OFFICE_HOME;
	private static int[] port = { 8100 };

	public void convert2PDF(String inputFile, String pdfFile, String extend) {

		startService();
		logger.info("进行文档转换转换:" + inputFile + " --> " + pdfFile);
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		try {
			converter.convert(new File(inputFile), new File(pdfFile));
		} catch (Exception e) {
			logger.error("pdf转换出错：",e);
		}

		stopService();
		logger.info("进行文档转换转换---- 结束----");
	}

	public void convert2PDF(String inputFile, String extend) {
		String pdfFile = FileUtils.getFilePrefix2(inputFile) + ".pdf";
		convert2PDF(inputFile, pdfFile, extend);

	}

	public static void startService() {
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		try {
			// 准备启动服务
			configuration.setOfficeHome(OFFICE_HOME);// 设置OpenOffice.org安装目录
			// 设置转换端口，默认为8100
			configuration.setPortNumbers(port);
			// 设置任务执行超时为5分钟
			configuration.setTaskExecutionTimeout(1000 * 60 * 5L);
			// 设置任务队列超时为24小时
			configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);

			officeManager = configuration.buildOfficeManager();
			officeManager.start(); // 启动服务
			logger.info("office转换服务启动成功!");
		} catch (Exception ce) {
			logger.error("office转换服务启动失败!",ce);
		}
	}

	public static void stopService() {
		logger.info("关闭office转换服务....");
		if (officeManager != null) {
			officeManager.stop();
		}
		logger.info("关闭office转换成功!");
	}
}
