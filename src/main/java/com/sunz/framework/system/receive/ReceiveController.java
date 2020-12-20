package com.sunz.framework.system.receive;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.jeecgframework.core.common.service.CommonService;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.GuidGenerator;
import com.sunz.framework.core.JsonHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import com.sunz.framework.core.JsonResult;

@Controller
@RequestMapping("framework/receive")
public class ReceiveController {

	String uploadRootPath = Config.get("receive.uploadRoot");
	String downloadRootPath = Config.get("receive.downloadRoot");
	
	@Autowired
	protected CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	
	@RequestMapping(params="getReceiveTemp")
	@ResponseBody 
	public JsonResult getReceiveTemp(String jobkey){
		try{
			List<Map<String,Object>> receiveData=commonService.findForJdbc("select b.* from T_S_RECEIVE_TEMP a left join T_S_RECEIVE_DEF b on a.receiveid=b.id where a.jobkey=?", jobkey);
			return new JsonResult(receiveData);
		}catch(Exception e){
			return new JsonResult("查询数据出错了，信息："+e.getMessage());
		}
	}
	
	@RequestMapping(params="setting")
	public String setting(){
		return "framework/system/recv/setting";
	}
	
	@RequestMapping(params="imageEdit")
	public ModelAndView imageEdit(String id){
		ModelAndView mv=new ModelAndView("framework/system/recv/ImageEdit");
		mv.addObject("id",id);
		Map<String,Object> data=commonService.findOneForJdbc("select a.* from T_S_RECEIVESCAN_RESOURCE a where a.id= ?",id);
		mv.addObject("imgUrl", data.get("PATH"));
		return mv;
	}
	
	@RequestMapping(params="recvScan")
	public ModelAndView recvScan(String jobid){
		ModelAndView mv=new ModelAndView("framework/system/recv/receives");
		List<Map<String,Object>> receData=commonService.findForJdbc("select a.* from T_S_RECEIVE_INSTANCE a where a.jobid= ? order by a.orderindex",jobid);
		mv.addObject("receiveData", receData);
		List<Map<String,Object>> resourceData=commonService.findForJdbc("select a.* from T_S_RECEIVESCAN_RESOURCE a left join T_S_RECEIVE_INSTANCE b on a.recvid=b.id where b.jobid= ?",jobid);
		mv.addObject("resourceData",resourceData);
		mv.addObject("JOBID",jobid);
		mv.addObject("downloadRootPath", downloadRootPath);
		return mv;
	}
	
	private DateFormat dateFormat=new SimpleDateFormat("yyyy\\MM\\dd\\");
	@RequestMapping(params="upload")
	@ResponseBody
	public JsonResult upload(String data,HttpServletRequest request){
        Date time=new Date();
		 Collection<Map> mapArray = JsonHelper.toCollection(data, Map.class);
		 for(Map m:mapArray) {
            String recvid=m.get("recvid").toString();
            // remove掉，不再返回给前端
            String file=m.remove("file").toString().replace("data:image/gif;base64,", "");
            String name=m.get("filename").toString();    //C:\\aaa\\bbb\\ddd.jpg
            String filename=name.substring(name.lastIndexOf("\\")+1, name.lastIndexOf("."));
            String filetype=name.substring(name.lastIndexOf(".")+1, name.length());
            String saveName=dateFormat.format(time)+ GuidGenerator.getGuid()+"."+filetype;
            boolean flag=GenerateImage(file,uploadRootPath,saveName);
            if(flag){
            	String id=GuidGenerator.getGuid();
            	commonService.executeSql("insert into T_S_RECEIVESCAN_RESOURCE (id,name,type,path,time,recvid) values (?,?,?,?,?,?)",id,filename,filetype,saveName,time,recvid);
            	m.put("ID", id);
            	m.put("filename", downloadRootPath+saveName);
            }else{
            	return new JsonResult("保存文件时出错了");
            }
	     }  
		 return new JsonResult(mapArray);
	}
	
	//base64字符串转化成图片  
    public static boolean GenerateImage(String imgStr,String path,String filename)  
    {   //对字节数组字符串进行Base64解码并生成图片  
        if (imgStr == null) //图像数据为空  
            return false;  
        try   
        {  
            //Base64解码  
            byte[] b = java.util.Base64.getDecoder().decode(imgStr);
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpg图片  
            String imgFilePath = path+filename;//新生成的图片  
        	try {
        		File dir=new File(new File(imgFilePath).getParent());
        		if (!dir.exists()) {
        		   dir.mkdirs();
        		}
        	  } catch (SecurityException e) {
        	   e.printStackTrace();
        	  }
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
            return false;  
        }  
    } 
    
    @RequestMapping(params="deleteRecv")
    @ResponseBody 
	public JsonResult deleteRecv(String id,HttpServletRequest request){
    	List<Map<String,Object>> data=commonService.findForJdbc("select a.* from T_S_RECEIVESCAN_RESOURCE a where recvid= ?",id);
    	if(data!=null){
    		for(int i=0;i<data.size();i++){
    			String path=(String) data.get(i).get("PATH");
    			String filePath=uploadRootPath+path.substring(path.lastIndexOf("/")+1,path.length());
    			if(this.deleteFile(filePath)){
    				commonService.executeSql("delete T_S_RECEIVESCAN_RESOURCE where id= ?",(String) data.get(i).get("ID"));
    			}else{
    				return new JsonResult("操作文件失败，删除失败");
    			}
    		}
    	}
    	int count=commonService.executeSql("delete T_S_RECEIVE_INSTANCE t where t.id= ?",id);
    	return new JsonResult((Object)count);
    }
    
    @RequestMapping(params="deleteScan")
    @ResponseBody 
	public JsonResult deleteScan(String id,HttpServletRequest request){
    	int count=0;
    	Map<String,Object> data=commonService.findOneForJdbc("select a.* from T_S_RECEIVESCAN_RESOURCE a where a.id= ?",id);
    	String path=(String) data.get("PATH");
    	String filePath=uploadRootPath+path.substring(path.lastIndexOf("/")+1,path.length());
		if(this.deleteFile(filePath)){
			count=commonService.executeSql("delete from T_S_RECEIVESCAN_RESOURCE where id=?",id);
		}
    	return new JsonResult((Object)count);
    }
    
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     */  
    public boolean deleteFile(String sPath) {  
        boolean flag = false;  
        File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            flag = true;  
        }  
        return flag;  
    }
    
    /**
	 * 流程收件列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "select")
	public ModelAndView processReceive(HttpServletRequest request) {
		String processkey = request.getParameter("processkey");
		request.setAttribute("processkey", processkey);
		return new ModelAndView("framework/system/recv/processReceive");
	}
	
	/**
	 * 删除配置
	 * 
	 * @return
	 */
	@RequestMapping(params = "deleteReceiveTemp")
	@ResponseBody
	public JsonResult deleteReceiveTemp(HttpServletRequest request) {
		String processkey = request.getParameter("jobkey");
		try{
			Integer coum=commonService.executeSql("delete from T_S_RECEIVE_TEMP where JOBKEY=?", processkey);
			return new JsonResult(coum);
		}catch(Exception e){
			return new JsonResult("删除数据出错了，信息："+e.getMessage());
		}
		
	}
	
	/**
	 * 添加配置
	 * 
	 * @return
	 */
	@RequestMapping(params = "addReceiveTemp")
	@ResponseBody
	public JsonResult addReceiveTemp(String jobkey,String receiveid,String index) {
		int dex="null".equals(index)?0:Integer.parseInt(index);
		try{
			Integer coum=commonService.executeSql("insert into T_S_RECEIVE_TEMP (jobkey,receiveid,orderindex) values (?,?,?)", jobkey,receiveid,dex);
			return new JsonResult(coum);
		}catch(Exception e){
			return new JsonResult("添加数据出错了，信息："+e.getMessage());
		}
	}
    
}
