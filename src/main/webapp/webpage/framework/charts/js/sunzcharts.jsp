<%@ page language="java" contentType="application/javascript; charset=UTF-8"
	import="java.util.*,java.io.*"  %>
<% 
 
String encoding = "UTF-8";
String[] fileNames = {
    "sunz.ui.charts.js","chartpanel.js","premap.js","map.js"

    ,"apiend.js"
};
out.write("if(!window.sunz){ sunz = { } ;};if(!sunz.charts){sunz.charts={}};");
for (String fileName: fileNames) {
    try {
        //String fileName = "sunz.ui.m.js";
        ServletContext s1 = this.getServletContext();
        String path = s1.getRealPath("/webpage/framework/charts/js/" + fileName);
        File file = new File(path);
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); //考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            out.println("\r\n\r\n/************************************************************\r\n*\r\n* includefile : " + fileName + "\r\n*\r\n************************************************************/");
            int rowNum = 0;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                //System.out.println(lineTxt);
                rowNum = rowNum+1;
                //out.println("/**  **/");
                out.println(lineTxt); 
            }
            read.close();
        } else {
            System.out.println("找不到指定的文件");
        }
    } catch (Exception e) {
        System.out.println("读取文件内容出错");
    }
}
 


%>

