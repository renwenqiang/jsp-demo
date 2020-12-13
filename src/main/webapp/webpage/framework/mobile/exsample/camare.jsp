<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile,requirejs"></z:resource>
<z:dict items="all"></z:dict>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

<title>list</title>

 
</head>
  
 <body onload="onLoad()">
  <a href="tel:10086">电话号码</a>
  <a href="sms:10086">短信</a>
  
</body>
<script>
function onLoad() {          
    document.addEventListener("deviceready", onDeviceReady, false);     
     }      
//device APIs are available    //      
function onDeviceReady() {          
// Register the event listener          
document.addEventListener("menubutton", onMenuKeyDown, false);      
}      
// Handle the menu button    //      
function onMenuKeyDown() {   
    //菜单按钮的执行  
alert(1)
   }    
</script>
</html>