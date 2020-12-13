<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/sunzbase.jsp"%>
<z:resource items="jquery,easyui,sunzui"></z:resource>

<script type="text/javascript" src="webpage/framework/query/queryEdit.js"></script>
<style>
<!--
	.htmlcell{width:100%;height:48px;}
-->
</style>

<script type="text/javascript">
	$(function(){
		var editor= new QueryEditor();
		editor.edit({});
	});
</script>
