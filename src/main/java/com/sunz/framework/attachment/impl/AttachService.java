package com.sunz.framework.attachment.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.jeecgframework.core.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunz.framework.attachment.Attachment;
import com.sunz.framework.attachment.AttachHelper;
import com.sunz.framework.attachment.Configuration;
import com.sunz.framework.attachment.IAttachmentHandler;
import com.sunz.framework.attachment.IAttachService;
import com.sunz.framework.attachment.IFileAccessor;
import com.sunz.framework.query.helper.SqlParser;
import com.sunz.framework.util.StringUtil;

@Service
public class AttachService implements IAttachService {
	
	IFileAccessor fileAccessor;
	@Autowired
	public void setFileAccessor(IFileAccessor accessor) {
		fileAccessor = accessor;
	}
	
	CommonService hibernateService;
	@Autowired
	public void setHibernateService(CommonService commonService) {
		this.hibernateService = commonService;
	}

	
	@Override
	public String[] save(InputStream stream, String ext, String type ,Attachment attach) {
		Configuration c=AttachHelper.getConfig(type);
		if(!c.isFileTypeAvalid(ext))
			throw new RuntimeException("不支持的文件格式");
		
		String fileName= fileAccessor.normalizeSavePath(c.getNextFileName(ext));		
		String pathStrings="",splitor =File.pathSeparator;
		short streamReaded=0;
		// 主线程
		for(IAttachmentHandler handler:c.getHandlerList()) {
			if(streamReaded++ >= 1) // 首次使用后需要tryRecycle
				stream=AttachHelper.tryRecycleStream(stream, fileName);
			AttachHelper.executeHandler(handler, stream, ext, fileName, false);			
			pathStrings +=handler.getFileName(fileName)+splitor;
			//
		}
		
		// 异步线程
		List<IAttachmentHandler> asyncHandlers=c.getAsyncHandlerList();
		// 开始异步处理
		for(IAttachmentHandler handler:asyncHandlers) {
			pathStrings += handler.getFileName(fileName)+splitor;
		}
		if(asyncHandlers.size()>0) {
			AttachHelper.executeHandlerAsync(asyncHandlers,stream, ext, fileName);
		}
		
		// 保存，返回结果
		String[] paths= pathStrings.split(splitor);
		if(attach!=null) {
			attach.setFilePath(paths[0]);
			attach.setPaths(pathStrings);
			if(!c.isRecordless())
				hibernateService.save(attach);
		}
		return paths;
	}

	private String getFileNameForAttach(String attachid) {
		return  getAttachment(attachid).getFilePath();
	}
	@Override
	public byte[] read(String attachid,String relativeFileName) {
		relativeFileName=StringUtil.isEmpty(relativeFileName)?getFileNameForAttach(attachid):relativeFileName;
		
		return fileAccessor.read(relativeFileName);
	}

	@Override
	public InputStream readToStream(String attachid,String relativeFileName) {
		relativeFileName=StringUtil.isEmpty(relativeFileName)?getFileNameForAttach(attachid):relativeFileName;
		return fileAccessor.readToStream(relativeFileName);
	}


	@Override
	public InputStream readImageToSize(String attachid,String relativeFileName, int width, int height) {
		relativeFileName=StringUtil.isEmpty(relativeFileName)?getFileNameForAttach(attachid):relativeFileName;
		if(!fileAccessor.exists(relativeFileName))
			throw new RuntimeException("指定的图片不存在");
		
		IAttachmentHandler imgHandler= AttachHelper.getHandler("imagex_"+width+"_"+height);
		String targetFile=imgHandler.getFileName(relativeFileName);
		if(!fileAccessor.exists(targetFile)) {
			AttachHelper.executeHandler(imgHandler,fileAccessor.readToStream(relativeFileName), AttachHelper.getFileExtension(relativeFileName), relativeFileName,false);
		}
				
		return fileAccessor.readToStream(targetFile);
	}
	
	IAttachmentHandler pdfxHandler;
	@Override
	public InputStream readToPdf(String attachid,String relativeFileName) {
		relativeFileName=StringUtil.isEmpty(relativeFileName)?getFileNameForAttach(attachid):relativeFileName;
		if(!fileAccessor.exists(relativeFileName))
			throw new RuntimeException("指定的文件不存在");
		if(AttachHelper.isPdf(relativeFileName))
			return fileAccessor.readToStream(relativeFileName);
		
		if(pdfxHandler==null) {
			pdfxHandler=AttachHelper.getHandler("pdfx");
		}
		String targetFile=pdfxHandler.getFileName(relativeFileName);
		if(!fileAccessor.exists(targetFile)) {
			AttachHelper.executeHandler(pdfxHandler,fileAccessor.readToStream(relativeFileName), AttachHelper.getFileExtension(relativeFileName), relativeFileName,false);
		}
				
		return fileAccessor.readToStream(targetFile);
	}

	@Override
	public int delete(String attachid, boolean fileCascade) {
		if(StringUtil.isEmpty(attachid))
			throw new RuntimeException("未指定目标");
		
		if(fileCascade) {
			fileAccessor.delete(getFileNameForAttach(attachid));
		}
		// 删除操作行为由sql语句自行控制--已知有两种方式：delete和set bid=null
		return hibernateService.executeSql(SqlParser.getSql(null, "sys_attachment_delete"), attachid);
	}
	
	@Override
	public Attachment getAttachment(String id) {
		if(StringUtil.isEmpty(id))
			throw new RuntimeException("未指定附件标识");
		
		return hibernateService.get(Attachment.class, id);
	}

	@Override
	public List<Attachment> getBusinessAttachments(String bid, String btype) {
		if(StringUtil.isEmpty(bid))
			throw new RuntimeException("未指定业务标识");
		
		return StringUtil.isEmpty(btype)?
				hibernateService.findHql("from Attachment where bid=?",bid):
				hibernateService.findHql("from Attachment where bid=? and btype=?",bid,btype);
	}

	
}
