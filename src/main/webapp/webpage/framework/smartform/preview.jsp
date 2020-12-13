<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>
<z:dict items=""></z:dict>
<style type="text/css">
<!--
body{margin:2px;}
div.proxy-container{width:100%;height:100%;}
.autoSize,div.proxy-select {display:inline-block;*display:inline;*zoom:1; }
.fit{width:100% !important;height:100% !important;}
-->
</style>
</head>
<body></body>
</html>
<script type="text/javascript" src="webpage/framework/smartform/interface.js"></script>
<script type="text/javascript" src="webpage/framework/smartform/components/defines.js"></script>
<c:forEach items="${exdefines}" var="exdefine"><script type="text/javascript" src="webpage/framework/smartform/components/exdefines/${exdefine}"></script>
</c:forEach>
<script type="text/javascript">
	$(function(){
		if(window.master){
			var def=window.master.parseDesigner(true);
			//reviveFunctionInJson(def);
			parsePart(def,document.body);
		}
		/* window.designs=location.href.substr(location.href.indexOf("designs")+8);
		 var designs=$.decode(window.designs);
		parsePart(designs,viewport);*/
	});
</script>