<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>无权访问</title>
	<style>
		body{line-height:32px;padding:15px 25px}
		.time{font-size:12px;line-height:20px}
		.url{color:#339}
		.err{color:#933;background-color:#ccc;padding:15px;display:inline-block;min-width:240px}
	</style>
</head>
<body>
	您正在访问：<span class="url">${origUrl}</span>
	<br/>
	但发生了如下错误：
	<br/>
	<div  class="err">${err}</div>
	<br/>
	如有疑问请联系管理员
	<div class="time"><%=new Date().toLocaleString() %></div>
</body>
</html>