<%@ page language="java" contentType="application/javascript; charset=UTF-8"
	import="java.util.*,java.io.*"  %>
<% 
 
String encoding = "UTF-8";
String[] fileNames = { 
		 "validate.js","sunz.ui.mobile.js",  "swipe.extend.js","textbox.js","form.js","combobox.js","dictcombobox.js","listitem.js","list.js"
    ,"panel.js","button.js","switch.js","switchgroup.js","textarea.js","filebox.js","window.js"
    ,"carousel.js","navhead.js","nav.js","splitbar.js","searchlist.js","menu.js"
    ,"tabitem.js","tab.js" ,"progress.js","sliderbar.js", "imageView.js", "radio.js"
    ,"activebottom.js","calendar-plugin.js","calendar.js","star.js","pay.js"
    ,"apiend.js"
};
String rfileName = request.getParameter("f");
boolean  loadByRequest = false;
if(rfileName == null || rfileName.equals("")){
	loadByRequest = false;
}else{
	loadByRequest = true;
	rfileName = "validate," + "sunz.ui.mobile," + rfileName + ",apiend";
	 
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
if(application.getAttribute("mobileuijs")!= null){
 
	
	if(application.getAttribute("mobileuijslastModify")!= null){
		Object lastModifyo =  application.getAttribute("mobileuijslastModify");
		lastModify = Long.valueOf(String.valueOf(lastModifyo));
	}
}
%>
<%
//if(isReadFromFile){
	String BasePath = "/webpage/framework/mobile/js/" ;
	ArrayList<String> list  = new ArrayList<String>();
	for (String fileName: fileNames) {
	    try {
	        //String fileName = "sunz.ui.m.js";
	        ServletContext s1 = this.getServletContext();
	        String path = s1.getRealPath(BasePath + fileName);
	        // System.out.println(path); 
	        File file = new File(path);
	        long thislastModified = file.lastModified();
	        if(thislastModified>lastModify){
	        	isReadFromFile = true;
	        	if(!loadByRequest){
	        		application.setAttribute("mobileuijslastModify", thislastModified);
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
 	//isReadFromFile = true;
	if(isReadFromFile){
		//System.out.println("生成缓存mobileuijs1");
		ServletContext s1 = this.getServletContext();
		for (String fileName: fileNames) {
			String path = s1.getRealPath(BasePath + fileName);
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
			application.setAttribute("mobileuijs" , list);
	 	}else{
	 		application.setAttribute("mobileuijs" , null); 
	 	}
		
	}else{
		//System.out.println("使用缓存mobileuijs");
		list = (ArrayList<String>)application.getAttribute("mobileuijs");
	}
	for( String str: list){
		out.println(str); 
	}
%>

