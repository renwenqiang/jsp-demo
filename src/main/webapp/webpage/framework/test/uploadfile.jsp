<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
  

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
 	<%@include file="/sunzbase.jsp"%>
    <z:resource items="jquery"></z:resource>
    <title>My JSP 'uploadfile.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script src="plug-in/jquery-plugs/uploadwithoutui/ajaxfileupload.js">
	</script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script>
	url = "framework/attachment.do?uploadFile&businesskey=1111";
	// url="http://172.16.1.36:8082/ytccpsys/ytdj/djFjController.do?upload"
	function uploadfile(){
		var request = sunzUpload.uploadFile("fff",{a:1},url,
				function(){
			console.log(arguments)
		},null,function(e){
			var pernum = e.loaded/e.total;
			console.log(pernum)
			$("#per").html(pernum*100+"%");
		})
		setTimeout(function(){
			 request.abort();
		},200)
		
		
	}
	
	</script>

  </head>
  
  <body>
      <input id="fff" type="file" name="file" multiple="multiple"></input>
    <button onclick="uploadfile()" value="提交">提交</button> 
    <div id="per">0</div>
  </body>
</html>
