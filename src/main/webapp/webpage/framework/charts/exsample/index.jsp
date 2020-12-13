<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!Doctype html>
<html>
<head>
<meta charset="utf-8">
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,sunzcharts"></z:resource>
 <script type="text/javascript" src="plug-in/echarts/echarts.min.js" ></script>
					 <link rel="stylesheet" href="webpage/framework/charts/css/style.css" type="text/css"></link>
					 <script type="text/javascript" src="webpage/framework/charts/js/sunzcharts.jsp" ></script>
<script type="text/javascript" src="plug-in/echarts/theme/dark.js" ></script>
<script type="text/javascript" src="plug-in/echarts/extension/bmap.js" ></script>
 
<!--  -->
        <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/china.js"></script>
        <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/world.js"></script>
       
    
 		<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&amp;ak=ZUONbpqGBsYGXNIYHicvbAbM"></script> 
         

    <title>title</title>
   <style>
   html,body{
   width:100%;
   height:100%;
   margin:0px;
   padding:0px;
   
   }
   </style>
</head>

<body  style="width: 100%;
    overflow-x: hidden; ">
  
 
 
</body>
   <script> 
   
   function randomData() {
	    return Math.round(Math.random()*1000);
	}


   

   option = {
        
       tooltip : {
           trigger: 'item'
       },
       series : [ 
       ]
   };
 
   seriesConfig = [{type:"lines",name:"迁徙"},{type:"scatter",name:"PM2.5",label: {
       normal: {
           formatter: '{1}111111111' 
       } 
   }}];//,seriesConfig:seriesConfig
    sunz.charts.Map.create({parent:"body",width:"100%",height:"100%",option: option,seriesConfig:seriesConfig})
 
   </script> 
   <script>
 
 </script>
</html>