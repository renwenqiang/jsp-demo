package com.sunz.framework.system.receive;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeecgframework.core.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunz.framework.core.Config;

@Controller
@RequestMapping("framework/image")
public class imageController {
	
	@Autowired
	protected CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	// 用于整体控制
	protected ImageEditor editor=new ImageEditor();
	//private static InputStream m_Empty=new java.io.ByteArrayInputStream(new byte[0]);
	
	@RequestMapping(params="getEdit")
	public InputStream getEdit(String id,int x1,int y1,int x2,int y2,int x3,int y3,HttpServletRequest request) throws FileNotFoundException {
		String fileName=this.getFileName(id);
		String realPath = this.getRealPath(fileName);
		//this.url=this.getWebPath(fileName);
		if ((new java.io.File(realPath)).exists()) {

			String backFile = getBackFileName(realPath);
			try {
				copyFile(realPath, backFile);
			} catch (IOException e) {
				//this.setErr("备份时出错");
				e.printStackTrace();
			}

			editor.loadImage(realPath);
			editor.translate(x1, y1, x2, y2, x3, y3);
			editor.save(realPath);
			// editor.save(fileName.substring(0,fileName.lastIndexOf("."))+"2"+fileName.substring(fileName.lastIndexOf(".")));//先写入备份

			return new FileInputStream(realPath);
		} else {
			//this.setErr("文件不存在");
			return new java.io.ByteArrayInputStream(new byte[0]);
		}
		//return SUCCESS;
		
	}
	
	@RequestMapping(params="editImage")
	public InputStream getEditImage(String id,int x1,int y1,int x2,int y2,int x3,int y3,HttpServletRequest request) throws IOException {
		String fileName=this.getFileName(id);
		String realPath = this.getRealPath(fileName);
		if ((new java.io.File(realPath)).exists()) {
			editor.loadImage(realPath);
			java.awt.Image img= editor.translate(x1, y1, x2, y2, x3, y3);
			ByteArrayOutputStream ost=new ByteArrayOutputStream();
			javax.imageio.ImageIO.write((RenderedImage) img, this.imgType, ost);
			
			return new ByteArrayInputStream(ost.toByteArray());
		} else {
			//this.setErr("文件不存在");
			return new java.io.ByteArrayInputStream(new byte[0]);
		}
	}

	
	private String getFileName(String aid) {
		Map<String,Object> data=commonService.findOneForJdbc("select a.* from T_S_RECEIVESCAN_RESOURCE a where a.id= ?",aid);
		if(data==null){return null;}
		String path=(String) data.get("PATH");
		this.imgType=(String) data.get("TYPE");
		String filename=path.substring(path.lastIndexOf("/")+1, path.length());
		return filename;
	}

	private String getRealPath(String fileName) {
		return Config.get("receive.uploadRoot")+ fileName;
	}

//	private String getWebPath(String fileName) {
//		return this.baseUrl + fileName+"?"+(new java.util.Date()).getTime();
//		// this.url=pInfo.getRealPath().replace(this.saveRoot,
//		// this.baseUrl).replace("\\", "/");
//	}

	// ****************** 获取 ******************

	public InputStream getLoad(String id,HttpServletRequest request) throws FileNotFoundException {
		return new FileInputStream(this.getRealPath(this.getFileName(id)));
		//this.url = this.getWebPath(this.getFileName(aid));
		//return SUCCESS;
	}
	
	@RequestMapping(params="getRevert")
	public InputStream getRevert(String id,HttpServletRequest request) throws FileNotFoundException {
		String fileName = this.getFileName(id);
		String realPath = this.getRealPath(fileName);
		try {
			String backFile = getBackFileName(realPath);
			if (!(new File(backFile)).exists()) {
				//this.setErr("当前文件没有备份");
			} else {
				copyFile(backFile, realPath);
			}
		} catch (IOException e) {
			//this.setErr("恢复文件时出错");
		}
		return new FileInputStream(realPath);
		//this.url = this.getWebPath(fileName);
		//return SUCCESS;
	}

	private final String backPost = "_Bak";

	private String getBackFileName(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex > -1)
			return fileName.substring(0, dotIndex) + backPost
					+ fileName.substring(dotIndex);
		else
			return fileName + backPost;
	}

	private void copyFile(String src, String dest) throws IOException {
		int buffer = 1024;
		File srcfile = new File(src);
		if (srcfile.exists()) { // 文件存在时
			InputStream inStream = new FileInputStream(srcfile); // 读入原文件
			FileOutputStream fs = new FileOutputStream(dest);
			byte[] bBuffer = new byte[buffer];
			while (inStream.read(bBuffer) != -1) {
				fs.write(bBuffer);
			}
			inStream.close();
			fs.flush();
			fs.close();
		}
	}
	private String imgType;
}
