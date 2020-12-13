<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html  >
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzmobile,requirejs"></z:resource>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,
initial-scale=1.0,
maximum-scale=1.0,
user-scalable=no">

    <title>star</title>
  	<style type="text/css" rel="stylesheet">
		html,body{
		width:100%;
		height:100%;
		margin:0px;
		padding:0px; 
		}
		
	</style>
</head>

<body  style="  overflow-x: hidden;">
  

 
</body>
   <script> 
    c=sunz.MStar.create({
    	parent:"body",
    	label:"评价",
    	value:4,
    	//readOnly:true
    });
   
   </script> 
   <script>
 
 </script>
</html>