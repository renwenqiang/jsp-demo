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

    <title>calender</title>
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
    c=sunz.MCalendar.create({
    	parent:"body"
    });
    
    btn = sunz.MButton.create({
    	parent:"body",
    	text:"选择日期",
    	handler: function(){
    		c.selectYearAndMonth()
    	}
    })
    btn = sunz.MButton.create({
    	parent:"body",
    	text:"收起",
    	handler: function(){
    		c.shrink()
    	}
    })
    btn = sunz.MButton.create({
    	parent:"body",
    	text:"展开",
    	handler: function(){
    		c.expand()
    	}
    })
   </script> 
   <script>
 
 </script>
</html>