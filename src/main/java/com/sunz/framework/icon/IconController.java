package com.sunz.framework.icon;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.sunz.framework.core.GuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;

@Controller("xIconController")
@RequestMapping("framework/icon")
public class IconController extends BaseController  {
	@RequestMapping(params="setting")
	public ModelAndView setting(){
		ModelAndView mv =new ModelAndView("framework/icon/setting");
		
		return mv;
	}
	
	IIconService iconService;
	@Autowired
	public void setIconService(IIconService iconService) {
		this.iconService = iconService;
	}
	
	
	private void getContent(Icon icon,HttpServletRequest request){
		MultipartHttpServletRequest fileRequest= (MultipartHttpServletRequest) request;
		String fname= fileRequest.getFileNames().next();
		MultipartFile file;
		if(fname!=null && !(file= fileRequest.getFile(fname)).isEmpty()){
			try {
				String orgName=file.getOriginalFilename();
				icon.setExtend(orgName.substring(orgName.lastIndexOf(".")+1));
				icon.setContent(file.getBytes());
			} catch (IOException e) {
				this.logger.error("获取上传图标出错",e);
				throw new RuntimeException(e);
			}
		}else {
			if(!isStringEmpty(icon.getId())) {				
				Icon old=iconService.find(icon.getId(),null);
				icon.setContent(old.getContent());
				icon.setExtend(old.getExtend());
			}
		}
	}
	
	@RequestMapping(params="add")
	@ResponseBody
	public JsonResult add(Icon icon,/*String category,String name,String iconClas,*/HttpServletRequest request){
		icon.setId(GuidGenerator.getGuid());
		getContent(icon, request);
		iconService.add(icon);
		
		// 返回json不需要content
		icon.setContent(null);
		return new JsonResult(icon);
	}
	
	@RequestMapping(params="save")
	@ResponseBody
	public JsonResult save(Icon icon,/*String category,String name,String iconClas,*/HttpServletRequest request){
		if(icon.getId()==null ||"".equals(icon.getId())){
			return new JsonResult("内部错误：未指定图标");
		}
		getContent(icon, request);		
		iconService.save(icon);
		
		// 返回json不需要content
		icon.setContent(null);
		return new JsonResult(icon);
	}
	@RequestMapping(params="delete")
	@ResponseBody
	public JsonResult delete(String id){
		if(id==null ||"".equals(id)){
			return new JsonResult("内部错误：未指定图标");
		}
		iconService.delete(id);
		
		return JsonResult.Success;
	}
}
