<%@ page language="java" contentType="application/javascript; charset=UTF-8"
	import="java.util.*,java.io.*"  %>
var RongIMToken =   '${not empty imtoken ?imtoken:"1"}'; 
var RongIMUerInfo = ${not empty imuser ?imuser:"{}"};
<% 
String fileType = "rongim";



//JS文件
String encoding = "UTF-8";
String filepath = "\\webpage\\framework\\im\\rongcloud\\js\\";
String[] fileNames = { 
	"../lib/RongIMLib-2.3.0.js","../lib/RongCallLib-2.3.0.min.js" ,"../lib/RongEmoji-2.2.4.min.js","../lib/RongIMVoice-2.2.4.min.js","js_check.js","im.js"
	,"imfuns.js"
	
	,"imend.js"
};
String rfileName = request.getParameter("f");
boolean  loadByRequest = false;
if(rfileName == null || rfileName.equals("")){
	loadByRequest = false;
}else{
	loadByRequest = true;
	rfileName = "";
	 
	String[] files = rfileName.split(",");
	for(int i=0;i<files.length;i++){
		String str = files[i];
		str = str + ".js";
		files[i] = str;
	}
	fileNames = files;
}

boolean isReadFromFile = false;
long lastModify = 0;
if(application.getAttribute(fileType)!= null){
 
	
	if(application.getAttribute(fileType + "lastModify")!= null){
		Object lastModifyo =  application.getAttribute(fileType + "lastModify");
		lastModify = Long.valueOf(String.valueOf(lastModifyo));
	}
}
%>
<%
//if(isReadFromFile){
	
	ArrayList<String> list  = new ArrayList<String>();
	for (String fileName: fileNames) {
	    try {
	        //String fileName = "sunz.ui.m.js";
	        ServletContext s1 = this.getServletContext();
	        String path = s1.getRealPath(filepath + fileName);
	        // System.out.println(path); 
	        File file = new File(path);
	        long thislastModified = file.lastModified();
	        if(thislastModified>lastModify){
	        	isReadFromFile = true;
	        	if(!loadByRequest){
	        		application.setAttribute(fileType + "lastModify", thislastModified);
	    	 	}
	        	break;
	        } 
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	    }
	}
 	if(loadByRequest){
 		isReadFromFile = true;
 	}
 
	if(isReadFromFile){
		System.out.println("生成缓存" + fileType );
		ServletContext s1 = this.getServletContext();
		for (String fileName: fileNames) {
			String path = s1.getRealPath(filepath + fileName);
			try {
		        //String fileName = "sunz.ui.m.js";
		       
		        
		        // System.out.println(path); 
		        File file = new File(path);
		        long thislastModified = file.lastModified(); 
		        if (file.isFile() && file.exists()) { //判断文件是否存在
		            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); //考虑到编码格式
		            BufferedReader bufferedReader = new BufferedReader(read);
		            String lineTxt = null;
		            String tline = "\r\n\r\n/************************************************************\r\n*\r\n* includefile : " + fileName + "\r\n*\r\n************************************************************/";
		            
		            //out.println(tline);
		            list.add(tline);
		            int rowNum = 0;
		            while ((lineTxt = bufferedReader.readLine()) != null) {
		                //System.out.println(lineTxt);
		                rowNum = rowNum+1;
		                //out.println("/**  **/");
		                //out.println(lineTxt); 
		                list.add(lineTxt);
		            }
		            read.close();
		        } else {
		            System.out.println("找不到指定的文件:"+path);
		        }
		    } catch (Exception e) {
		        System.out.println("读取文件内容出错:"+path);
		    }
		}
		if(!loadByRequest){
			application.setAttribute(fileType  , list);
	 	}else{
	 		application.setAttribute(fileType  , null); 
	 	}
		
	}else{
		System.out.println("使用缓存 " + fileType );
		list = (ArrayList<String>)application.getAttribute(fileType );
	}
	for( String str: list){
		out.println(str); 
	}
%>


